package org.jboss.as.console.client.shared.subsys.jca.model;

import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 12/12/11
 */
@Address("/subsystem=resource-adapters/resource-adapter={0}/connection-definitions={1}")
public interface ConnectionDefinition {

    @Binding(detypedName = "jndi-name")
    String getJndiName();
    void setJndiName(String name);

    @Binding(detypedName = "class-name")
    String getConnectionClass();
    void setConnectionClass(String classname);

    void setEnabled(boolean enabled);
    boolean isEnabled();

    @Binding(detypedName = "security-domain")
    String getSecurityDomain();
    void setSecurityDomain(String domain);

    @Binding(detypedName = "security-application")
    String getApplication();
    void setApplication(String application);

    @Binding(detypedName = "security-domain-and-application")
    String getDomainAndApplication();
    void setDomainAndApplication(String archive);

    @Binding(detypedName = "background-validation")
    boolean isBackgroundValidation();
    void setBackgroundValidation(boolean b);

    @Binding(detypedName = "background-validation-millis")
    Long getBackgroundValidationMillis();
    void setBackgroundValidationMillis(Long millis);

    @Binding(skip = true)
    List<PropertyRecord> getProperties();
    void setProperties(List<PropertyRecord> props);

    @Binding(skip = true)
    PoolConfig getPoolConfig();
    void setPoolConfig(PoolConfig pool);
}
