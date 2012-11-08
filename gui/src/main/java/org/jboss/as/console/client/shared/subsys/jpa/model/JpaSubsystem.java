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

    @Binding(detypedName = "default-extended-persistence-inheritance")
    String getInheritance();
    void setInheritance(String name);

    @Binding(detypedName = "default-vfs")
    boolean isDefaultVfs();
    void setDefaultVfs(boolean isDefault);

}
