package org.jboss.as.console.client.shared.subsys.jms.model;

/**
 * @author Heiko Braun
 * @date 3/29/11
 */
public interface JMSEndpoint {
    String getName();
    void setName(String name);

    String getJndiName();
    void setJndiName(String jndi);
}
