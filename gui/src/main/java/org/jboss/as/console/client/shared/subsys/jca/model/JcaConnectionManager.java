package org.jboss.as.console.client.shared.subsys.jca.model;

import org.jboss.as.console.client.widgets.forms.Address;

/**
 * @author Heiko Braun
 * @date 11/30/11
 */
@Address("/subsystem=jca/cached-connection-manager=cached-connection-manager")
public interface JcaConnectionManager {
    boolean isDebug();
    void setDebug(boolean b);

    boolean isError();
    void setError(boolean b);
}
