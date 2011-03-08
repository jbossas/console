package org.jboss.as.console.client.domain.model;

/**
 * @author Heiko Braun
 * @date 3/3/11
 */
public interface ServerInstance {
    String getName();
    void setName(String name);

    boolean isRunning();
    void setRunning(boolean b);

    String getSever();
    void setServer(String server);
}
