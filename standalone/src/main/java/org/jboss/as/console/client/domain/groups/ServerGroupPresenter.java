package org.jboss.as.console.client.domain.groups;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.core.message.Message;
import org.jboss.as.console.client.domain.model.*;
import org.jboss.as.console.client.widgets.DefaultWindow;

import java.util.List;

/**
 * Maintains a single server group.
 *
 * @author Heiko Braun
 * @date 2/16/11
 */
public class ServerGroupPresenter
        extends Presenter<ServerGroupPresenter.MyView, ServerGroupPresenter.MyProxy> {

    private ServerGroupStore serverGroupStore;
    private ProfileStore profileStore;

    private List<ServerGroupRecord> serverGroups;
    private ServerGroupRecord selectedRecord;
    private DefaultWindow window;

    @ProxyCodeSplit
    @NameToken(NameTokens.ServerGroupPresenter)
    public interface MyProxy extends Proxy<ServerGroupPresenter>, Place {
    }

    public interface MyView extends SuspendableView {
        void setPresenter(ServerGroupPresenter presenter);
        void setSelectedRecord(ServerGroupRecord record);
        void setEnabled(boolean isEnabled);
        void updateProfiles(List<ProfileRecord> result);
    }

    @Inject
    public ServerGroupPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            ServerGroupStore serverGroupStore,
            ProfileStore profileStore) {
        super(eventBus, view, proxy);

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

        final String groupName = request.getParameter("name", null);
        String action = request.getParameter("action", null);

        if("new".equals(action))
        {
            selectedRecord = null;
            createNewGroup();
        }
        else
        {
            serverGroupStore.loadServerGroups(new SimpleCallback<List<ServerGroupRecord>>() {
                @Override
                public void onSuccess(List<ServerGroupRecord> result) {

                    if(groupName!=null)
                    {
                        for(ServerGroupRecord record : result)
                        {
                            if(groupName.equals(record.getGroupName()))
                            {
                                selectedRecord = record;
                                break;
                            }
                        }
                    }
                    else
                    {
                        Log.warn("Parameter 'groupName' missing, fallback to default group");
                        selectedRecord = result.get(0);
                    }
                }
            });
        }

    }

    @Override
    protected void onReset() {
        super.onReset();

        profileStore.loadProfiles(new SimpleCallback<List<ProfileRecord>>() {
            @Override
            public void onSuccess(List<ProfileRecord> result) {
                getView().updateProfiles(result);

                if(selectedRecord!=null)
                    getView().setSelectedRecord(selectedRecord);
            }
        });

        serverGroupStore.loadServerGroups(new SimpleCallback<List<ServerGroupRecord>>() {

            @Override
            public void onSuccess(List<ServerGroupRecord> result) {
                serverGroups = result;
            }
        });

        getView().setEnabled(false);
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), ServerGroupMgmtPresenter.TYPE_MainContent, this);
    }

    // ----------------------------------------------------------------


    public String[] getSocketBindings() {
        return new String[] {"default", "DMZ"}; // TODO: implement
    }

    public void editCurrentRecord() {
        getView().setEnabled(true);
    }

    public void deleteCurrentRecord() {

        if(selectedRecord!=null)
        {
            serverGroupStore.deleteGroup(selectedRecord, new SimpleCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {

                }
            });

            Console.MODULES.getMessageCenter().notify(
                    new Message("Deleted "+selectedRecord.getGroupName())
            );
        }

        // switch to alternate record instead
        workOn(serverGroups.get(0));

    }

    public void onNewGroup(ServerGroupRecord newGroup) {

        workOn(newGroup);

        // close popup
        if(window!=null && window.isShowing())
        {
            window.hide();
        }

        // save changes
        onSaveChanges(newGroup);
    }

    public void onSaveChanges(ServerGroupRecord updatedEntity) {
        getView().setEnabled(false);

        Console.MODULES.getMessageCenter().notify(
                new Message("Saved " + updatedEntity.getGroupName(), Message.Severity.Info)
        );

        serverGroupStore.persist(updatedEntity, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {

            }
        });
    }

    private void workOn(ServerGroupRecord record) {
        this.selectedRecord = record;
        getView().setSelectedRecord(selectedRecord);
    }

    public void createNewGroup() {
        window = new DefaultWindow("Create Server Group");
        window.setWidth(300);
        window.setHeight(250);
        window.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                if(selectedRecord==null)
                    History.back();
            }
        });

        window.setWidget(
                new NewGroupWizard(this, serverGroups).asWidget()
        );

        window.setGlassEnabled(true);
        window.center();
    }

}
