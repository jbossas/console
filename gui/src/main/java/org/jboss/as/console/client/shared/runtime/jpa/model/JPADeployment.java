package org.jboss.as.console.client.shared.runtime.jpa.model;

import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * @author Heiko Braun
 * @date 1/19/12
 */
public interface JPADeployment {

    @Binding(skip = true)
    String getDeploymentName();
    void setDeploymentName(String name);

    @Binding(skip = true)
    String getPersistenceUnit();
    void setPersistenceUnit(String unit);

    @Binding(detypedName = "enabled")
    boolean isMetricEnabled();
    void setMetricEnabled(boolean enabled);
}
