package org.jboss.as.console.client.domain.topology;

import org.jboss.as.console.client.domain.model.ServerInstance;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 8/21/12
 */
public class HostInfo implements Comparable<HostInfo> {
    String name;
    String pid;
    boolean isController;
    boolean status;
    private List<ServerInstance> serverInstances;

    public HostInfo(String name, boolean controller) {
        this.name = name;
        this.isController = controller;
    }

    @Override
    public int compareTo(final HostInfo hostInfo)
    {
        return name.compareTo(hostInfo.name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public boolean isController() {
        return isController;
    }

    public void setController(boolean controller) {
        isController = controller;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setServerInstances(List<ServerInstance> serverInstances) {
        this.serverInstances = serverInstances;
    }

    public List<ServerInstance> getServerInstances() {
        return serverInstances;
    }
}
