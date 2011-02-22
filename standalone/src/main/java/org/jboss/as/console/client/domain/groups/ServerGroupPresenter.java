package org.jboss.as.console.client.domain.groups;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.*;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.NameTokens;
import org.jboss.as.console.client.components.SuspendableView;
import org.jboss.as.console.client.domain.DomainMgmtApplicationPresenter;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ProfileStore;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerGroupStore;
import org.jboss.as.console.client.util.message.Message;

import java.util.Map;

/**
 * Maintains a single server group.
 *
 * @author Heiko Braun
 * @date 2/16/11
 */
public class ServerGroupPresenter
        extends Presenter<ServerGroupPresenter.MyView, ServerGroupPresenter.MyProxy> {

    private final PlaceManager placeManager;
    private ServerGroupStore serverGroupStore;
    private ServerGroupRecord selectedRecord;
    private ProfileStore profileStore;

    public String[] getProfileNames() {

        ProfileRecord[] profileRecords = profileStore.loadProfiles();
        String[] names = new String[profileRecords.length];
        int i=0;
        for(ProfileRecord profile : profileRecords)
        {
            names[i] = profile.getName();
            i++;
        }
        return names;
    }

    public String[] getSocketBindings() {
        return new String[] {"default", "DMZ"};
    }

    public void persistChanges(Map changedValues) {
        String groupName = selectedRecord.getGroupName();
        Console.MODULES.getMessageCenter().notify(
                new Message("Saved :"+ groupName +" " +changedValues, Message.Severity.Info)
        );
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.ServerGroupPresenter)
    public interface MyProxy extends Proxy<ServerGroupPresenter>, Place {
    }

    public interface MyView extends SuspendableView {
        void setPresenter(ServerGroupPresenter presenter);
        void setSelectedRecord(ServerGroupRecord record);
        void setEnabled(boolean isEnabled);
    }

    @Inject
    public ServerGroupPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,
            ServerGroupStore serverGroupStore,
            ProfileStore profileStore) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.serverGroupStore = serverGroupStore;
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
        String groupName = request.getParameter("name", null);
        String action = request.getParameter("action", null);
        if(groupName!=null)
        {
            onSelectServerGroup(groupName);
        }
        else if(action!=null && action.equals("new"))
        {
            createNewGroup();
        }
        else
        {
            Log.error("Parameters missing!");
        }
    }

    public void createNewGroup() {
        final Window winModal = new Window();

        double GOLDEN_RATIO = 1.618;
        int winWidth = (int)(com.google.gwt.user.client.Window.getClientWidth()*0.8);
        int winHeight = (int) ( winWidth / GOLDEN_RATIO );

        winModal.setWidth(winWidth);
        winModal.setHeight(winHeight);


        winModal.setTitle("Create Server Group");
        winModal.setShowMinimizeButton(false);
        winModal.setIsModal(true);
        winModal.setShowModalMask(true);
        winModal.centerInPage();
        winModal.addCloseClickHandler(new CloseClickHandler() {
            public void onCloseClick(CloseClientEvent event) {

                winModal.destroy();
                History.back();
            }
        });
        winModal.addItem(
                new Label("This will become a wizard to create server groups.")
                {{
                        setMargin(15);
                    }}
        );

        winModal.show();
    }

    @Override
    protected void onReset() {
        super.onReset();
        getView().setSelectedRecord(selectedRecord);
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
            if(groupName.equals(record.getGroupName()))
            {
                selectedRecord = record;
                break;
            }
        }
    }
}
