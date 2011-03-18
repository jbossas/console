package org.jboss.as.console.client.shared.model;

/**
 * @author Heiko Braun
 * @date 1/31/11
 */
public interface DeploymentRecord  {

    public String getName();
    public void setName(String name);

    public String getRuntimeName();
    public void setRuntimeName(String runtimeName);

    public String getSha();
    public void setSha(String sha);

    public String getServerGroup();
    public void setServerGroup(String groupName);
}
