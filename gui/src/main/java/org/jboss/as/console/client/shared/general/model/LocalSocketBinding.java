package org.jboss.as.console.client.shared.general.model;

import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * @author Heiko Braun
 * @date 4/19/12
 */
public interface LocalSocketBinding {

    @Binding(skip = true)
    String getName();
    void setName(String name);

    @Binding(detypedName = "source-port")
    int getSourcePort();
    void setSourcePort(int port);

    @Binding(detypedName = "source-interface")
    String getSourceInterface();
    void setSourceInterface(String name);

    @Binding(detypedName = "fixed-source-port")
    boolean isFixedSourcePort();
    void setFixedSourcePort(boolean b);

    @Binding(detypedName = "socket-binding-ref")
    String getSocketBinding();
    void setSocketBinding(String name);

}
