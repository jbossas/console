package org.jboss.as.console.client.auth;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.gwtplatform.mvp.client.proxy.Gatekeeper;

@Singleton
public class LoggedInGatekeeper implements Gatekeeper {

    private final CurrentUser currentUser;

    @Inject
    public LoggedInGatekeeper (final CurrentUser currentUser ) {
        this.currentUser = currentUser;
    }

    @Override
    public boolean canReveal() {
        return currentUser.isLoggedIn();
    }
}