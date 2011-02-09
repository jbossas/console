package org.jboss.as.console.client.auth;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Heiko Braun
 * @date 2/7/11
 */
public class AuthenticationEvent extends GwtEvent<AuthenticationListener> {

    public static final Type TYPE = new Type<AuthenticationListener>();

    private CurrentUser user;

    public AuthenticationEvent(CurrentUser user) {
        super();
        this.user = user;
    }

    @Override
    public Type<AuthenticationListener> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AuthenticationListener authenticationListener) {
        authenticationListener.onUserAuthenticated(this);
    }

    public CurrentUser getUser() {
        return user;
    }

}
