package se.moveit.membership;

import java.rmi.RemoteException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Optional;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.app.policy.BooleanValuePolicy;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.cm.policy.UserDataPolicy;
import com.polopoly.user.server.UserId;
import com.polopoly.user.server.jsp.UserFactory;

import example.membership.UserDataHandlerImpl;

public class MembershipUtil {

    public static boolean isSiteUserLoggedIn(PolicyCMServer cmServer, UserCaller userCaller) throws MembershipException, CMException {
        try {
            boolean isSiteUser = !isCMUser(cmServer, getUserId(userCaller));
            boolean isLoggedIn = userCaller.getUser().isLoggedIn(userCaller.getCaller(), 10000);

            return isSiteUser && isLoggedIn;
        } catch (RemoteException e) {
            throw new MembershipException("Could not determine user logged in status", e);
        }
    }
    
    public static SiteUserDataPolicy getSiteUserData(PolicyCMServer cmServer, UserCaller userCaller) throws MembershipException, CMException {
        try {
            return new UserDataHandlerImpl(cmServer).getUserData(getUserId(userCaller));
        } catch (RemoteException e) {
            throw new MembershipException("Could not retrieve site user data", e);
        }
    }
    
    public static Optional<UserCaller> getUserCallerIfPresent(HttpServletRequest request) throws MembershipException {
        try {
            Object[] obj = UserFactory.getInstance().getUserAndCallerIfPresent(request, null);
            return UserCaller.getInstance(obj);
        } catch (ServletException e) {
            throw new MembershipException("Could not retrieve user and caller", e);
        }
    }

    private static UserId getUserId(UserCaller userCaller) throws RemoteException {
        return userCaller.getUser().getUserId();
    }

    private static boolean isCMUser(PolicyCMServer cmServer, UserId userId) throws CMException {
        UserDataPolicy userdataPolicy =
                (UserDataPolicy) cmServer.getPolicy(
                        new ExternalContentId(userId.getPrincipalIdString()));
        
        return ((BooleanValuePolicy) userdataPolicy.getChildPolicy("IsCMUser")).getBooleanValue();
    }
}

