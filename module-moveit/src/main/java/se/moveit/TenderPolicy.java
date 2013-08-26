package se.moveit;

import se.moveit.membership.SiteUserDataPolicy;

import com.atex.plugins.baseline.policy.BaselinePolicy;
import com.google.common.base.Optional;
import com.polopoly.cm.ContentId;
import com.polopoly.cm.app.inbox.InboxFlags;
import com.polopoly.cm.app.policy.BooleanValuePolicy;
import com.polopoly.cm.app.policy.SingleReference;
import com.polopoly.cm.app.policy.SingleValued;
import com.polopoly.cm.client.CMException;

public class TenderPolicy extends BaselinePolicy {
    
    public void storeTender(String tenderId, PriceInquiry priceInquiry, int price, Optional<SiteUserDataPolicy> userdataPolicy) throws CMException {
        setValue("name", tenderId);
        setValue("tenderId", tenderId);
        setValue("fromAddress", priceInquiry.fromAddress);
        setValue("toAddress", priceInquiry.toAddress);
        setValue("distance", priceInquiry.distance);
        setValue("houseArea", priceInquiry.houseArea);
        setValue("otherArea", priceInquiry.otherArea);
        setValue("piano", priceInquiry.hasPiano);
        setValue("packaging", priceInquiry.packaging);
        setValue("price", price);
        if (userdataPolicy.isPresent()) {
            setReference("client", userdataPolicy.get().getContentId().getContentId());
        }
    }
    
    private void setValue(String field, boolean value) throws CMException {
        ((BooleanValuePolicy) getChildPolicy(field)).setBooleanValue(value);
    }
    
    private void setValue(String field, int value) throws CMException {
        setValue(field, Integer.toString(value));
    }
    
    private void setValue(String field, String value) throws CMException {
        ((SingleValued) getChildPolicy(field)).setValue(value);
    }
    
    private void setReference(String field, ContentId contentId) throws CMException {
        ((SingleReference) getChildPolicy(field)).setReference(contentId);
    }
    
    public void postCreate() throws CMException {
        super.postCreate();

        new InboxFlags().setShowInInbox(this, true);
    }
}
