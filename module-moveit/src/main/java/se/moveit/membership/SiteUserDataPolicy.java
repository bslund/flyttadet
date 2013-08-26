package se.moveit.membership;

import se.moveit.TenderPolicy;

import com.polopoly.cm.ContentReference;
import com.polopoly.cm.app.policy.ContentListWrapperPolicy;
import com.polopoly.cm.app.policy.SingleValued;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.collections.ContentList;
import com.polopoly.cm.policy.ContentPolicy;

/**
 * Policy used to store related data on site users.
 */
public class SiteUserDataPolicy extends ContentPolicy {

    public String getScreenName() throws CMException {
        return getValue("screenName");
    }

    public String getEmail() throws CMException {
        return getValue("email");
    }
    
    public void setScreenName(String screenName) throws CMException {
        setValue("screenName", screenName.trim());
    }

    public void setEmail(String email) throws CMException {
        setValue("email", email.trim());
    }
    
    private void setValue(String field, String value) throws CMException {
        ((SingleValued) getChildPolicy(field)).setValue(value);
    }
    
    private String getValue(String field) throws CMException {
        return ((SingleValued) getChildPolicy(field)).getValue();
    }
    
    public ContentList getTenderList() throws CMException {
        ContentListWrapperPolicy policy = (ContentListWrapperPolicy) getChildPolicy("tenderList");
        return policy.getContentList();
    }
    
    public void addTender(TenderPolicy tenderPolicy) throws CMException {
        getTenderList().add(getTenderList().size(), new ContentReference(tenderPolicy.getContentId().getContentId()));
    }
}
