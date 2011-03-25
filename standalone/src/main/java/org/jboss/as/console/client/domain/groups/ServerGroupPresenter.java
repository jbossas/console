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
    private String groupName;

    @ProxyCodeSplit
    @NameToken(NameTokens.ServerGroupPresenter)
    public interface MyProxy extends Proxy<ServerGroupPresenter>, Place {
    }

    public interface MyView extends SuspendableView {
        void setPresenter(ServerGroupPresenter presenter);
        void setSelectedRecord(ServerGroupRecord record);
        void setEnabled(boolean isEnabled);
        void updateProfiles(List<ProfileRecord> result);

        void updateSocketBindings(List<String> result);
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

        this.groupName = request.getParameter("name", null);
        final String action = request.getParameter("action", null);

        if("new".equals(action))
        {
            selectedRecord = null;
            groupName = null;
            createNewGroup();
        }
    }

    @Override
    protected void onReset() {

        super.onReset();

        profileStore.loadProfiles(new SimpleCallback<List<ProfileRecord>>() {
            @Override
            public void onSuccess(List<ProfileRecord> result) {
                getView().updateProfiles(result);
            }
        });

        serverGroupStore.loadSocketBindingGroupNames(new SimpleCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> result) {
                getView().updateSocketBindings(result);
            }
        });
        serverGroupStore.loadServerGroups(new SimpleCallback<List<ServerGroupRecord>>() {
            @Override
            public void onSuccess(List<ServerGroupRecord> result) {

                serverGroups = result;
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
                    Log.warn("Parameter 'name' missing, fallback to default group");
                    groupName = result.get(0).getGroupName();
                    selectedRecord = result.get(0);
                }

                loadServerGroup(selectedRecord.getGroupName());
                getView().setEnabled(false);
            }
        });

    }

    private void loadServerGroup(String name)
    {
        serverGroupStore.loadServerGroup(name, new SimpleCallback<ServerGroupRecord>() {
            @Override
            public void onSuccess(ServerGroupRecord result) {
                getView().setSelectedRecord(result);
            }
        });

    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), ServerGroupMgmtPresenter.TYPE_MainContent, this);
    }

    // ----------------------------------------------------------------

    public void editCurrentRecord() {
        getView().setEnabled(true);
    }

    public void deleteCurrentRecord() {

        if(selectedRecord!=null)
        {
            serverGroupStore.delete(selectedRecord, new SimpleCallback<Boolean>() {
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

    public void createNewGroup(final ServerGroupRecord newGroup) {

        // close popup
        if(window!=null && window.isShowing())
        {
            window.hide();
        }

        getView().setEnabled(false);

        serverGroupStore.create(newGroup, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean success) {

                if (success) {
                    Console.MODULES.getMessageCenter().notify(
                            new Message("Created " + newGroup.getGroupName(), Message.Severity.Info)
                    );

                    workOn(newGroup);

                } else {
                    Console.MODULES.getMessageCenter().notify(
                            new Message("Failed to create " + newGroup.getGroupName(), Message.Severity.Error)
                    );

                }

            }
        });
    }

    public void onSaveChanges(ServerGroupRecord updatedEntity) {
        getView().setEnabled(false);

        Console.MODULES.getMessageCenter().notify(
                new Message("Saved " + updatedEntity.getGroupName(), Message.Severity.Info)
        );

        serverGroupStore.save(updatedEntity, new SimpleCallback<Boolean>() {
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
        window.setWidth(320);
        window.setHeight(240);
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
