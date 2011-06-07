package org.jboss.as.console.client.standalone;

/**
 * @author Heiko Braun
 * @date 6/7/11
 */
public interface StandaloneServer {

    String getName();
    void setName(String name);

    String getSocketBinding();
    void setSocketBinding(String socketBinding);

}
