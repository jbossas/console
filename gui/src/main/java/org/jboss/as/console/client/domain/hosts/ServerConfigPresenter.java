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

package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.core.message.Message;
import org.jboss.as.console.client.domain.events.StaleModelEvent;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerGroupStore;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.general.model.LoadSocketBindingsCmd;
import org.jboss.as.console.client.shared.general.model.SocketBinding;
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
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.dmr.client.ModelNode;

import java.util.List;
import java.util.Map;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 3/3/11
 */
public class ServerConfigPresenter extends Presenter<ServerConfigPresenter.MyView, ServerConfigPresenter.MyProxy>
        implements HostSelectionEvent.HostSelectionListener,
        ServerWizardEvent.ServerWizardListener,
        JvmManagement, PropertyManagement {

    private HostInformationStore hostInfoStore;

    private Server selectedRecord = null;
    private ServerGroupStore serverGroupStore;

    private String serverName;
    private String selectedHost;

    private DefaultWindow window = null;
    private List<ServerGroupRecord> serverGroups;

    private DefaultWindow propertyWindow;
    private DispatchAsync dispatcher;
    private PropertyMetaData propertyMetaData;
    private BeanFactory factory;
    private PlaceManager placeManager;

    @ProxyCodeSplit
    @NameToken(NameTokens.ServerPresenter)
    public interface MyProxy extends Proxy<ServerConfigPresenter>, Place {
    }

    public interface MyView extends SuspendableView {
        void setPresenter(ServerConfigPresenter presenter);
        void setEnabled(boolean isEnabled);
        void setSelectedRecord(Server selectedRecord);
        void updateServerGroups(List<ServerGroupRecord> serverGroupRecords);
        void updateSocketBindings(List<String> result);
        void updateVirtualMachines(List<String> result);
    }

    @Inject
    public ServerConfigPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            HostInformationStore hostInfoStore,
            ServerGroupStore serverGroupStore,
            DispatchAsync dispatcher,
            PropertyMetaData propertyMetaData, BeanFactory factory,
            PlaceManager placeManager) {
        super(eventBus, view, proxy);

        this.hostInfoStore = hostInfoStore;
        this.serverGroupStore = serverGroupStore;
        this.dispatcher = dispatcher;
        this.propertyMetaData = propertyMetaData;
        this.factory = factory;
        this.placeManager = placeManager;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
        getEventBus().addHandler(HostSelectionEvent.TYPE, this);
        getEventBus().addHandler(ServerWizardEvent.TYPE, this);
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {
        selectedHost = request.getParameter("host", null);
        serverName = request.getParameter("server", null);
        String action= request.getParameter("action", null);

        if("new".equals(action))
        {
            launchNewConfigDialoge();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();

        // step1
        serverGroupStore.loadServerGroups(new SimpleCallback<List<ServerGroupRecord>>() {
            @Override
            public void onSuccess(List<ServerGroupRecord> result) {
                serverGroups = result;
                getView().updateServerGroups(result);

                // step2
                loadSocketBindings();
            }
        });

    }

    private void loadSocketBindings() {
        serverGroupStore.loadSocketBindingGroupNames(new SimpleCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> result) {
                getView().updateSocketBindings(result);

                // step3
                loadServerConfigurations();
            }
        });
    }

    private void loadServerConfigurations() {

        if(selectedHost !=null && serverName!=null)
        {
            loadJVMs(selectedHost);

            hostInfoStore.getServerConfigurations(selectedHost, new SimpleCallback<List<Server>>() {
                @Override
                public void onSuccess(List<Server> result) {

                    for(Server server : result)
                    {
                        if(server.getName().equals(serverName))
                        {
                            serverName = server.getName();
                            workOn(server);
                            break;
                        }
                    }
                }
            });
        }
        else
        {
            // fallback (first request)
            hostInfoStore.getHosts(new SimpleCallback<List<Host>>() {
                @Override
                public void onSuccess(List<Host> result) {
                    selectedHost = result.get(0).getName();
                    loadDefaultForHost(selectedHost);
                }
            });
        }
    }

    private void loadJVMs(String host) {
        hostInfoStore.getVirtualMachines(host, new SimpleCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> result) {
                getView().updateVirtualMachines(result);
            }
        });
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), HostMgmtPresenter.TYPE_MainContent, this);
    }

    @Override
    public void onHostSelection(String hostName) {

        // display first server config by default
        //loadDefaultForHost(selectedHost);
    }

    public void launchNewConfigDialoge() {

        serverName = null;
        selectedRecord = null;

        window = new DefaultWindow("Create Server Configuration");
        window.setWidth(480);
        window.setHeight(360);
        window.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                /*if(selectedRecord==null)
                    History.back();*/
            }
        });


        serverGroupStore.loadServerGroups(new SimpleCallback<List<ServerGroupRecord>>() {
            @Override
            public void onSuccess(List<ServerGroupRecord> result) {
                serverGroups = result;
                window.setWidget(
                        new NewServerConfigWizard(ServerConfigPresenter.this, serverGroups).asWidget()
                );

                window.setGlassEnabled(true);
                window.center();
            }
        });

    }

    public void closeDialoge() {
        if(window!=null && window.isShowing())
        {
            window.hide();
        }
    }

    private void loadDefaultForHost(final String hostName) {
        hostInfoStore.getServerConfigurations(hostName, new SimpleCallback<List<Server>>() {
            @Override
            public void onSuccess(List<Server> result) {

                if(!result.isEmpty()) {
                    workOn(result.get(0));
                    serverName = selectedRecord.getName();
                    loadJVMs(hostName);
                }
                else {
                    noServerAvailable();
                }

            }
        });
    }

    private void noServerAvailable() {
        placeManager.revealPlace(new PlaceRequest(NameTokens.InstancesPresenter));
    }

    public void createServerConfig(final Server newServer) {

        // close popup
        closeDialoge();

        hostInfoStore.createServerConfig(getSelectedHost(), newServer, new AsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean wasSuccessful) {
                if (wasSuccessful) {

                    Console.MODULES.getMessageCenter().notify(
                            new Message("Created server config " + newServer.getName())
                    );

                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            getEventBus().fireEvent(new StaleModelEvent(StaleModelEvent.SERVER_CONFIGURATIONS));
                        }
                    });

                    workOn(newServer);

                } else {
                    closeDialoge();
                    Console.MODULES.getMessageCenter().notify(
                            new Message("Failed to create server config " + newServer.getName(), Message.Severity.Error)
                    );

                }

            }

            @Override
            public void onFailure(Throwable caught) {

                Console.MODULES.getMessageCenter().notify(
                        new Message("Failed to create server config " + newServer.getName(), Message.Severity.Error)
                );

            }
        });
    }

    private void workOn(Server record) {
        getView().setEnabled(false); // default edit state
        selectedRecord = record;
        getView().setSelectedRecord(record);
    }

    public void onSaveChanges(final String name, Map<String, Object> changedValues) {

        getView().setEnabled(false);

        if(changedValues.size()>0)
        {
            hostInfoStore.saveServerConfig(selectedHost, name, changedValues, new AsyncCallback<Boolean>() {

                @Override
                public void onFailure(Throwable caught) {
                    // log and reset when something fails
                    Console.error("Failed to modify server-config " +name);
                    loadServerConfigurations();
                }

                @Override
                public void onSuccess(Boolean wasSuccessful) {
                    if(wasSuccessful)
                    {
                        Console.info("Successfully modified server-config " +name);
                    }
                    else
                    {
                        Console.error("Failed to modify server-config " +name);
                    }

                    loadServerConfigurations();
                }
            });

        }
    }


    public void editCurrentRecord() {
        getView().setEnabled(true);
    }

    public void tryDeleteCurrentRecord() {

        // check if instance exist
        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).add("host", selectedHost);
        operation.get(ADDRESS).add("server", selectedRecord.getName());
        operation.get(OP).set(READ_RESOURCE_OPERATION);

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable throwable) {
                performDeleteOperation();
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                String outcome = response.get(OUTCOME).asString();

                Boolean serverIsRunning = outcome.equals(SUCCESS) ? Boolean.TRUE : Boolean.FALSE;
                if(!serverIsRunning)
                    performDeleteOperation();
                else
                    Console.error(
                            "Failed to delete server configuration",
                            "The server instance is still running: "+selectedRecord.getName()
                    );
            }
        });


    }

    private void performDeleteOperation() {

        hostInfoStore.deleteServerConfig(selectedHost, selectedRecord, new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable caught) {
                Console.MODULES.getMessageCenter().notify(
                        new Message("Failed to delete server config "+selectedRecord.getName(), Message.Severity.Error)
                );
            }

            @Override
            public void onSuccess(Boolean wasSuccessful) {
                if(wasSuccessful)
                {
                    Console.MODULES.getMessageCenter().notify(
                            new Message("Successfully deleted server config "+selectedRecord.getName())
                    );

                    getEventBus().fireEvent(new StaleModelEvent(StaleModelEvent.SERVER_CONFIGURATIONS));

                    loadDefaultForHost(selectedHost);
                }
                else
                {
                    Console.MODULES.getMessageCenter().notify(
                            new Message("Failed to delete server config "+selectedRecord.getName(), Message.Severity.Error)
                    );
                }
            }
        });
    }

    public String getSelectedHost() {
        return selectedHost;
    }


    @Override
    public void onCreateJvm(String reference, Jvm jvm) {
        ModelNode address = new ModelNode();
        address.add("host", selectedHost);
        address.add("server-config", reference);
        address.add(JVM, jvm.getName());

        CreateJvmCmd cmd = new CreateJvmCmd(dispatcher, factory, address);
        cmd.execute(jvm, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                loadServerConfigurations();
            }
        });
    }

    @Override
    public void onDeleteJvm(String reference, Jvm jvm) {

        ModelNode address = new ModelNode();
        address.add("host", selectedHost);
        address.add("server-config", reference);
        address.add(JVM, jvm.getName());

        DeleteJvmCmd cmd = new DeleteJvmCmd(dispatcher, factory, address);
        cmd.execute(new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                loadServerConfigurations();
            }
        });

    }

    @Override
    public void onUpdateJvm(String reference, String jvmName, Map<String, Object> changedValues) {

        if(changedValues.size()>0)
        {
            ModelNode address = new ModelNode();
            address.add("host", selectedHost);
            address.add("server-config", reference);
            address.add(JVM, jvmName);

            UpdateJvmCmd cmd = new UpdateJvmCmd(dispatcher, factory, propertyMetaData, address);
            cmd.execute(changedValues, new SimpleCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    loadServerConfigurations();
                }
            });
        }
    }

    @Override
    public void onCreateProperty(String reference, final PropertyRecord prop) {
        if(propertyWindow!=null && propertyWindow.isShowing())
        {
            propertyWindow.hide();
        }

        ModelNode address = new ModelNode();
        address.add("host", selectedHost);
        address.add("server-config", reference);
        address.add("system-property", prop.getKey());

        CreatePropertyCmd cmd = new CreatePropertyCmd(dispatcher, factory, address);
        cmd.execute(prop, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                loadServerConfigurations();
            }
        });
    }

    @Override
    public void onDeleteProperty(String reference, final PropertyRecord prop) {

        ModelNode address = new ModelNode();
        address.add("host", selectedHost);
        address.add("server-config", reference);
        address.add("system-property", prop.getKey());

        DeletePropertyCmd cmd = new DeletePropertyCmd(dispatcher,factory,address);
        cmd.execute(prop, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                loadServerConfigurations();
            }
        });
    }
    
    @Override
    public void onChangeProperty(String reference, PropertyRecord prop) {
        // do nothing
    }

    @Override
    public void launchNewPropertyDialoge(String reference) {
        propertyWindow = new DefaultWindow("New System Property");
        propertyWindow.setWidth(320);
        propertyWindow.setHeight(240);
        propertyWindow.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {

            }
        });

        propertyWindow.setWidget(
                new NewPropertyWizard(this, reference).asWidget()
        );

        propertyWindow.setGlassEnabled(true);
        propertyWindow.center();
    }

    @Override
    public void closePropertyDialoge() {
        propertyWindow.hide();
    }

    public void onShowEffectivePorts() {

        assert selectedRecord!=null : "No record selected!";

        window = new DefaultWindow("Server Ports");
        window.setWidth(480);
        window.setHeight(360);

        String socketBinding = selectedRecord.getSocketBinding()!=null ?
                selectedRecord.getSocketBinding() : "standard-sockets";

        LoadSocketBindingsCmd cmd = new LoadSocketBindingsCmd(dispatcher, factory, propertyMetaData, socketBinding);
        cmd.execute(new SimpleCallback<List<SocketBinding>>() {
            @Override
            public void onSuccess(List<SocketBinding> result) {
                window.setWidget(
                        new EffectivePortsDialogue(ServerConfigPresenter.this, result, selectedRecord).asWidget()
                );

                window.setGlassEnabled(true);
                window.center();
            }
        });
    }

    @Override
    public void launchWizard(String HostName) {
        launchNewConfigDialoge();
    }
}
