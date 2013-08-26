package se.moveit;

import static se.moveit.membership.MembershipUtil.getSiteUserData;
import static se.moveit.membership.MembershipUtil.getUserCallerIfPresent;
import static se.moveit.membership.MembershipUtil.isSiteUserLoggedIn;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import se.moveit.membership.MembershipException;
import se.moveit.membership.SiteUserDataPolicy;
import se.moveit.membership.UserCaller;

import com.google.common.base.Optional;
import com.polopoly.cm.ContentId;
import com.polopoly.cm.ContentReference;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.model.Model;
import com.polopoly.render.CacheInfo;
import com.polopoly.render.RenderRequest;
import com.polopoly.siteengine.dispatcher.ControllerContext;
import com.polopoly.siteengine.model.TopModel;
import com.polopoly.siteengine.mvc.RenderControllerBase;

import example.content.PolicyCMServerProviderFromContext;
import example.util.ModelDomainUtil;

public class RenderControllerTenderListElement extends RenderControllerBase {
    private static final Logger LOG = Logger.getLogger(RenderControllerTenderListElement.class.getName());
    protected PolicyCMServerProviderFromContext _policyCMServerProvider =
            new PolicyCMServerProviderFromContext();

    @Override
    public void populateModelAfterCacheKey(RenderRequest request, TopModel m, CacheInfo cacheInfo, ControllerContext context) {
        super.populateModelAfterCacheKey(request, m, cacheInfo, context);
        try {
            PolicyCMServer cmServer =  _policyCMServerProvider.getPolicyCMServer(context);
            ModelDomainUtil modelUtil = new ModelDomainUtil();
            List<Model> tenderList = null; // Model is the output template representation of a content
            boolean isLoggedIn = false;
            
            // fetch tenderList
            Optional<UserCaller> userCaller = getUserCallerIfPresent((HttpServletRequest) request);
            if (userCaller.isPresent()) {
                isLoggedIn |= isSiteUserLoggedIn(cmServer, userCaller.get());
                
                if (isLoggedIn) {
                    tenderList = getTenderList(modelUtil, getSiteUserData(cmServer, userCaller.get()));
                }
            }

            // populate velocity variables
            m.getLocal().setAttribute("isLoggedIn", isLoggedIn);
            m.getLocal().setAttribute("tenderList", tenderList);

        } catch (CMException e) {
            LOG.log(Level.WARNING, "Tender list element: Could not successfully populate model.", e);
        } catch (MembershipException e) {
            LOG.log(Level.WARNING, "Tender list element: Could not successfully populate model.", e);
        }
    }
    
    
    private List<Model> getTenderList(ModelDomainUtil modelUtil, SiteUserDataPolicy userdataPolicy) throws CMException {
        List<Model> tenderList = new ArrayList<Model>();
        ListIterator<ContentReference> iter = userdataPolicy.getTenderList().getListIterator();

        while (iter.hasNext()) {
            ContentId tenderId = iter.next().getReferredContentId();

            try {
                tenderList.add(
                        modelUtil.getModel(tenderId)
                        );
            } catch (CMException e) {
                LOG.log(Level.WARNING, "Userdata " + userdataPolicy.getExternalId() + " could not fetch tender " 
                        + tenderId.getContentId().getContentIdString(), e);
            }
        }

        return tenderList;
    }
}
