package org.jboss.as.console.client.shared.subsys.messaging.model;

import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.widgets.forms.Binding;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 4/4/12
 */
public interface Connector {

    @Binding(skip = true)
    String getName();
    void setName(String name);

    @Binding(skip = true)
    ConnectorType getType();
    void setType(ConnectorType type);

    @Binding(detypedName = "socket-binding")
    String getSocketBinding();
    void setSocketBinding(String binding);

    @Binding(detypedName = "factory-class")
    String getFactoryClass();
    void setFactoryClass(String factory);

    @Binding(detypedName = "server-id")
    Integer getServerId();
    void setServerId(Integer serverId);

    @Binding(detypedName = "param", skip = true)
    List<PropertyRecord> getParameter();
    void setParameter(List<PropertyRecord> params);
}
