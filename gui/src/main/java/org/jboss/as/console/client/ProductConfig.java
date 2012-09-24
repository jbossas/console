package org.jboss.as.console.client;

/**
 * @author Heiko Braun
 * @date 9/24/12
 */
public interface ProductConfig {

    public enum Profile {JBOSS, EAP}

    Profile getProfile();
    String getProductTitle();
    String getProductVersion();
    String getCoreVersion();
    String getDevHost();
}
