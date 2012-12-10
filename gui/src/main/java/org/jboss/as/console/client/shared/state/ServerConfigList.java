package org.jboss.as.console.client.shared.state;

import org.jboss.as.console.client.domain.model.Server;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 12/10/12
 */
public class ServerConfigList {

    private Server selectedServer;
    private List<Server > server;

    public ServerConfigList (Server  selectedServer, List<Server > server) {
        assert selectedServer!=null;
        this.selectedServer= selectedServer;
        this.server = server;
    }

    public Server  getSelectedServer() {
        return selectedServer;
    }

    public List<Server > getServer() {
        return server;
    }
}