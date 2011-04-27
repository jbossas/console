/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jboss.as.console.client.domain.groups;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.autobean.shared.AutoBeanUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
import org.jboss.as.console.client.domain.events.StaleModelEvent;
import org.jboss.as.console.client.domain.model.Jvm;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ProfileStore;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerGroupStore;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.widgets.DefaultWindow;
import org.jboss.as.console.client.widgets.LHSHighlightEvent;
import org.jboss.as.console.client.widgets.forms.PropertyBinding;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;

import java.util.List;
import java.util.Map;

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
            ProfileStore profileStore,
            PropertyMetaData propertyMeta) {
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
            launchNewGroupDialoge();
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
        refreshServerGroups();

    }

    private void refreshServerGroups() {
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

                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            getEventBus().fireEvent(
                                    new LHSHighlightEvent(null, selectedRecord.getGroupName(), "groups")

                            );
                        }
                    });

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
            final ServerGroupRecord deletion = selectedRecord;
            serverGroupStore.delete(deletion, new SimpleCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean wasSuccessful) {
                    if(wasSuccessful)
                    {
                        Console.MODULES.getMessageCenter().notify(
                                new Message("Deleted server group "+deletion.getGroupName())
                        );

                        getEventBus().fireEvent(new StaleModelEvent(StaleModelEvent.SERVER_GROUPS));
                    }
                    else
                    {
                        Console.MODULES.getMessageCenter().notify(
                                new Message("Failed to delete "+deletion.getGroupName(), Message.Severity.Error)
                        );
                    }
                }
            });


        }

        // switch to alternate record instead
        serverGroupStore.loadServerGroups(new SimpleCallback<List<ServerGroupRecord>>() {
            @Override
            public void onSuccess(List<ServerGroupRecord> result) {
                if(result.size()>0)
                    workOn(serverGroups.get(0));
            }
        });

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


                    getEventBus().fireEvent(new StaleModelEvent(StaleModelEvent.SERVER_GROUPS));

                    workOn(newGroup);


                } else {
                    Console.MODULES.getMessageCenter().notify(
                            new Message("Failed to create " + newGroup.getGroupName(), Message.Severity.Error)
                    );

                }

            }
        });
    }

    public void onSaveChanges(final String name, Map<String,Object> changeset) {
        getView().setEnabled(false);

        if(changeset.size()>0)
        {
            serverGroupStore.save(name, changeset, new AsyncCallback<Boolean>() {

                @Override
                public void onFailure(Throwable caught) {
                    // log and reset when something fails
                    Console.error("Failed to modify server-group "+name);
                    refreshServerGroups();
                }

                @Override
                public void onSuccess(Boolean wasSuccessful) {
                    if(wasSuccessful)
                    {
                        Console.info("Modified server-group "+name);
                    }
                    else
                    {
                        Console.error("Failed to modify server-group "+name);
                    }

                    refreshServerGroups();
                }
            });
        }
        else
        {
            Console.warning("No changes applied!");
        }
    }

    private void workOn(ServerGroupRecord record) {
        this.selectedRecord = record;
        getView().setSelectedRecord(selectedRecord);
    }

    public void launchNewGroupDialoge() {

        selectedRecord = null;
        groupName = null;

        window = new DefaultWindow("Create Server Group");
        window.setWidth(320);
        window.setHeight(240);
        window.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {

            }
        });

        window.setWidget(
                new NewServerGroupWizard(this, serverGroups).asWidget()
        );

        window.setGlassEnabled(true);
        window.center();
    }

    public void closeDialoge()
    {
        if(window!=null) window.hide();
    }

    public void onSaveJvm(final String groupName, String jvmName, Map<String, Object> changedValues) {

        System.out.println(groupName+">"+changedValues);

        if(changedValues.size()>0)
        {
            serverGroupStore.saveJvm(groupName, jvmName, changedValues, new SimpleCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean success) {
                    if(success)
                    {
                        Console.info("Saved JVM settings");
                        loadServerGroup(groupName);
                    }
                    else
                        Console.error("Failed to saved JVM settings");
                }
            });
        }
        else
        {
            Console.warning("No changes applied!");
        }
    }

    public void onCreateJvm(final String groupName, Jvm jvm) {

        serverGroupStore.createJvm(groupName, jvm, new SimpleCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean success) {
                    if(success)
                    {
                        Console.info("Saved JVM settings");
                        loadServerGroup(groupName);
                    }
                    else
                        Console.error("Failed to saved JVM settings");
                }
            });
    }

     public void onDeleteJvm(final String groupName, Jvm editedEntity) {
        serverGroupStore.removeJvm(groupName, editedEntity, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                loadServerGroup(groupName);
            }
        });
    }
}
