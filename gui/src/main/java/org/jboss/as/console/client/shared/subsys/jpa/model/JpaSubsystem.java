package org.jboss.as.console.client.shared.subsys.jpa.model;

import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * @author Heiko Braun
 * @date 11/28/11
 */
@Address("/subsystem=jpa")
public interface JpaSubsystem {

    @Binding(detypedName = "default-datasource")
    String getDefaultDataSource();
    void setDefaultDataSource(String name);
}
