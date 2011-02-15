package org.jboss.as.console.client.domain.profiles;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.*;
import org.jboss.as.console.client.NameTokens;
import org.jboss.as.console.client.components.SuspendableView;
import org.jboss.as.console.client.domain.DomainMgmtApplicationPresenter;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ProfileStore;


/**
 * @author Heiko Braun
 * @date 2/11/11
 */
public class ProfileToolPresenter extends Presenter<ProfileToolPresenter.MyView, ProfileToolPresenter.MyProxy> {

    private final PlaceManager placeManager;
    private ProfileStore profileStore;

    @ProxyCodeSplit
    @NameToken(NameTokens.ProfileToolPresenter)
    public interface MyProxy extends Proxy<ProfileToolPresenter>, Place {
    }

    public interface MyView extends SuspendableView {
        void setPresenter(ProfileToolPresenter presenter);
    }

    @Inject
    public ProfileToolPresenter(EventBus eventBus, MyView view, MyProxy proxy,
                                PlaceManager placeManager, ProfileStore profileStore) {
        super(eventBus, view, proxy);
        this.placeManager = placeManager;
        this.profileStore = profileStore;
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
        RevealContentEvent.fire(getEventBus(), DomainMgmtApplicationPresenter.TYPE_MainContent, this);
    }


    // --------------------------------

    public ProfileRecord[] getRecords() {
        return profileStore.loadProfiles();
    }

}
