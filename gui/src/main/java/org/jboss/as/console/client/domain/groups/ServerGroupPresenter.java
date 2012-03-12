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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DomainGateKeeper;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.domain.events.StaleModelEvent;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ProfileStore;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerGroupStore;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.domain.profiles.ProfileMgmtPresenter;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.jvm.CreateJvmCmd;
import org.jboss.as.console.client.shared.jvm.DeleteJvmCmd;
import org.jboss.as.console.client.shared.jvm.Jvm;
import org.jboss.as.console.client.shared.jvm.JvmManagement;
import org.jboss.as.console.client.shared.jvm.UpdateJvmCmd;
import org.jboss.as.console.client.shared.properties.CreatePropertyCmd;
import org.jboss.as.console.client.shared.properties.DeletePropertyCmd;
import org.jboss.as.console.client.shared.properties.NewPropertyWizard;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.ballroom.client.layout.LHSHighlightEvent;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.dmr.client.ModelNode;

import java.util.List;
import java.util.Map;

/**
 * Maintains a single server group.
 *
 * @author Heiko Braun
 * @date 2/16/11
 */
public class ServerGroupPresenter
        extends Presenter<ServerGroupPresenter.MyView, ServerGroupPresenter.MyProxy>
        implements JvmManagement, PropertyManagement {

    private ServerGroupStore serverGroupStore;
    private ProfileStore profileStore;

    private DefaultWindow window;
    private DefaultWindow propertyWindow;

    private DispatchAsync dispatcher;
    private BeanFactory factory;
    private ApplicationMetaData propertyMetaData;

    private List<ProfileRecord> existingProfiles;
    private List<String> existingSockets;

    @ProxyCodeSplit
    @NameToken(NameTokens.ServerGroupPresenter)
    @UseGatekeeper( DomainGateKeeper.class )
    public interface MyProxy extends Proxy<ServerGroupPresenter>, Place {
    }

    public interface MyView extends SuspendableView {
        void setPresenter(ServerGroupPresenter presenter);
        void setServerGroups(List<ServerGroupRecord> groups);
        void updateSocketBindings(List<String> result);
        void setJvm(ServerGroupRecord group, Jvm jvm);
        void setProperties(ServerGroupRecord group, List<PropertyRecord> properties);
    }

    @Inject
    public ServerGroupPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            ServerGroupStore serverGroupStore,
            ProfileStore profileStore,
            DispatchAsync dispatcher, BeanFactory factory,
            ApplicationMetaData propertyMetaData) {
        super(eventBus, view, proxy);

        this.serverGroupStore = serverGroupStore;
        this.profileStore = profileStore;
        this.dispatcher = dispatcher;
        this.factory = factory;
        this.propertyMetaData = propertyMetaData;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {

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
                existingProfiles = result;
            }
        });

        serverGroupStore.loadSocketBindingGroupNames(new SimpleCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> result) {
                existingSockets = result;

                getView().updateSocketBindings(result);
            }
        });

        loadServerGroups();

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                getEventBus().fireEvent(
                        new LHSHighlightEvent(null, Console.CONSTANTS.common_label_serverGroupConfigurations(), "groups")

                );
            }
        });

    }

    private void loadServerGroups() {
        serverGroupStore.loadServerGroups(new SimpleCallback<List<ServerGroupRecord>>() {
            @Override
            public void onSuccess(List<ServerGroupRecord> result) {
                getView().setServerGroups(result);
            }
        });
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), ProfileMgmtPresenter.TYPE_MainContent, this);
    }

    // ----------------------------------------------------------------


    public void onDeleteGroup(final ServerGroupRecord group) {

        serverGroupStore.delete(group, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean wasSuccessful) {
                if (wasSuccessful) {
                    Console.info(Console.MESSAGES.deleted(group.getGroupName()));
                    getEventBus().fireEvent(new StaleModelEvent(StaleModelEvent.SERVER_GROUPS));
                } else {
                    Console.error(Console.MESSAGES.deletionFailed(group.getGroupName()));
                }

                loadServerGroups();
            }
        });
    }

    public void createNewGroup(final ServerGroupRecord newGroup) {

        closeDialoge();

        serverGroupStore.create(newGroup, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean success) {

                if (success) {

                    Console.info(Console.MESSAGES.added(newGroup.getGroupName()));

                    getEventBus().fireEvent(new StaleModelEvent(StaleModelEvent.SERVER_GROUPS));

                    loadServerGroups();

                } else {
                    Console.error(Console.MESSAGES.addingFailed(newGroup.getGroupName()));
                }

            }
        });
    }

    public void onSaveChanges(final ServerGroupRecord group, Map<String,Object> changeset) {

        serverGroupStore.save(group.getGroupName(), changeset, new SimpleCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean wasSuccessful) {
                if(wasSuccessful)
                {
                    Console.info(Console.MESSAGES.modified(group.getGroupName()));
                }
                else
                {
                    Console.info(Console.MESSAGES.modificationFailed(group.getGroupName()));
                }

                loadServerGroups();
            }
        });

    }

    public void launchNewGroupDialoge() {

        window = new DefaultWindow(Console.MESSAGES.createTitle("Server Group"));
        window.setWidth(480);
        window.setHeight(360);
        window.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {

            }
        });

        window.trapWidget(
                new NewServerGroupWizard(this, existingProfiles, existingSockets).asWidget()
        );

        window.setGlassEnabled(true);
        window.center();
    }

    public void closeDialoge()
    {
        if(window!=null) window.hide();
    }

    public void onUpdateJvm(final String groupName, String jvmName, Map<String, Object> changedValues) {

        ModelNode address = new ModelNode();
        address.add("server-group", groupName);
        address.add("jvm", jvmName);

        UpdateJvmCmd cmd = new UpdateJvmCmd(dispatcher, factory, propertyMetaData, address);
        cmd.execute(changedValues, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                loadServerGroups();
            }
        });

    }

    public void onCreateJvm(final String groupName, Jvm jvm) {

        ModelNode address = new ModelNode();
        address.add("server-group", groupName);
        address.add("jvm", jvm.getName());

        CreateJvmCmd cmd = new CreateJvmCmd(dispatcher, factory, address);
        cmd.execute(jvm, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                loadServerGroups();
            }
        });

    }

    public void onDeleteJvm(final String groupName, Jvm jvm) {

        ModelNode address = new ModelNode();
        address.add("server-group", groupName);
        address.add("jvm", jvm.getName());

        DeleteJvmCmd cmd = new DeleteJvmCmd(dispatcher, factory, address);
        cmd.execute(new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                loadServerGroups();
            }
        });
    }

    public void closePropertyDialoge() {
        propertyWindow.hide();
    }

    public void launchNewPropertyDialoge(String group) {

        propertyWindow = new DefaultWindow(Console.MESSAGES.createTitle("System Property"));
        propertyWindow.setWidth(320);
        propertyWindow.setHeight(240);

        propertyWindow.trapWidget(
                new NewPropertyWizard(this, group, true).asWidget()
        );

        propertyWindow.setGlassEnabled(true);
        propertyWindow.center();
    }

    public void onCreateProperty(final String groupName, final PropertyRecord prop)
    {
        if(propertyWindow!=null && propertyWindow.isShowing())
        {
            propertyWindow.hide();
        }

        ModelNode address = new ModelNode();
        address.add("server-group", groupName);
        address.add("system-property", prop.getKey());

        CreatePropertyCmd cmd = new CreatePropertyCmd(dispatcher, factory, address);
        cmd.execute(prop, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                loadServerGroups();
            }
        });
    }

    public void onDeleteProperty(final String groupName, final PropertyRecord prop)
    {
        ModelNode address = new ModelNode();
        address.add("server-group", groupName);
        address.add("system-property", prop.getValue());

        DeletePropertyCmd cmd = new DeletePropertyCmd(dispatcher,factory,address);
        cmd.execute(prop, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                loadServerGroups();
            }
        });
    }

    @Override
    public void onChangeProperty(String groupName, PropertyRecord prop) {
        // do nothing
    }

    public void loadJVMConfiguration(final ServerGroupRecord group) {
        serverGroupStore.loadJVMConfiguration(group, new SimpleCallback<Jvm>() {
            @Override
            public void onSuccess(Jvm jvm) {
                getView().setJvm(group, jvm);
            }
        });
    }

    public void loadProperties(final ServerGroupRecord group) {
        serverGroupStore.loadProperties(group, new SimpleCallback<List<PropertyRecord>>() {
            @Override
            public void onSuccess(List<PropertyRecord> properties) {
                getView().setProperties(group, properties);
            }
        });
    }

}
