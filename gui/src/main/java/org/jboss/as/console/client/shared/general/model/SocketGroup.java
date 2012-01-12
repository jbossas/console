package org.jboss.as.console.client.shared.general.model;

import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * @author Heiko Braun
 * @date 1/12/12
 */
@Address("/socket-binding-group={0}")
public interface SocketGroup {

    String getName();
    void setName(String name);

    @Binding(detypedName = "default-interface")
    String getDefaultInterface();
    void setDefaultInterface(String name);

}
