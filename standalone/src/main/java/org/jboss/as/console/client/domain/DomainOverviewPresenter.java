package org.jboss.as.console.client.domain;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.*;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ProfileStore;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerGroupStore;
import org.jboss.as.console.client.domain.profiles.ProfileMgmtPresenter;
import org.jboss.as.console.client.shared.DeploymentRecord;
import org.jboss.as.console.client.shared.DeploymentStore;

import java.util.List;


/**
 * @author Heiko Braun
 * @date 2/11/11
 */
public class DomainOverviewPresenter extends Presenter<DomainOverviewPresenter.MyView, DomainOverviewPresenter.MyProxy> {

    private final PlaceManager placeManager;
    private ProfileStore profileStore;
    private ServerGroupStore serverGroupStore;
    private DeploymentStore deploymentStore;

    @ProxyCodeSplit
    @NameToken(NameTokens.ProfileOverviewPresenter)
    public interface MyProxy extends Proxy<DomainOverviewPresenter>, Place {
    }

    public interface MyView extends SuspendableView {
        void setPresenter(DomainOverviewPresenter presenter);
    }

    @Inject
    public DomainOverviewPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, ProfileStore profileStore,
            ServerGroupStore serverGroupStore,
            DeploymentStore deploymentStore) {

        super(eventBus, view, proxy);
        this.placeManager = placeManager;
        this.profileStore = profileStore;
        this.serverGroupStore = serverGroupStore;
        this.deploymentStore = deploymentStore;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {
        super.prepareFromRequest(request);
        String profileName = request.getParameter("name", "none");
        Log.debug("requested profile: "+ profileName);

    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), ProfileMgmtPresenter.TYPE_MainContent, this);
    }

    // --------------------------------

    public List<ProfileRecord> getProfileRecords() {
        return profileStore.loadProfiles();
    }

    public List<ServerGroupRecord> getServerGroupRecords()
    {
        return serverGroupStore.loadServerGroups();
    }

    public List<DeploymentRecord> getDeploymentRecords() {
        return deploymentStore.loadDeployments();
    }
}
