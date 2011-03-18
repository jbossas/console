package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.*;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.domain.model.*;

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
        assert selectedHost!=null : "Parameter 'host' is missing";

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
        getView().setSelectedHost(selectedHost);

        // TODO: server instances
        /*hostInfoStore.getServerInstances(selectedHost, new SimpleCallback<List<ServerInstance>>() {
            @Override
            public void onSuccess(List<ServerInstance> result) {
                serverInstances = result;
                getView().updateInstances(result);
            }
        });*/

        hostInfoStore.getServerConfigurations(selectedHost, new SimpleCallback<List<Server>>() {
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
}
