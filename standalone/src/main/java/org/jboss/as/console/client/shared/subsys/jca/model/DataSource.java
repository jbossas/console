package org.jboss.as.console.client.shared.subsys.jca.model;

/**
 * @author Heiko Braun
 * @date 3/29/11
 */
public interface DataSource {

    String getName();
    void setName(String name);

    String getConnectionUrl();
    void setConnectionUrl(String url);

    String getDriverClass();
    void setDriverClass(String driverClass);

    String getJndiName();
    void setJndiName(String name);

    boolean isEnabled();
    void setEnabled(boolean isEnabled);

    String getUsername();
    void setUsername(String user);

    String getPassword();
    void setPassword(String password);
}
