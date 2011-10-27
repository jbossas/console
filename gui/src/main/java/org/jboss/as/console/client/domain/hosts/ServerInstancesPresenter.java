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
import com.google.gwt.event.shared.EventBus;
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
import org.jboss.as.console.client.domain.model.EntityFilter;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.Predicate;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.ballroom.client.layout.LHSHighlightEvent;

import java.util.Collections;
import java.util.List;

/**
 * Manage server instances on a specific host.
 *
 * @author Heiko Braun
 * @date 3/8/11
 */
public class ServerInstancesPresenter extends Presenter<ServerInstancesPresenter.MyView, ServerInstancesPresenter.MyProxy>
        implements HostSelectionEvent.HostSelectionListener, StaleModelEvent.StaleModelListener {

    private final PlaceManager placeManager;
    private HostInformationStore hostInfoStore;
    private String selectedHost = null;
    private EntityFilter<ServerInstance> filter = new EntityFilter<ServerInstance>();
    private List<ServerInstance> serverInstances;
    private boolean hasBeenRevealed = false;

    public String getCurrentHostSelection() {
        return selectedHost;
    }


    @ProxyCodeSplit
    @NameToken(NameTokens.InstancesPresenter)
    public interface MyProxy extends Proxy<ServerInstancesPresenter>, Place {
    }

    public interface MyView extends SuspendableView {
        void setPresenter(ServerInstancesPresenter presenter);
        void setSelectedHost(String selectedHost);
        void updateInstances(List<ServerInstance> instances);
        void updateServerConfigurations(List<Server> servers);
    }

    @Inject
    public ServerInstancesPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,
            HostInformationStore hostInfoStore) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.hostInfoStore = hostInfoStore;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
        getEventBus().addHandler(HostSelectionEvent.TYPE, this);
        getEventBus().addHandler(StaleModelEvent.TYPE, this);
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {
        selectedHost = request.getParameter("host", null);
    }

    @Override
    protected void onReset() {
        super.onReset();
        refreshView();

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                getEventBus().fireEvent(
                        new LHSHighlightEvent(null, Console.CONSTANTS.common_label_runtimeStatus(), "hosts")

                );
            }
        });

    }

    @Override
    protected void onReveal() {
        super.onReveal();
        hasBeenRevealed = true;
    }

    private void refreshView() {

        // host selection event may cause this
        if(!hasBeenRevealed) return;

        if(null== selectedHost)
        {
            // fallback
            hostInfoStore.getHosts(new SimpleCallback<List<Host>>() {
                @Override
                public void onSuccess(List<Host> result) {

                    loadHostData(result.get(0).getName());
                }
            });
        }
        else
        {
            loadHostData(selectedHost);
        }
    }

    private void loadHostData(String hostName) {

        selectedHost = hostName;

        getView().setSelectedHost(hostName);

        hostInfoStore.getServerInstances(hostName, new SimpleCallback<List<ServerInstance>>() {
            @Override
            public void onSuccess(List<ServerInstance> result) {
                serverInstances = result;
                getView().updateInstances(result);
            }
        });

        hostInfoStore.getServerConfigurations(hostName, new SimpleCallback<List<Server>>() {
            @Override
            public void onSuccess(List<Server> result) {
                getView().updateServerConfigurations(result);

                if(result.isEmpty())
                {
                    // hacky: See HostInfoStore#getServerInstances()
                    getView().updateInstances(Collections.EMPTY_LIST);
                }
            }
        });
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), HostMgmtPresenter.TYPE_MainContent, this);
    }

    @Override
    public void onHostSelection(String hostName) {
        selectedHost = hostName;
        refreshView();
    }

    public void onFilterByGroup(String serverConfig) {

        List<ServerInstance> filtered = filter.apply(
                new ServerGroupPredicate(serverConfig),
                serverInstances
        );

        getView().updateInstances(filtered);
    }

    class ServerGroupPredicate implements Predicate<ServerInstance> {
        private String groupFilter;

        ServerGroupPredicate(String filter) {
            this.groupFilter = filter;
        }

        @Override
        public boolean appliesTo(ServerInstance candidate) {

            boolean configMatch = groupFilter.equals("") ?
                    true : candidate.getGroup().equals(groupFilter);

            return configMatch;
        }
    }

    public void startServer(final String configName, final boolean startIt) {
        hostInfoStore.startServer(selectedHost, configName, startIt, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean wasSuccessful) {

                String msg;
                if(startIt)
                {
                    msg = wasSuccessful ?
                            "Successfully started server "+configName :
                            "Failed to start server "+configName;
                }
                else
                {
                    msg = wasSuccessful ?
                            "Successfully stopped server "+configName :
                            "Failed to stop server "+configName;

                }

                Message.Severity sev = wasSuccessful ? Message.Severity.Info : Message.Severity.Error;
                Console.MODULES.getMessageCenter().notify(
                        new Message(msg, sev)
                );

                if(wasSuccessful)
                {
                    // if the operation was success we merge the local state changes into the model
                    // to avoid a polling request (server started async)

                    hostInfoStore.getServerInstances(selectedHost, new SimpleCallback<List<ServerInstance>>() {
                        @Override
                        public void onSuccess(List<ServerInstance> result) {
                            serverInstances = result;

                            // merge local state
                            for(ServerInstance instance : result)
                                if(instance.getServer().equals(configName)) instance.setRunning(startIt);

                            getView().updateInstances(result);
                        }
                    });
                }
            }
        });
    }

    public void reloadServer(final String server) {
        hostInfoStore.reloadServer(selectedHost, server, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if(result)
                    Console.info("Success: Reload server configuration " + server);
                else
                    Console.error("Error: Failed to reload server configuration " + server);
            }
        });
    }

    @Override
    public void onStaleModel(String modelName) {
        if(StaleModelEvent.SERVER_CONFIGURATIONS.equals(modelName))
            refreshView();
    }
}
