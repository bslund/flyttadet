package example.membership;

import se.moveit.membership.SiteUserDataPolicy;

import com.polopoly.cm.client.CMException;
import com.polopoly.user.server.UserId;

/**
 * Handling of user data, see implementing class for details.
 */
public interface UserDataHandler
{
    public RegistrationFormHandler getFormHandler(String siteIdString)
        throws CMException;
    
    public SiteUserDataPolicy createUserData(UserId userId)
        throws CMException;
    
    public SiteUserDataPolicy getUserData(UserId userId)
        throws CMException;

    public void commitUserData(SiteUserDataPolicy userData)
        throws CMException;
    
}
