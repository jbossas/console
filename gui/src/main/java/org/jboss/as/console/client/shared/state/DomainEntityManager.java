package org.jboss.as.console.client.shared.state;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 12/10/12
 */
public class DomainEntityManager implements
        GlobalHostSelection.HostSelectionListener,
        GlobalServerSelection.ServerSelectionListener, StaleGlobalModel.StaleModelListener {


    private String selectedHost;
    private String selectedServer;

    private final HostInformationStore hostInfo;
    private final EventBus eventBus;
    private final BeanFactory factory;

    @Inject
    public DomainEntityManager(HostInformationStore hostInfo, EventBus eventBus, BeanFactory factory) {
        this.hostInfo = hostInfo;
        this.eventBus = eventBus;
        this.factory = factory;

        eventBus.addHandler(GlobalHostSelection.TYPE, this);
        eventBus.addHandler(GlobalServerSelection.TYPE, this);
        eventBus.addHandler(StaleGlobalModel.TYPE, this);
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

                if(serverInstances.isEmpty())
                {
                    ServerInstance blank = factory.serverInstance().as();
                    blank.setHost("not-set");
                    blank.setName("not-set");
                    callback.onSuccess(new ServerInstanceList(blank, Collections.EMPTY_LIST));
                }
                else
                {
                    ServerInstance server = getSelectedServerInstance(serverInstances);
                    callback.onSuccess(new ServerInstanceList(server, serverInstances));
                }
            }
        });
    }

    public void getServerConfigurations(String hostName, final AsyncCallback<ServerConfigList> callback) {
        hostInfo.getServerConfigurations(hostName, new SimpleCallback<List<Server>>() {
            @Override
            public void onSuccess(List<Server> serverConfigs) {

                if (serverConfigs.isEmpty()) {
                    // no server at all on this host
                    Server blank = factory.server().as();
                    blank.setName("not-set");
                    callback.onSuccess(new ServerConfigList(blank, Collections.EMPTY_LIST));
                } else {
                    Server s = getSelectedServerConfig(serverConfigs);
                    callback.onSuccess(new ServerConfigList(s, serverConfigs));
                }
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
            Log.warn("server selection is null");//throw new IllegalStateException("server should not be null");

        return selectedServer!=null ? selectedServer : "not-set";
    }

    @Override
    public void onStaleModel(String modelName) {

        // TODO: Needed?

        if(StaleGlobalModel.SERVER_INSTANCES.equals(modelName))
        {
            // server instances carry started/stopped state
        }
        else if(StaleGlobalModel.SERVER_GROUPS.equals(modelName))
        {
            // do groups have relevant state?
        }
        else if(StaleGlobalModel.SERVER_CONFIGURATIONS.equals(modelName))
        {
            // do configs have relevant state?
        }
    }

    /**
     * Entry point for explicit host selection (user initiated)
     * @param hostName
     */
    @Override
    public void onHostSelection(String hostName) {
        selectedHost = hostName;

        // fire stale model event
        eventBus.fireEvent(new HostSelectionChanged());
    }


    /**
     * Entry point for explicit server selection (user initiated)
     * @param server
     */
    @Override
    public void onServerSelection(ServerInstance server) {
        // replace host selection, server selection has precedence
        selectedHost = server.getHost();
        selectedServer = server.getName();

        // check server state
        if(!server.isRunning())
            Console.warning("The selected server is not running: "+server.getName());

        // fire stale model
        eventBus.fireEvent(new ServerSelectionChanged(server.isRunning()));
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

    private ServerInstance getSelectedServerInstance(List<ServerInstance> serverInstances) {
        assert !serverInstances.isEmpty();

        ServerInstance matched = null;

        // match by preselection
        for(ServerInstance ServerInstance : serverInstances)
        {
            if(ServerInstance.getName().equals(selectedServer))
            {
                matched = ServerInstance;
                break;
            }
        }

        // fallback match
        if(null==matched)
            matched = serverInstances.get(0);

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
