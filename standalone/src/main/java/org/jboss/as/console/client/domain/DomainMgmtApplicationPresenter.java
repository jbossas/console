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

/**
 * @author Heiko Braun
 * @date 2/11/11
 */
public class DomainMgmtApplicationPresenter
        extends Presenter<DomainMgmtApplicationPresenter.MyView, DomainMgmtApplicationPresenter.MyProxy>
        implements ProfileSelectionEvent.ProfileSelectionListener {

    private final PlaceManager placeManager;
    private ProfileStore profileStore;


    @ProxyCodeSplit
    @NameToken(NameTokens.DomainManagementPresenter)
    public interface MyProxy extends Proxy<DomainMgmtApplicationPresenter>, Place {
    }

    public interface MyView extends View {
        void setProfiles(ProfileRecord[] profileRecords);
        void setSubsystems(SubsystemRecord[] subsystemRecords);
    }

    @ContentSlot
    public static final GwtEvent.Type<RevealContentHandler<?>> TYPE_MainContent =
            new GwtEvent.Type<RevealContentHandler<?>>();

    @Inject
    public DomainMgmtApplicationPresenter(
            EventBus eventBus,
            MyView view, MyProxy proxy,
            PlaceManager placeManager, ProfileStore profileStore) {

        super(eventBus, view, proxy);
        this.placeManager = placeManager;
        this.profileStore = profileStore;

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
    }

    @Override
    protected void onReset() {
        super.onReset();
        getView().setProfiles(profileStore.loadProfiles());
    }

    public void loadSubsystems(String profileName) {
        System.out.println("load subsystem for "+ profileName);
        getView().setSubsystems(subsysRecords);
    }

    @Override
    public void onProfileSelection(String profileName) {
        loadSubsystems(profileName);
    }

    static SubsystemRecord[] subsysRecords = new SubsystemRecord[] {
        new SubsystemRecord("Threads"),
            new SubsystemRecord("Web"),
            new SubsystemRecord("EJB"),
            new SubsystemRecord("JCA"),
            new SubsystemRecord("Messaging"),
            new SubsystemRecord("Transactions"),
            new SubsystemRecord("Web Services"),
            new SubsystemRecord("Clustering")

    };
}
