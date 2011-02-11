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
import org.jboss.as.console.client.domain.profiles.ProfileRecord;
import org.jboss.as.console.client.domain.profiles.ProfileStore;

/**
 * @author Heiko Braun
 * @date 2/11/11
 */
public class DomainMgmtApplicationPresenter extends Presenter<DomainMgmtApplicationPresenter.MyView, DomainMgmtApplicationPresenter.MyProxy> {

    private final PlaceManager placeManager;
    private ProfileStore profileStore;

    @ProxyCodeSplit
    @NameToken(NameTokens.DomainManagementPresenter)
    public interface MyProxy extends Proxy<DomainMgmtApplicationPresenter>, Place {
    }

    public interface MyView extends View {
        void setProfiles(ProfileRecord[] profileRecords);
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
    protected void onReset() {
        super.onReset();
        getView().setProfiles(profileStore.loadProfiles());
    }
}
