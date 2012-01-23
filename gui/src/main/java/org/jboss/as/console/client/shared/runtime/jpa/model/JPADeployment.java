package org.jboss.as.console.client.shared.runtime.jpa.model;

/**
 * @author Heiko Braun
 * @date 1/19/12
 */
public interface JPADeployment {

    String getDeploymentName();
    void setDeploymentName(String name);

    String getPersistenceUnit();
    void setPersistenceUnit(String unit);

    void setMetricEnabled(boolean enabled);
    boolean isMetricEnabled();
}
