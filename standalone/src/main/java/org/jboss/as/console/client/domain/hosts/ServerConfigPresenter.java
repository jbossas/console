package org.jboss.as.console.client.domain.hosts;

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

    private boolean hasBeenRevealed = false;
    private String serverName;
    private String hostName;

    private DefaultWindow window = null;
    private List<ServerGroupRecord> serverGroups;


    @ProxyCodeSplit
    @NameToken(NameTokens.ServerPresenter)
    public interface MyProxy extends Proxy<ServerConfigPresenter>, Place {
    }

    public interface MyView extends SuspendableView {
        void setPresenter(ServerConfigPresenter presenter);
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
        hostName = request.getParameter("host", null);
        serverName = request.getParameter("server", null);
        String action= request.getParameter("action", null);

        if("new".equals(action))
        {
            serverName = null;
            selectedRecord = null;
            launchNewConfigDialoge();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();

        serverGroupStore.loadServerGroups(new SimpleCallback<List<ServerGroupRecord>>() {
            @Override
            public void onSuccess(List<ServerGroupRecord> result) {
                serverGroups = result;
                getView().updateServerGroups(result);
            }
        });

        serverGroupStore.loadSocketBindingGroupNames(new SimpleCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> result) {
                getView().updateSocketBindings(result);
            }
        });

        if(hostName!=null && serverName!=null)
        {
            loadJVMs(hostName);

            hostInfoStore.getServerConfigurations(hostName, new SimpleCallback<List<Server>>() {
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
                    hostName = result.get(0).getName();
                    loadDefaultForHost(hostName);
                }
            });
        }

        hasBeenRevealed = true;
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
        //loadDefaultForHost(hostName);
    }

    public void launchNewConfigDialoge() {
        window = new DefaultWindow("Create Server Configuration");
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
                new NewServerConfigWizard(this, serverGroups).asWidget() // TODO: fragile (serverGroups==null)
        );

        window.setGlassEnabled(true);
        window.center();
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
                if(!result.isEmpty() && hasBeenRevealed) {
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

        hostInfoStore.createServerConfig(hostName, newServer, new AsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean wasSuccessful) {
                if (wasSuccessful) {

                    Console.MODULES.getMessageCenter().notify(
                            new Message("Created server config " + newServer.getName())
                    );

                    getEventBus().fireEvent(new StaleModelEvent(StaleModelEvent.SERVER_CONFIGURATIONS));

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

}
