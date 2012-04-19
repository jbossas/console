package org.jboss.as.console.client.shared.general.model;

import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * @author Heiko Braun
 * @date 4/19/12
 */
public interface RemoteSocketBinding {

    @Binding(skip = true)
    String getName();
    void setName(String name);

    int getPort();
    void setPort(int port);

    String getHost();
    void setHost(String name);

    @Binding(detypedName = "source-interface")
    String getSourceInterface();
    void setSourceInterface(String name);

    @Binding(detypedName = "source-port")
    int getSourcePort();
    void setSourcePort(int port);

    @Binding(detypedName = "fixed-source-port")
    boolean isFixedSourcePort();
    void setFixedSourcePort(boolean b);
}
