package org.jboss.as.console.client.shared.state;

import org.jboss.as.console.client.domain.model.ServerInstance;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 12/10/12
 */
public class ServerInstanceList {

    private ServerInstance selectedServer;
    private List<ServerInstance> server;

    public ServerInstanceList(ServerInstance selectedServer, List<ServerInstance> server) {
        assert selectedServer!=null;
        this.selectedServer= selectedServer;
        this.server = server;
    }

    public ServerInstance getSelectedServer() {
        return selectedServer;
    }

    public List<ServerInstance> getServer() {
        return server;
    }

    public boolean isEmpty() {
        return server.isEmpty();
    }
}
