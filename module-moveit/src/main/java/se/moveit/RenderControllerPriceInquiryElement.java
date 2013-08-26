package se.moveit;

import static java.lang.Integer.parseInt;
import static se.moveit.businessrules.Calculator.quote;
import static se.moveit.membership.MembershipUtil.getSiteUserData;
import static se.moveit.membership.MembershipUtil.getUserCallerIfPresent;
import static se.moveit.membership.MembershipUtil.isSiteUserLoggedIn;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import se.moveit.membership.MembershipException;
import se.moveit.membership.SiteUserDataPolicy;
import se.moveit.membership.UserCaller;

import com.google.common.base.Optional;
import com.polopoly.cm.DefaultMajorNames;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.render.CacheInfo;
import com.polopoly.render.RenderRequest;
import com.polopoly.siteengine.dispatcher.ControllerContext;
import com.polopoly.siteengine.model.TopModel;
import com.polopoly.siteengine.mvc.RenderControllerBase;

import example.content.PolicyCMServerProviderFromContext;
import example.util.ModelDomainUtil;

public class RenderControllerPriceInquiryElement extends RenderControllerBase {
    private static final Logger LOG = Logger.getLogger(RenderControllerPriceInquiryElement.class.getName());
    protected PolicyCMServerProviderFromContext _policyCMServerProvider =
            new PolicyCMServerProviderFromContext();

    @Override
    public void populateModelAfterCacheKey(RenderRequest request, TopModel m, CacheInfo cacheInfo, ControllerContext context) {
        super.populateModelAfterCacheKey(request, m, cacheInfo, context);
        try {
            PolicyCMServer cmServer =  _policyCMServerProvider.getPolicyCMServer(context);
            ModelDomainUtil modelDomainUtil = new ModelDomainUtil();
            TenderPolicy tenderPolicy = null;
            SiteUserDataPolicy userdataPolicy = null;
            boolean isLoggedIn = false;
            
            // fetch user information
            Optional<UserCaller> userCaller = getUserCallerIfPresent((HttpServletRequest) request);
            if (userCaller.isPresent()) {
                isLoggedIn |= isSiteUserLoggedIn(cmServer, userCaller.get());
                
                if (isLoggedIn) {
                    userdataPolicy = getSiteUserData(cmServer, userCaller.get());
                }
            }

            // fetch tender information
            if (isPriceInquiry(request)) {
                
                PriceInquiry priceInquiry = buildPriceInquiry(request);
                
                int price = computePrice(priceInquiry);
                
                tenderPolicy = createTender(cmServer, m, priceInquiry, price, Optional.fromNullable(userdataPolicy));
                
                if (userdataPolicy != null) {
                    associateTenderWithUser(cmServer, tenderPolicy, userdataPolicy);
                }
            }

            // populate velocity variables
            m.getLocal().setAttribute("isLoggedIn", isLoggedIn);
            
            if (userdataPolicy != null) {
                m.getLocal().setAttribute("fullName", userdataPolicy.getScreenName());
                m.getLocal().setAttribute("email", userdataPolicy.getEmail());
            }
            
            if (tenderPolicy != null)
                m.getLocal().setAttribute("tender", modelDomainUtil.getModel(tenderPolicy.getContentId()));
            
            
        } catch (NumberFormatException e) {
            LOG.info("Price Inquiry Element: Invalid user input data. " + e.getMessage());
        } catch (CMException e) {
            LOG.log(Level.WARNING, "Price Inquiry Element " + context.getContentId().getContentId().getContentIdString() +
                    ": Could not successfully populate model.", e);
        } catch (MembershipException e) {
            LOG.log(Level.WARNING, "Price Inquiry Element " + context.getContentId().getContentId().getContentIdString() +
                    ": Could not successfully populate model.", e);        }
    }
    
    private boolean isPriceInquiry(RenderRequest request) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        return httpRequest.getMethod().equals("POST");
    }
    
    private int computePrice(PriceInquiry inquiry) {
        return quote(inquiry.houseArea, inquiry.otherArea, inquiry.distance, inquiry.hasPiano);
    }
    
    private PriceInquiry buildPriceInquiry(RenderRequest request) throws NumberFormatException {
        String fromAddress = request.getParameter("inputFrom");
        String toAddress = request.getParameter("inputTo");
        int distance = parseInt(request.getParameter("inputDistance"));
        int houseArea = parseInt(request.getParameter("inputHouseArea"));
        int otherArea = parseInt(request.getParameter("inputOtherArea"));
        boolean hasPiano = "on".equals(request.getParameter("inputPiano"));
        boolean isPackaging = "on".equals(request.getParameter("inputPackaging"));
        
        return new PriceInquiry(
                fromAddress,
                toAddress,
                distance,
                houseArea,
                otherArea,
                hasPiano,
                isPackaging);
    }
    
    private TenderPolicy createTender(PolicyCMServer cmServer, TopModel m, PriceInquiry priceInquiry, int price, 
            Optional<SiteUserDataPolicy> userdataPolicy) throws CMException {
        
        TenderPolicy tenderPolicy = null;
        try {
            tenderPolicy = (TenderPolicy) cmServer.createContent(
                    cmServer.getMajorByName(DefaultMajorNames.ARTICLE), 
                    m.getContext().getPage().getContentPath().getLast(), 
                    new ExternalContentId("moveit.Tender")
                    );

            tenderPolicy.storeTender(generateTenderId(), priceInquiry, price, userdataPolicy);

            cmServer.commitContent(tenderPolicy);
        } catch (CMException e) {
            if (tenderPolicy != null)
                cmServer.abortContent(tenderPolicy);
            throw e;
        }
        
        return tenderPolicy;
    }
    
    private void associateTenderWithUser(PolicyCMServer cmServer, TenderPolicy tenderPolicy, SiteUserDataPolicy userdataPolicy) throws CMException {
        SiteUserDataPolicy newVersion = null;
        try {
            newVersion = (SiteUserDataPolicy) cmServer.createContentVersion(userdataPolicy.getContentId());

            newVersion.addTender(tenderPolicy);

            cmServer.commitContent(newVersion);
        } catch (CMException e) {
            if (newVersion != null)
                cmServer.abortContent(newVersion);
            throw e;
        }
    }
    
    private String generateTenderId() {
        return Long.toString(System.currentTimeMillis());
    }
}
