package se.moveit.membership;

import com.google.common.base.Optional;
import com.polopoly.user.server.Caller;
import com.polopoly.user.server.User;

public final class UserCaller {
    private final User user;
    private final Caller caller;

    private UserCaller(User user, Caller caller) {
        this.user = user;
        this.caller = caller;
    }

    static Optional<UserCaller> getInstance(Object[] userCaller) {
        if (userCaller != null 
                && userCaller.length > 1 
                && userCaller[0] != null 
                && userCaller[1] != null) { 
            
            return Optional.of(new UserCaller((User) userCaller[0], (Caller) userCaller[1]));
        } else {

            return Optional.<UserCaller> absent();
        }
    }
    
    public User getUser() {
        return user;
    }
    
    public Caller getCaller() {
        return caller;
    }
}
