package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.*;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.MainLayoutPresenter;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.Places;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.profiles.ProfileHeader;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/2/11
 */
public class HostMgmtPresenter
        extends Presenter<HostMgmtPresenter.MyView, HostMgmtPresenter.MyProxy>
        implements HostSelectionEvent.HostSelectionListener {

    private final PlaceManager placeManager;

    private HostInformationStore hostInfoStore;
    private String hostSelection = null;
    private boolean hasBeenRevealed;

    @ContentSlot
    public static final GwtEvent.Type<RevealContentHandler<?>> TYPE_MainContent = new GwtEvent.Type<RevealContentHandler<?>>();

    @ProxyCodeSplit
    @NameToken(NameTokens.HostMgmtPresenter)
    public interface MyProxy extends Proxy<HostMgmtPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(HostMgmtPresenter presenter);
        void updateHosts(List<Host> hosts);
        void updateServers(List<Server> servers);
    }

    @Inject
    public HostMgmtPresenter(
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

        getView().updateHosts(hostInfoStore.getHosts());
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {
        hostSelection = request.getParameter("host", null);
    }

    @Override
    protected void onReset() {
        super.onReset();

        Console.MODULES.getHeader().highlight(NameTokens.HostMgmtPresenter);

        ProfileHeader header = new ProfileHeader("Host Management");
        Console.MODULES.getHeader().setContent(header);

        if(!hasBeenRevealed)
        {
            placeManager.revealPlaceHierarchy(
                    Places.fromString("hosts/"+NameTokens.ServerPresenter)
            );
            hasBeenRevealed = true;
        }
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), MainLayoutPresenter.TYPE_MainContent, this);
    }

    @Override
    public void onHostSelection(String hostName) {
        hostSelection = hostName;
        getView().updateServers(
                hostInfoStore.getServerConfigurations(hostName)
        );
    }
}
