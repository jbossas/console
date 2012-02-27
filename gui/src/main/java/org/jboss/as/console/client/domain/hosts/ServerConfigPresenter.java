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
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DomainGateKeeper;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.core.message.Message;
import org.jboss.as.console.client.domain.events.HostSelectionEvent;
import org.jboss.as.console.client.domain.events.StaleModelEvent;
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
import org.jboss.as.console.client.shared.model.ModelAdapter;
import org.jboss.as.console.client.shared.properties.CreatePropertyCmd;
import org.jboss.as.console.client.shared.properties.DeletePropertyCmd;
import org.jboss.as.console.client.shared.properties.NewPropertyWizard;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.state.CurrentHostSelection;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.PropertyBinding;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.dmr.client.ModelDescriptionConstants;
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
    private ServerGroupStore serverGroupStore;

    private CurrentHostSelection hostSelection;

    private DefaultWindow window = null;
    private List<ServerGroupRecord> serverGroups;

    private DefaultWindow propertyWindow;
    private DispatchAsync dispatcher;
    private ApplicationMetaData propertyMetaData;
    private BeanFactory factory;
    private PlaceManager placeManager;

    private LoadSocketBindingsCmd loadSocketCmd;

    @ProxyCodeSplit
    @NameToken(NameTokens.ServerPresenter)
    @UseGatekeeper( DomainGateKeeper.class )
    public interface MyProxy extends Proxy<ServerConfigPresenter>, Place {
    }

    public interface MyView extends SuspendableView {
        void setPresenter(ServerConfigPresenter presenter);
        void updateSocketBindings(List<String> result);
        void setJvm(String reference, Jvm jvm);
        void setProperties(String reference, List<PropertyRecord> properties);
        void setPorts(String socketBinding, Server selectedRecord, List<SocketBinding> result);
        void setConfigurations(String selectedHost, List<Server> servers, String selectedConfigName);

        void setGroups(List<ServerGroupRecord> result);
    }

    @Inject
    public ServerConfigPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            HostInformationStore hostInfoStore,
            ServerGroupStore serverGroupStore,
            DispatchAsync dispatcher,
            ApplicationMetaData propertyMetaData, BeanFactory factory,
            PlaceManager placeManager, CurrentHostSelection hostSelection) {
        super(eventBus, view, proxy);

        this.hostInfoStore = hostInfoStore;
        this.serverGroupStore = serverGroupStore;
        this.dispatcher = dispatcher;
        this.propertyMetaData = propertyMetaData;
        this.factory = factory;
        this.placeManager = placeManager;
        this.hostSelection = hostSelection;

        this.loadSocketCmd = new LoadSocketBindingsCmd(
                dispatcher, factory, propertyMetaData
        );
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
        loadSocketBindings();
    }

    private void loadSocketBindings() {
        serverGroupStore.loadSocketBindingGroupNames(new SimpleCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> result) {
                getView().updateSocketBindings(result);

                // step2
                loadServerConfigurations(null);
            }
        });
    }

    private void loadServerConfigurations(final String selectedConfigName) {

        if(!hostSelection.isSet())
            throw new RuntimeException("Host selection not set!");


        hostInfoStore.getServerConfigurations(hostSelection.getName(), new SimpleCallback<List<Server>>() {
            @Override
            public void onSuccess(List<Server> result) {
                getView().setConfigurations(hostSelection.getName(), result, selectedConfigName);
            }
        });


        serverGroupStore.loadServerGroups(new SimpleCallback<List<ServerGroupRecord>>() {
            @Override
            public void onSuccess(List<ServerGroupRecord> result) {
                getView().setGroups(result);
            }
        });

    }


    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), HostMgmtPresenter.TYPE_MainContent, this);
    }

    @Override
    public void onHostSelection(String hostName) {

        if(isVisible())
            loadServerConfigurations(null);
    }

    public void launchNewConfigDialoge() {

        window = new DefaultWindow(Console.MESSAGES.createTitle("Server Configuration"));
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

    public void createServerConfig(final Server newServer) {

        // close popup
        closeDialoge();
        final String newServerName = newServer.getName();


        hostInfoStore.createServerConfig(getSelectedHost(), newServer, new AsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean wasSuccessful) {
                if (wasSuccessful) {

                    Console.info(Console.MESSAGES.added("Server Configuration ") + newServer.getName());

                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            getEventBus().fireEvent(new StaleModelEvent(StaleModelEvent.SERVER_CONFIGURATIONS));
                        }
                    });

                    loadServerConfigurations(newServerName);

                } else {
                    closeDialoge();
                    Console.error(Console.MESSAGES.addingFailed("Server Configuration ") + newServer.getName());

                }

            }

            @Override
            public void onFailure(Throwable caught) {

                Console.MODULES.getMessageCenter().notify(
                        new Message(Console.MESSAGES.addingFailed("Server Configuration ") + newServer.getName(), Message.Severity.Error)
                );

            }
        });
    }

    public void onSaveChanges(final Server entity, Map<String, Object> changedValues) {

        //System.out.println(changedValues);

        if(changedValues.containsKey("portOffset"))
            changedValues.put("socketBinding", entity.getSocketBinding());

        if(changedValues.containsKey("socketBinding"))
            changedValues.put("portOffset", entity.getPortOffset());

        final String name = entity.getName();

        ModelNode proto = new ModelNode();
        proto.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
        proto.get(ADDRESS).add("host", hostSelection.getName());
        proto.get(ADDRESS).add(ModelDescriptionConstants.SERVER_CONFIG, name);

        List<PropertyBinding> bindings = propertyMetaData.getBindingsForType(Server.class);
        ModelNode operation  = ModelAdapter.detypedFromChangeset(proto, changedValues, bindings);

        //System.out.println(operation);

        // TODO: https://issues.jboss.org/browse/AS7-3643

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                System.out.println(response);

                if(response.isFailure())
                {
                    Console.error(Console.MESSAGES.modificationFailed("Server Configuration ") +name, response.getFailureDescription());

                }
                else
                {
                    Console.info(Console.MESSAGES.modified("Server Configuration ") +name);
                }

                loadServerConfigurations(name);
            }
        });

    }


    public void tryDelete(final Server server) {

        // check if instance exist
        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).add("host", hostSelection.getName());
        operation.get(ADDRESS).add("server", server.getName());
        operation.get(OP).set(READ_RESOURCE_OPERATION);

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable throwable) {
                performDeleteOperation(server);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                String outcome = response.get(OUTCOME).asString();

                Boolean serverIsRunning = outcome.equals(SUCCESS) ? Boolean.TRUE : Boolean.FALSE;
                if(!serverIsRunning)
                    performDeleteOperation(server);
                else
                    Console.error(
                            Console.MESSAGES.deletionFailed("Server Configuration"),
                            Console.MESSAGES.server_config_stillRunning(server.getName())
                    );
            }
        });


    }

    private void performDeleteOperation(final Server server) {

        hostInfoStore.deleteServerConfig(hostSelection.getName(), server, new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable caught) {
                Console.MODULES.getMessageCenter().notify(
                        new Message(Console.MESSAGES.deletionFailed("Server Configuration ")+server.getName(), Message.Severity.Error)
                );
            }

            @Override
            public void onSuccess(Boolean wasSuccessful) {
                if(wasSuccessful)
                {
                    Console.MODULES.getMessageCenter().notify(
                            new Message(Console.MESSAGES.deleted("Server Configuration ")+server.getName())
                    );

                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            getEventBus().fireEvent(new StaleModelEvent(StaleModelEvent.SERVER_CONFIGURATIONS));
                        }
                    });


                    loadServerConfigurations(null);
                }
                else
                {
                    Console.MODULES.getMessageCenter().notify(
                            new Message(Console.MESSAGES.deleted("Server Configuration ")+server.getName(), Message.Severity.Error)
                    );
                }
            }
        });
    }

    public String getSelectedHost() {
        return hostSelection.getName();
    }


    @Override
    public void onCreateJvm(String reference, Jvm jvm) {
        ModelNode address = new ModelNode();
        address.add("host", hostSelection.getName());
        address.add("server-config", reference);
        address.add(JVM, jvm.getName());
        final String selectedConfigName = reference;

        CreateJvmCmd cmd = new CreateJvmCmd(dispatcher, factory, address);
        cmd.execute(jvm, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                loadServerConfigurations(selectedConfigName);
            }
        });
    }

    @Override
    public void onDeleteJvm(String reference, Jvm jvm) {

        ModelNode address = new ModelNode();
        address.add("host", hostSelection.getName());
        address.add("server-config", reference);
        address.add(JVM, jvm.getName());
        final String selectedConfigName = reference;

        DeleteJvmCmd cmd = new DeleteJvmCmd(dispatcher, factory, address);
        cmd.execute(new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                loadServerConfigurations(selectedConfigName);
            }
        });

    }

    @Override
    public void onUpdateJvm(String reference, String jvmName, Map<String, Object> changedValues) {

        if(changedValues.size()>0)
        {
            ModelNode address = new ModelNode();
            address.add("host", hostSelection.getName());
            address.add("server-config", reference);
            address.add(JVM, jvmName);
            final String selectedConfigName = reference;

            UpdateJvmCmd cmd = new UpdateJvmCmd(dispatcher, factory, propertyMetaData, address);
            cmd.execute(changedValues, new SimpleCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    loadServerConfigurations(selectedConfigName);
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
        address.add("host", hostSelection.getName());
        address.add("server-config", reference);
        address.add("system-property", prop.getKey());
        final String selectedConfigName = reference;

        CreatePropertyCmd cmd = new CreatePropertyCmd(dispatcher, factory, address);
        cmd.execute(prop, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                loadServerConfigurations(selectedConfigName);
            }
        });
    }

    @Override
    public void onDeleteProperty(String reference, final PropertyRecord prop) {

        ModelNode address = new ModelNode();
        address.add("host", hostSelection.getName());
        address.add("server-config", reference);
        address.add("system-property", prop.getKey());
        final String selectedConfigName = reference;

        DeletePropertyCmd cmd = new DeletePropertyCmd(dispatcher,factory,address);
        cmd.execute(prop, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                loadServerConfigurations(selectedConfigName);
            }
        });
    }

    @Override
    public void onChangeProperty(String reference, PropertyRecord prop) {
        // do nothing
    }

    @Override
    public void launchNewPropertyDialoge(String reference) {
        propertyWindow = new DefaultWindow(Console.MESSAGES.createTitle("System Property"));
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

    public void loadPorts(final Server server) {

        if(server.getSocketBinding()!=null &&
                !server.getSocketBinding().equals(""))
        {

            loadSocketCmd.execute(server.getSocketBinding(),
                    new SimpleCallback<List<SocketBinding>>() {
                        @Override
                        public void onSuccess(List<SocketBinding> result) {

                            getView().setPorts(server.getSocketBinding(), server, result);
                        }
                    }
            );
        }

    }

    @Override
    public void launchWizard(String HostName) {
        launchNewConfigDialoge();
    }

    public void loadJVMConfiguration(final Server server) {
        hostInfoStore.loadJVMConfiguration(hostSelection.getName(), server, new SimpleCallback<Jvm>() {
            @Override
            public void onSuccess(Jvm jvm) {

                getView().setJvm(server.getName(), jvm);
            }
        });
    }

    public void loadProperties(final Server server) {
        hostInfoStore.loadProperties(hostSelection.getName(), server, new SimpleCallback<List<PropertyRecord>>() {
            @Override
            public void onSuccess(List<PropertyRecord> properties) {
                getView().setProperties(server.getName(), properties);
            }
        });
    }
}