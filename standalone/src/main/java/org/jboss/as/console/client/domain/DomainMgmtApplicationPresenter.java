package org.jboss.as.console.client.domain;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.*;
import org.jboss.as.console.client.MainLayoutPresenter;
import org.jboss.as.console.client.NameTokens;
import org.jboss.as.console.client.domain.events.ProfileSelectionEvent;
import org.jboss.as.console.client.domain.events.ServerGroupSelectionEvent;
import org.jboss.as.console.client.domain.model.*;
import org.jboss.as.console.client.shared.SubsystemRecord;
import org.jboss.as.console.client.shared.SubsystemStore;

/**
 * @author Heiko Braun
 * @date 2/11/11
 */
public class DomainMgmtApplicationPresenter
        extends Presenter<DomainMgmtApplicationPresenter.MyView, DomainMgmtApplicationPresenter.MyProxy>
        implements ProfileSelectionEvent.ProfileSelectionListener,
        ServerGroupSelectionEvent.ServerGroupSelectionListener {

    private final PlaceManager placeManager;
    private ProfileStore profileStore;
    private SubsystemStore subsysStore;
    private ServerGroupStore serverGroupStore;

    @ProxyCodeSplit
    @NameToken(NameTokens.DomainManagementPresenter)
    public interface MyProxy extends Proxy<DomainMgmtApplicationPresenter>, Place {
    }

    public interface MyView extends View {
        void setProfiles(ProfileRecord[] profileRecords);
        void setSubsystems(SubsystemRecord[] subsystemRecords);
        void setServerGroups(ServerGroupRecord[] serverGroupRecords);
        void setSelectedServerGroup(ServerGroupRecord record);
    }

    @ContentSlot
    public static final GwtEvent.Type<RevealContentHandler<?>> TYPE_MainContent =
            new GwtEvent.Type<RevealContentHandler<?>>();

    @Inject
    public DomainMgmtApplicationPresenter(
            EventBus eventBus,
            MyView view, MyProxy proxy,
            PlaceManager placeManager, ProfileStore profileStore,
            SubsystemStore subsysStore,
            ServerGroupStore serverGroupStore) {

        super(eventBus, view, proxy);
        this.placeManager = placeManager;
        this.profileStore = profileStore;
        this.subsysStore = subsysStore;
        this.serverGroupStore = serverGroupStore;

    }

    @Override
    protected void revealInParent() {
        // reveal in main layout
        RevealContentEvent.fire(getEventBus(), MainLayoutPresenter.TYPE_SetMainContent, this);
    }

    @Override
    protected void onBind() {
        super.onBind();
        getEventBus().addHandler(ProfileSelectionEvent.TYPE, this);
        getEventBus().addHandler(ServerGroupSelectionEvent.TYPE, this);
    }

    @Override
    protected void onReset() {
        super.onReset();
        getView().setProfiles(profileStore.loadProfiles());
        getView().setServerGroups(serverGroupStore.loadServerGroups());
    }

    private void loadSubsystems(String profileName) {
        getView().setSubsystems(subsysStore.loadSubsystems(profileName));
    }

    @Override
    public void onProfileSelection(String profileName) {
        loadSubsystems(profileName);
    }

    @Override
    public void onServerGroupSelection(String serverGroupName) {

        for(ServerGroupRecord group : serverGroupStore.loadServerGroups())
        {
            if(group.getAttribute("group-name").equals(serverGroupName))
            {
                getView().setSelectedServerGroup(group);
                break;
            }

        }

    }

}
