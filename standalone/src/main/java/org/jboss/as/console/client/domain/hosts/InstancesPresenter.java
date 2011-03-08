package org.jboss.as.console.client.domain.hosts;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.*;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerInstance;

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

    @ProxyCodeSplit
    @NameToken(NameTokens.InstancesPresenter)
    public interface MyProxy extends Proxy<InstancesPresenter>, Place {
    }

    public interface MyView extends SuspendableView {
        void setPresenter(InstancesPresenter presenter);
        void setSelectedHost(String selectedHost);
        void updateInstances(List<ServerInstance> instances);
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
    }

    @Override
    protected void onReset() {
        super.onReset();
        refreshView();

    }

    private void refreshView() {
        getView().setSelectedHost(selectedHost);
        getView().updateInstances(hostInfoStore.getInstances(selectedHost));
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
}
