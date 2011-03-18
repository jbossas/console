package org.jboss.as.console.client.domain.groups;

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
import org.jboss.as.console.client.domain.events.StaleModelEvent;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerGroupStore;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.domain.profiles.ProfileHeader;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/28/11
 */
public class ServerGroupMgmtPresenter
        extends Presenter<ServerGroupMgmtPresenter.MyView, ServerGroupMgmtPresenter.MyProxy>
        implements StaleModelEvent.StaleModelListener{

    private final PlaceManager placeManager;
    private ServerGroupStore serverGroupStore;
    private boolean hasBeenRevealed;

    @ProxyCodeSplit
    @NameToken(NameTokens.ServerGroupMgmtPresenter)
    public interface MyProxy extends Proxy<ServerGroupMgmtPresenter>, Place {

    }

    public interface MyView extends View {
        void setPresenter(ServerGroupMgmtPresenter presenter);

        void updateServerGroups(List<ServerGroupRecord> serverGroupRecords);
    }

    @ContentSlot
    public static final GwtEvent.Type<RevealContentHandler<?>> TYPE_MainContent = new GwtEvent.Type<RevealContentHandler<?>>();

    @Inject
    public ServerGroupMgmtPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,
            ServerGroupStore serverGroupStore) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.serverGroupStore = serverGroupStore;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getEventBus().addHandler(StaleModelEvent.TYPE, this);
        getView().setPresenter(this);
    }


    @Override
    protected void onReset() {
        super.onReset();

        Console.MODULES.getHeader().highlight(NameTokens.ServerGroupMgmtPresenter);

        serverGroupStore.loadServerGroups(new SimpleCallback<List<ServerGroupRecord>>() {
            @Override
            public void onSuccess(List<ServerGroupRecord> result) {
                getView().updateServerGroups(result);
            }
        });

        ProfileHeader header = new ProfileHeader("Group Management");
        Console.MODULES.getHeader().setContent(header);

        // default place upon first request
        if(!hasBeenRevealed)
        {
            placeManager.revealPlaceHierarchy(
                    Places.fromString("server-groups/"+NameTokens.ServerGroupPresenter)
            );
            hasBeenRevealed = true;
        }
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), MainLayoutPresenter.TYPE_MainContent, this);
    }

    @Override
    public void onStaleModel(String modelName) {
        if(StaleModelEvent.SERVER_GROUPS.equals(modelName))
        {
            serverGroupStore.loadServerGroups(new SimpleCallback<List<ServerGroupRecord>>() {
                @Override
                public void onSuccess(List<ServerGroupRecord> result) {
                    getView().updateServerGroups(result);
                }
            });
        }
    }
}
