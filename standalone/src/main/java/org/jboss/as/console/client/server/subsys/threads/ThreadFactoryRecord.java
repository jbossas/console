package org.jboss.as.console.client.server.subsys.threads;

/**
 * @author Heiko Braun
 * @date 2/9/11
 */
public interface ThreadFactoryRecord {

    String getName();
    void setName(String name);

    String getGroup();
    void setGroup(String group);

    int getPriority();
    void setPriority(int prio);
}