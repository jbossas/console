package org.jboss.as.console.client.auth;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author Heiko Braun
 * @date 2/7/11
 */
public interface AuthenticationListener extends EventHandler {
    void onUserAuthenticated(AuthenticationEvent event);
}
