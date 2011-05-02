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
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.core.message.Message;
import org.jboss.as.console.client.domain.events.StaleModelEvent;
import org.jboss.as.console.client.domain.groups.JvmManagement;
import org.jboss.as.console.client.domain.groups.NewPropertyWizard;
import org.jboss.as.console.client.domain.groups.PropertyManagement;
import org.jboss.as.console.client.domain.groups.PropertyRecord;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.Jvm;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerGroupStore;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.model.ModelAdapter;
import org.jboss.as.console.client.widgets.DefaultWindow;
import org.jboss.as.console.client.widgets.forms.PropertyBinding;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.dmr.client.ModelNode;

import java.util.List;
import java.util.Map;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 3/3/11
 */
public class ServerConfigPresenter extends Presenter<ServerConfigPresenter.MyView, ServerConfigPresenter.MyProxy>
        implements HostSelectionEvent.HostSelectionListener, JvmManagement, PropertyManagement {

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
            PropertyMetaData propertyMetaData) {
        super(eventBus, view, proxy);

        this.hostInfoStore = hostInfoStore;
        this.serverGroupStore = serverGroupStore;
        this.dispatcher = dispatcher;
        this.propertyMetaData = propertyMetaData;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
        getEventBus().addHandler(HostSelectionEvent.TYPE, this);
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
                            workOn(server);
                            serverName = selectedRecord.getName();

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
        window.setWidth(320);
        window.setHeight(240);
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
            }
        });
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
                            System.out.println("> stale model event");
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
        else
        {
            Console.warning("No changes applied!");
        }
    }


    public void editCurrentRecord() {
        getView().setEnabled(true);
    }

    public void deleteCurrentRecord() {
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
        ModelNode operation = new ModelNode();
        operation.get(OP).set(ADD);
        operation.get(ADDRESS).add("host", selectedHost);
        operation.get(ADDRESS).add("server-config", reference);
        operation.get(ADDRESS).add(JVM, jvm.getName());

        operation.get("heap-size").set(jvm.getHeapSize());
        operation.get("max-heap-size").set(jvm.getMaxHeapSize());
        operation.get("debug-enabled").set(jvm.isDebugEnabled());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                Console.info("Success: Created JVM settings");
                loadServerConfigurations();
            }
        });
    }

    @Override
    public void onDeleteJvm(String reference, Jvm jvm) {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(REMOVE);
        operation.get(ADDRESS).add("host", selectedHost);
        operation.get(ADDRESS).add("server-config", reference);
        operation.get(ADDRESS).add(JVM, jvm.getName());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                Console.info("Success: Removed JVM settings");
                loadServerConfigurations();
            }
        });
    }

    @Override
    public void onUpdateJvm(String reference, String jvmName, Map<String, Object> changedValues) {
        if(changedValues.size()>0)
        {
            ModelNode proto = new ModelNode();
            proto.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
            proto.get(ADDRESS).add("host", selectedHost);
            proto.get(ADDRESS).add("server-config", reference);
            proto.get(ADDRESS).add(JVM, jvmName);

            List<PropertyBinding> bindings = propertyMetaData.getBindingsForType(Jvm.class);
            ModelNode operation  = ModelAdapter.detypedFromChangeset(proto, changedValues, bindings);

            System.out.println(operation.toString());

            dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

                @Override
                public void onSuccess(DMRResponse result) {
                    Console.info("Success: Updated JVM settings");
                    loadServerConfigurations();
                }
            });
        }
        else
        {
            Console.warning("No changes applied!");
        }
    }

    @Override
    public void onCreateProperty(String reference, final PropertyRecord prop) {
        if(propertyWindow!=null && propertyWindow.isShowing())
        {
            propertyWindow.hide();
        }

        ModelNode operation = new ModelNode();
        operation.get(OP).set("add-system-property");
        operation.get(ADDRESS).add("host", selectedHost);
        operation.get(ADDRESS).add("server-config", reference);
        operation.get("name").set(prop.getKey());
        operation.get("value").set(prop.getValue());
        operation.get("boot-time").set(prop.isBootTime());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                Console.info("Success: Created property "+prop.getKey());
                loadServerConfigurations();
            }
        });
    }

    @Override
    public void onDeleteProperty(String reference, final PropertyRecord prop) {
        ModelNode operation = new ModelNode();
        operation.get(OP).set("remove-system-property");
        operation.get(ADDRESS).add("host", selectedHost);
        operation.get(ADDRESS).add("server-config", reference);
        operation.get("name").set(prop.getKey());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                Console.info("Success: Removed property "+prop.getKey());
                loadServerConfigurations();
            }
        });
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
}
