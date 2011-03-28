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

    boolean isAutoStart();
    void setAutoStart(boolean b);

    boolean isStarted();
    void setStarted(boolean b);

    String getSocketBinding();
    void setSocketBinding(String socketBindingRef);

    int getPortOffset();
    void setPortOffset(int offset);

    String getJvm();
    void setJvm(String jvm);
}
