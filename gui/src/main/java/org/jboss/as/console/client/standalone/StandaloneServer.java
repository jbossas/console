package org.jboss.as.console.client.standalone;

/**
 * @author Heiko Braun
 * @date 6/7/11
 */
public interface StandaloneServer {

    String getName();
    void setName(String name);

    String getReleaseVersion();
    void setReleaseVersion(String version);

    String getReleaseCodename();
    void setReleaseCodename(String codename);

    String getServerState();
    void setServerState(String state);

    String getSocketBinding();
    void setSocketBinding(String socketBinding);

}
