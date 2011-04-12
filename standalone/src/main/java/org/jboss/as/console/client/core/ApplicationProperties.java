package org.jboss.as.console.client.core;

/**
 * @author Heiko Braun
 * @date 4/12/11
 */
public interface ApplicationProperties {
    String INITIAL_TOKEN = "initial_token";
    String STANDALONE = "standalone_usage";
    String DOMAIN_API = "domain-api";
    String DEPLOYMENT_API = "add-content";

    void setProperty(String key, String value);

    String getProperty(String key);

    boolean hasProperty(String key);

    void removeProperty(String key);
}
