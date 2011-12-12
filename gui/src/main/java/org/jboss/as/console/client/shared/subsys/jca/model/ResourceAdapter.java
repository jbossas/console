package org.jboss.as.console.client.shared.subsys.jca.model;

import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;

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

    @Binding(detypedName = "pool-name")
    String getPoolName();
    void setPoolName(String name);

    // regular DS attributes below

    @Binding(detypedName = "class-name")
    String getConnectionClass();
    void setConnectionClass(String classname);

    @Binding(detypedName = "transaction-support")
    String getTransactionSupport();
    void setTransactionSupport(String txSupport);

    String getArchive();
    void setArchive(String archive);

    @Binding(skip = true)
    List<PropertyRecord> getProperties();
    void setProperties(List<PropertyRecord> props);

    // connection definition attributes

    @Binding(detypedName = "jndi-name")
    String getJndiName();
    void setJndiName(String name);

    @Binding(skip = true)
    void setEnabled(boolean enabled);
    boolean isEnabled();

    @Binding(skip=true, detypedName = "security-domain")
    String getSecurityDomain();
    void setSecurityDomain(String domain);

    @Binding(skip = true, detypedName = "security-application")
    String getApplication();
    void setApplication(String application);

    @Binding(skip = true, detypedName = "security-domain-and-application")
    String getDomainAndApplication();
    void setDomainAndApplication(String archive);

    @Binding(skip = true, detypedName = "background-validation")
    boolean isBackgroundValidation();
    void setBackgroundValidation(boolean b);

    @Binding(skip = true, detypedName = "background-validation-millis")
    long getBackgroundValidationMillis();
    void setBackgroundValidationMillis(long millis);
}
