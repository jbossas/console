package org.jboss.as.console.client.shared.subsys.jca.model;

import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 7/19/11
 */
@Address("/subsystem=resource-adapters/resource-adapter={0}")
public interface ResourceAdapter {

    @Binding(detypedName= "archive")
    String getName();
    void setName(String name);

    @Binding(detypedName = "transaction-support")
    String getTransactionSupport();
    void setTransactionSupport(String txSupport);

    String getArchive();
    void setArchive(String archive);

    @Binding(skip = true)
    List<PropertyRecord> getProperties();
    void setProperties(List<PropertyRecord> props);

    @Binding(skip = true)
    void setConnectionDefinitions(List<ConnectionDefinition> connections);
    List<ConnectionDefinition> getConnectionDefinitions();
}
