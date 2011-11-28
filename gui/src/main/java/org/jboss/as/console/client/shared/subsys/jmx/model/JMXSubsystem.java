package org.jboss.as.console.client.shared.subsys.jmx.model;

import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * @author Heiko Braun
 * @date 11/28/11
 */
@Address("/subsystem=jmx")
public interface JMXSubsystem {

    @Binding(detypedName = "registry-binding")
    String getRegistryBinding();
    void setRegistryBinding(String binding);

    @Binding(detypedName = "server-binding")
    String getServerBinding();
    void setServerBinding(String binding);

    @Binding(detypedName = "show-model")
    boolean isShowModel();
    void setShowModel(boolean b);

}
