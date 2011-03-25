package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.core.message.Message;
import org.jboss.as.console.client.domain.model.EntityFilter;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.Predicate;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.domain.model.SimpleCallback;

import java.util.List;

/**
 * Manage server instances on a specific host.
 *
 * @author Heiko Braun
 * @date 3/8/11
 */
public class InstancesPresenter extends Presenter<InstancesPresenter.MyView, InstancesPresenter.MyProxy>
        implements HostSelectionEvent.HostSelectionListener {

    private final PlaceManager placeManager;
    private HostInformationStore hostInfoStore;
    private String selectedHost = null;
    private EntityFilter<ServerInstance> filter = new EntityFilter<ServerInstance>();
    private List<ServerInstance> serverInstances;


    @ProxyCodeSplit
    @NameToken(NameTokens.InstancesPresenter)
    public interface MyProxy extends Proxy<InstancesPresenter>, Place {
    }

    public interface MyView extends SuspendableView {
        void setPresenter(InstancesPresenter presenter);
        void setSelectedHost(String selectedHost);
        void updateInstances(List<ServerInstance> instances);
        void updateServerConfigurations(List<Server> servers);
    }

    @Inject
    public InstancesPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,
            HostInformationStore hostInfoStore) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.hostInfoStore = hostInfoStore;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
        getEventBus().addHandler(HostSelectionEvent.TYPE, this);
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {
        selectedHost = request.getParameter("host", null);

        String action = request.getParameter("action", null);
        if(action!=null)
        {
            Window.alert("Not yet implemented");
        }
    }

    @Override
    protected void onReset() {
        super.onReset();
        refreshView();

    }

    private void refreshView() {

        if(null== selectedHost)
        {
            // fallback
            hostInfoStore.getHosts(new SimpleCallback<List<Host>>() {
                @Override
                public void onSuccess(List<Host> result) {
                    loadHostData(result.get(0).getName());
                }
            });
        }
        else
        {
            loadHostData(selectedHost);
        }
    }

    private void loadHostData(String hostName) {

        selectedHost = hostName;

        getView().setSelectedHost(hostName);

        hostInfoStore.getServerInstances(hostName, new SimpleCallback<List<ServerInstance>>() {
            @Override
            public void onSuccess(List<ServerInstance> result) {
                serverInstances = result;
                getView().updateInstances(result);
            }
        });

        hostInfoStore.getServerConfigurations(hostName, new SimpleCallback<List<Server>>() {
            @Override
            public void onSuccess(List<Server> result) {
                getView().updateServerConfigurations(result);
            }
        });
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), HostMgmtPresenter.TYPE_MainContent, this);
    }

    @Override
    public void onHostSelection(String hostName) {
        selectedHost = hostName;
        refreshView();
    }

    public void onFilterType(String serverConfig) {

        List<ServerInstance> filtered = filter.apply(
                new ServerConfigPredicate(serverConfig),
                serverInstances
        );

        getView().updateInstances(filtered);
    }

    class ServerConfigPredicate implements Predicate<ServerInstance> {
        private String configFilter;

        ServerConfigPredicate(String configFilter) {
            this.configFilter = configFilter;
        }

        @Override
        public boolean appliesTo(ServerInstance candidate) {

            boolean configMatch = configFilter.equals("") ?
                    true : candidate.getServer().equals(configFilter);

            return configMatch;
        }
    }

    public void startServer(final String configName, final boolean startIt) {
        hostInfoStore.startServer(selectedHost, configName, startIt, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean wasSuccessful) {

                String msg;
                if(startIt)
                {
                    msg = wasSuccessful ?
                            "Successfully started server "+configName :
                            "Failed to start server "+configName;
                }
                else
                {
                    msg = wasSuccessful ?
                            "Successfully stopped server "+configName :
                            "Failed to stop server "+configName;

                }

                Message.Severity sev = wasSuccessful ? Message.Severity.Info : Message.Severity.Error;
                Console.MODULES.getMessageCenter().notify(
                    new Message(msg, sev)
                );

                if(wasSuccessful)
                {
                    // if the operation was success we merge the local state changes into the mdoel
                    // to avoid a polling request (server started async)

                    hostInfoStore.getServerInstances(selectedHost, new SimpleCallback<List<ServerInstance>>() {
                        @Override
                        public void onSuccess(List<ServerInstance> result) {
                            serverInstances = result;

                            // merge local state
                            for(ServerInstance instance : result)
                                if(instance.getServer().equals(configName)) instance.setRunning(startIt);

                            getView().updateInstances(result);
                        }
                    });
                }
            }
        });
    }
}
