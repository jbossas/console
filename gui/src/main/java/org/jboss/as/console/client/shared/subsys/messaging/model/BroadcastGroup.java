package org.jboss.as.console.client.shared.subsys.messaging.model;

import org.jboss.as.console.client.widgets.forms.Binding;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 4/18/12
 */
public interface BroadcastGroup {

    @Binding(skip = true)
    String getName();
    void setName(String name);

    @Binding(detypedName = "broadcast-period")
    Long getBroadcastPeriod();
    void setBroadcastPeriod(Long period);

    @Binding(detypedName = "connectors", skip = true)
    List<String> getConnectors();
    void setConnectors(List<String> connectors);

    @Binding(detypedName = "socket-binding")
    String getSocketBinding();
    void setSocketBinding(String bidning);
}
