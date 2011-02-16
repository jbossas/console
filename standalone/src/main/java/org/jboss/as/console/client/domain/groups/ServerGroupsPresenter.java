package org.jboss.as.console.client.domain.groups;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.NameTokens;
import org.jboss.as.console.client.components.SuspendableView;
import org.jboss.as.console.client.domain.DomainMgmtApplicationPresenter;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerGroupStore;

/**
 * @author Heiko Braun
 * @date 2/16/11
 */
public class ServerGroupsPresenter extends Presenter<ServerGroupsPresenter.MyView, ServerGroupsPresenter.MyProxy> {

    private final PlaceManager placeManager;
    private ServerGroupStore serverGroupStore;

    @ProxyCodeSplit
    @NameToken(NameTokens.ServerGroupsPresenter)
    public interface MyProxy extends Proxy<ServerGroupsPresenter>, Place {
    }

    public interface MyView extends SuspendableView {
        void setPresenter(ServerGroupsPresenter presenter);
        void updateFrom(ServerGroupRecord[] serverGroupRecords);
        void setSelectedRecord(ServerGroupRecord record);
    }

    @Inject
    public ServerGroupsPresenter(EventBus eventBus, MyView view, MyProxy proxy,
                                 PlaceManager placeManager, ServerGroupStore serverGroupStore) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.serverGroupStore = serverGroupStore;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }


    @Override
    protected void onReset() {
        super.onReset();
        getView().updateFrom(serverGroupStore.loadServerGroups());
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), DomainMgmtApplicationPresenter.TYPE_MainContent, this);
    }

    public void onSelectServerGroup(String groupName)
    {
        ServerGroupRecord[] records = serverGroupStore.loadServerGroups();
        for(ServerGroupRecord record : records)
        {
            if(groupName.equals(record.getAttribute("group-name")))
            {
                getView().setSelectedRecord(record);
                break;
            }
        }
    }
}
