package org.jboss.as.console.client.domain.overview;

import org.jboss.as.console.client.domain.model.ServerInstance;

/**
 * @author Heiko Braun
 * @date 8/22/12
 */
public class ServerPanelReference {
    String hostName;
    ServerInstance server;
    String serverPanelId;
    String toolBoxId;

    public ServerPanelReference(String hostName, ServerInstance server, String toolBoxId, String serverPanelId) {
        this.hostName = hostName;
        this.server = server;
        this.serverPanelId = serverPanelId;
        this.toolBoxId = toolBoxId;
    }

    public String getHostName() {
        return hostName;
    }

    public ServerInstance getServer() {
        return server;
    }

    public String getServerPanelId() {
        return serverPanelId;
    }

    public String getToolBoxId() {
        return toolBoxId;
    }

    public void updateDomReferences(String serverPanelId, String toolBoxId)
    {
        this.toolBoxId = toolBoxId;
        this.serverPanelId = serverPanelId;
    }

    public boolean hasDomReferences() {
        return this.serverPanelId!=null && this.toolBoxId!=null;
    }
}
