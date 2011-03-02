package org.jboss.as.console.client.domain.model;

/**
 * @author Heiko Braun
 * @date 3/2/11
 */
public interface Server {
    String getName();
    void setName(String name);
    
    String getGroup();
    void setGroup(String group);

    boolean isStarted();
    void setStarted(boolean b);
}
