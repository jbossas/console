package org.jboss.as.console.client.shared;

/**
 * @author Heiko Braun
 * @date 2/11/11
 */
public interface SubsystemRecord {

    String getToken();
    void setToken(String token);

    String getTitle();
    void setTitle(String title);
}
