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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.core.message.Message;
import org.jboss.as.console.client.domain.events.HostSelectionEvent;
import org.jboss.as.console.client.domain.events.StaleModelEvent;
import org.jboss.as.console.client.domain.model.EntityFilter;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.Predicate;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.domain.runtime.DomainRuntimePresenter;
import org.jboss.as.console.client.shared.dispatch.AsyncCommand;
import org.jboss.as.console.client.shared.schedule.LongRunningTask;
import org.jboss.as.console.client.shared.state.CurrentHostSelection;
import org.jboss.ballroom.client.layout.LHSHighlightEvent;

import java.util.List;

/**
 * Manage server instances on a specific host.
 *
 * @author Heiko Braun
 * @date 3/8/11
 */
public class ServerInstancesPresenter extends Presenter<ServerInstancesPresenter.MyView, ServerInstancesPresenter.MyProxy>
        implements HostSelectionEvent.HostSelectionListener {

    private final PlaceManager placeManager;
    private HostInformationStore hostInfoStore;
    private EntityFilter<ServerInstance> filter = new EntityFilter<ServerInstance>();
    private List<ServerInstance> serverInstances;
    private CurrentHostSelection hostSelection;

    @ProxyCodeSplit
    @NameToken(NameTokens.InstancesPresenter)
    public interface MyProxy extends Proxy<ServerInstancesPresenter>, Place {
    }

    public interface MyView extends SuspendableView {
        void setPresenter(ServerInstancesPresenter presenter);
        void updateInstances(String hostName, List<ServerInstance> instances);
    }

    @Inject
    public ServerInstancesPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,
            HostInformationStore hostInfoStore, CurrentHostSelection hostSelection) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.hostInfoStore = hostInfoStore;
        this.hostSelection = hostSelection;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
        getEventBus().addHandler(HostSelectionEvent.TYPE, this);
    }

    @Override
    protected void onReset() {
        super.onReset();
        //refreshView();

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                getEventBus().fireEvent(
                        new LHSHighlightEvent(null, Console.CONSTANTS.common_label_serverInstances(), "domain-runtime")

                );
            }
        });

    }

    @Override
    protected void onReveal() {
        super.onReveal();
    }

    private void loadHostData() {

        if(!hostSelection.isSet())
            throw new RuntimeException("Host selection not set!");

        hostInfoStore.getServerInstances(hostSelection.getName(), new SimpleCallback<List<ServerInstance>>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new RuntimeException("", caught);
            }

            @Override
            public void onSuccess(List<ServerInstance> result) {
                serverInstances = result;
                getView().updateInstances(hostSelection.getName(), result);
            }
        });
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), DomainRuntimePresenter.TYPE_MainContent, this);
    }

    @Override
    public void onHostSelection(final String hostName) {

        // current host selection is set in DomainRuntimePresenter

        if(isVisible())
            loadHostData();
    }

    public void onFilterByGroup(String serverConfig) {

        List<ServerInstance> filtered = filter.apply(
                new ServerGroupPredicate(serverConfig),
                serverInstances
        );

        getView().updateInstances(hostSelection.getName(), filtered);
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

    public void startServer(final String hostName, final String serverName, final boolean startIt) {
        hostInfoStore.startServer(hostName, serverName, startIt, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(final Boolean wasSuccessful) {

                if(wasSuccessful)
                {
                    int limit = startIt ? 10:5;
                    LongRunningTask poll = new LongRunningTask(new AsyncCommand<Boolean>() {
                        @Override
                        public void execute(final AsyncCallback<Boolean> callback) {
                            hostInfoStore.getServerInstances(hostName, new SimpleCallback<List<ServerInstance>>() {
                                @Override
                                public void onSuccess(List<ServerInstance> result) {
                                    serverInstances = result;

                                    boolean keepPolling = false;

                                    for(ServerInstance instance : result) {
                                        if(instance.getServer().equals(serverName)) {

                                            if(startIt)
                                                keepPolling = !instance.isRunning();
                                            else
                                                keepPolling = instance.isRunning();

                                            break;
                                        }
                                    }

                                    // notify scheduler
                                    callback.onSuccess(keepPolling);

                                    if(!keepPolling) {

                                        /*String msg;
                                        if(startIt)
                                        {
                                            msg = wasSuccessful ?
                                                    "Successfully started server "+serverName :
                                                    "Failed to start server "+serverName;
                                        }
                                        else
                                        {
                                            msg = wasSuccessful ?
                                                    "Successfully stopped server "+serverName :
                                                    "Failed to stop server "+serverName;

                                        }

                                        Message.Severity sev = wasSuccessful ? Message.Severity.Info : Message.Severity.Error;
                                        Console.MODULES.getMessageCenter().notify(
                                                new Message(msg, sev)
                                        );  */
                                        getView().updateInstances(hostName, result);

                                    }
                                }
                            });
                        }
                    }, limit);

                    // kick of the polling request
                    poll.schedule(500);

                }

                // force reload of server selector (LHS nav)
                getEventBus().fireEvent(new StaleModelEvent(StaleModelEvent.SERVER_INSTANCES));

            }
        });
    }
}
