package org.jboss.as.console.client.domain.groups;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.*;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.core.message.Message;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ProfileStore;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerGroupStore;
import org.jboss.as.console.client.shared.BeanFactory;
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

        String groupName = request.getParameter("name", null);
        String action = request.getParameter("action", null);

        if(groupName!=null)
        {
            for(ServerGroupRecord record : serverGroupStore.loadServerGroups())
            {
                if(groupName.equals(record.getGroupName()))
                {
                    selectedRecord = record;
                    break;
                }
            }
        }
        else if("new".equals(action))
        {
            selectedRecord = null;
            createNewGroup();
        }
        else
        {
            Log.error("Parameters missing. Fallback to default Group");
            this.selectedRecord = serverGroupStore.loadServerGroups().get(0);
        }
    }

    @Override
    protected void onReset() {
        super.onReset();
        if(selectedRecord!=null)
            getView().setSelectedRecord(selectedRecord);
        getView().setEnabled(false);
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), ServerGroupMgmtPresenter.TYPE_MainContent, this);
    }

    // ----------------------------------------------------------------

    public String[] getProfileNames() {

        List<ProfileRecord> profileRecords = profileStore.loadProfiles();
        String[] names = new String[profileRecords.size()];
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

    public void editCurrentRecord() {
        getView().setEnabled(true);
    }

    public void deleteCurrentRecord() {

        if(selectedRecord!=null)
        {
            serverGroupStore.deleteGroup(selectedRecord);
            Console.MODULES.getMessageCenter().notify(
                    new Message("Deleted "+selectedRecord.getGroupName())
            );
        }

        // switch to alternate record instead
        workOn(serverGroupStore.loadServerGroups().get(0));

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

        serverGroupStore.persist(updatedEntity);
    }

    private void workOn(ServerGroupRecord record) {
        this.selectedRecord = record;
        getView().setSelectedRecord(selectedRecord);
    }

    public void createNewGroup() {
        window = new DefaultWindow("Create Server Group");

        window.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                if(selectedRecord==null)
                    History.back();
            }
        });

        window.setWidget(
                new NewGroupWizard(this, serverGroupStore.loadServerGroups()).asWidget()
        );

        window.setGlassEnabled(true);
        window.center();
    }

}
