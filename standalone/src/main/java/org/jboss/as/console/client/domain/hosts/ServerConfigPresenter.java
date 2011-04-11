/*
 * JBoss, Home of Professional Open Source
 * Copyright <YEAR> Red Hat Inc. and/or its affiliates and other contributors
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
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerGroupStore;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.widgets.DefaultWindow;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/3/11
 */
public class ServerConfigPresenter extends Presenter<ServerConfigPresenter.MyView, ServerConfigPresenter.MyProxy>
        implements HostSelectionEvent.HostSelectionListener {

    private HostInformationStore hostInfoStore;

    private Server selectedRecord = null;
    private ServerGroupStore serverGroupStore;

    private String serverName;
    private String selectedHost;

    private DefaultWindow window = null;
    private List<ServerGroupRecord> serverGroups;


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
            ServerGroupStore serverGroupStore) {
        super(eventBus, view, proxy);

        this.hostInfoStore = hostInfoStore;
        this.serverGroupStore = serverGroupStore;
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
                            selectedRecord = server;
                            serverName = selectedRecord.getName();
                            getView().setSelectedRecord(selectedRecord);

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
                    selectedRecord = result.get(0);
                    serverName = selectedRecord.getName();
                    loadJVMs(hostName);
                    getView().setSelectedRecord(selectedRecord);
                }
            }
        });
    }

    public void createServerConfig(final Server newServer) {

        // close popup
        closeDialoge();

        System.out.println("> create server-config");
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
        selectedRecord = record;
        getView().setSelectedRecord(record);

    }

    public void onSaveChanges(Server updatedEntity) {

        Console.MODULES.getMessageCenter().notify(
                new Message("'Save' operation not implemented!", Message.Severity.Warning)
        );


        hostInfoStore.saveServerConfig(selectedHost, updatedEntity);
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

}
