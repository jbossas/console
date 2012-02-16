package org.jboss.as.console.client.shared.subsys.jgroups;

import org.jboss.as.console.client.widgets.forms.Binding;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/16/12
 */
public interface JGroupsStack {
    String getName();
    void setName(String name);

    @Binding(skip = true)
    List<JGroupsProtocol> getProtocols();
    void setProtocols(List<JGroupsProtocol> protocols);

    @Binding(skip = true)
    JGroupsTransport getTransport();
    void setTransport(JGroupsTransport transport);
}
