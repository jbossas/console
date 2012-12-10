package org.jboss.as.console.client.shared.state;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.domain.model.SimpleCallback;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 12/10/12
 */
public class DomainEntityManager implements
        GlobalHostSelection.HostSelectionListener,
        GlobalServerSelection.ServerSelectionListener {


    private String selectedHost;
    private String selectedServer;

    private final HostInformationStore hostInfo;
    private final EventBus eventBus;

    @Inject
    public DomainEntityManager(HostInformationStore hostInfo, EventBus eventBus) {
        this.hostInfo = hostInfo;
        this.eventBus = eventBus;

        eventBus.addHandler(GlobalHostSelection.TYPE, this);
        eventBus.addHandler(GlobalServerSelection.TYPE, this);
    }

    public void getHosts(final AsyncCallback<HostList> callback) {
        hostInfo.getHosts(new SimpleCallback<List<Host>>() {
            @Override
            public void onSuccess(List<Host> hosts) {
                Host host = getSelectedHost(hosts);
                callback.onSuccess(new HostList(host, hosts));
            }
        });
    }

    public void getServerInstances(String hostName, final AsyncCallback<ServerInstanceList> callback) {
        hostInfo.getServerInstances(hostName, new SimpleCallback<List<ServerInstance>>() {
            @Override
            public void onSuccess(List<ServerInstance> serverInstances) {
                ServerInstance server = getSelectedServerInstance(serverInstances);
                callback.onSuccess(new ServerInstanceList(server, serverInstances));
            }
        });
    }

    public void getServerConfigurations(String hostName, final AsyncCallback<ServerConfigList> callback) {
        hostInfo.getServerConfigurations(hostName, new SimpleCallback<List<Server>>() {
            @Override
            public void onSuccess(List<Server> serverConfigs) {
                Server s = getSelectedServerConfig(serverConfigs);
                callback.onSuccess(new ServerConfigList(s, serverConfigs));
            }
        });
    }

    public String getSelectedHost() {

        if(null==selectedHost)
            throw new IllegalStateException("host should not be null");

        return selectedHost;
    }

    public String getSelectedServer() {

        if(null==selectedServer)
            throw new IllegalStateException("server should not be null");

        return selectedServer;
    }

    @Override
    public void onHostSelection(String hostName) {
        selectedHost = hostName;

        // fire stale model event
        eventBus.fireEvent(new HostSelectionChanged());
    }

    @Override
    public void onServerSelection(ServerInstance server) {
        // replace host selection, server selection has precedence
        selectedHost = server.getHost();
        selectedServer = server.getName();

        // fire stale model
        eventBus.fireEvent(new ServerSelectionChanged());
    }

    private Host getSelectedHost(List<Host> hosts) {
        assert !hosts.isEmpty();

        Host matched = null;

        // match by preselection
        for(Host host : hosts)
        {
            if(host.getName().equals(selectedHost))
            {
                matched = host;
                break;
            }
        }

        // fallback match
        if(null==matched)
            matched = hosts.get(0);

        selectedHost = matched.getName();

        return matched;
    }

    private ServerInstance getSelectedServerInstance(List<ServerInstance> ServerInstances) {
        assert !ServerInstances.isEmpty();


        ServerInstance matched = null;

        // match by preselection
        for(ServerInstance ServerInstance : ServerInstances)
        {
            if(ServerInstance.getName().equals(selectedServer))
            {
                matched = ServerInstance;
                break;
            }
        }

        // fallback match
        if(null==matched)
            matched = ServerInstances.get(0);

        selectedHost = matched.getHost();
        selectedServer = matched.getName();

        return matched;
    }

    private Server getSelectedServerConfig(List<Server> serverConfigs) {
        assert !serverConfigs.isEmpty();


        Server matched = null;

        // match by preselection
        for(Server s : serverConfigs)
        {
            if(s.getName().equals(selectedServer))
            {
                matched = s;
                break;
            }
        }

        // fallback match
        if(null==matched)
            matched = serverConfigs.get(0);

        selectedServer = matched.getName();

        return matched;
    }
}
