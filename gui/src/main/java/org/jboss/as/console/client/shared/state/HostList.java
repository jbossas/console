package org.jboss.as.console.client.shared.state;

import org.jboss.as.console.client.domain.model.Host;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 12/10/12
 */
public class HostList {

    private Host selectedHost;
    private List<Host> hosts;

    public HostList(Host selectedHost, List<Host> hosts) {
        assert selectedHost!=null;
        this.selectedHost = selectedHost;
        this.hosts = hosts;
    }

    public Host getSelectedHost() {
        return selectedHost;
    }

    public List<Host> getHosts(){
        return hosts;
    }

    public boolean isEmpty() {
        return hosts.isEmpty();
    }
}
