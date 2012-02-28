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
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.core.DomainGateKeeper;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.domain.events.StaleModelEvent;
import org.jboss.as.console.client.domain.model.EntityFilter;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.Predicate;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.domain.runtime.DomainRuntimePresenter;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.AsyncCommand;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.runtime.RuntimeBaseAddress;
import org.jboss.as.console.client.shared.schedule.LongRunningTask;
import org.jboss.as.console.client.shared.state.CurrentServerSelection;
import org.jboss.as.console.client.shared.state.ReloadState;
import org.jboss.as.console.client.shared.state.ServerSelectionEvent;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;


/**
 * Manage server instances on a specific host.
 *
 * @author Heiko Braun
 * @date 3/8/11
 */
public class ServerInstancesPresenter extends Presenter<ServerInstancesPresenter.MyView, ServerInstancesPresenter.MyProxy>
        implements ServerSelectionEvent.ServerSelectionListener {

    private final PlaceManager placeManager;
    private HostInformationStore hostInfoStore;
    private EntityFilter<ServerInstance> filter = new EntityFilter<ServerInstance>();
    private List<ServerInstance> serverInstances;
    private ReloadState reloadState;
    private CurrentServerSelection serverSelection;
    private DispatchAsync dispatcher;
    private BeanFactory factory;

    @ProxyCodeSplit
    @NameToken(NameTokens.InstancesPresenter)
    @UseGatekeeper( DomainGateKeeper.class )
    public interface MyProxy extends Proxy<ServerInstancesPresenter>, Place {
    }

    public interface MyView extends SuspendableView {
        void setPresenter(ServerInstancesPresenter presenter);
        void updateInstances(String hostName, List<ServerInstance> instances);

        void setEnvironment(List<PropertyRecord> environment);

        void mergeUpdatedInstance(String name, boolean started);
    }

    @Inject
    public ServerInstancesPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,
            HostInformationStore hostInfoStore, CurrentServerSelection serverSelection,
            ReloadState reloadState, DispatchAsync dispatcher, BeanFactory factory) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.hostInfoStore = hostInfoStore;
        this.serverSelection = serverSelection;
        this.reloadState = reloadState;
        this.dispatcher = dispatcher;
        this.factory = factory;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
        getEventBus().addHandler(ServerSelectionEvent.TYPE, this);
    }

    @Override
    protected void onReset() {
        super.onReset();

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                if(serverSelection.getHost()!=null)
                    loadHostData();
            }
        });

    }

    private void loadHostData() {

        if(!serverSelection.isSet())
            throw new RuntimeException("Host selection not set!");

        hostInfoStore.getServerInstances(serverSelection.getHost(), new SimpleCallback<List<ServerInstance>>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new RuntimeException("", caught);
            }

            @Override
            public void onSuccess(List<ServerInstance> result) {
                serverInstances = result;
                getView().updateInstances(serverSelection.getHost(), result);
            }
        });
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), DomainRuntimePresenter.TYPE_MainContent, this);
    }

    @Override
    public void onServerSelection(String hostName, ServerInstance server) {
        if(isVisible() && serverSelection.isSet())
        {
            loadHostData();
        }
    }

    public void onFilterByGroup(String serverConfig) {

        List<ServerInstance> filtered = filter.apply(
                new ServerGroupPredicate(serverConfig),
                serverInstances
        );

        getView().updateInstances(serverSelection.getHost(), filtered);
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

        reloadState.resetServer(serverName);

        hostInfoStore.startServer(hostName, serverName, startIt, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(final Boolean wasSuccessful) {

                if(wasSuccessful)
                {
                    int limit = startIt ? 15:5;
                    LongRunningTask poll = new LongRunningTask(new AsyncCommand<Boolean>() {
                        @Override
                        public void execute(final AsyncCallback<Boolean> callback) {


                            hostInfoStore.getServerConfiguration(hostName, serverName, new SimpleCallback<Server>() {
                                @Override
                                public void onSuccess(final Server server) {

                                    boolean keepPolling = false;

                                    if(startIt)
                                        keepPolling = !server.isStarted();
                                    else
                                        keepPolling = server.isStarted();


                                    if(!keepPolling) {

                                        getView().mergeUpdatedInstance(server.getName(), server.isStarted());

                                        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                                            @Override
                                            public void execute() {

                                                // force reload of server selector (LHS nav)
                                                getEventBus().fireEvent(new StaleModelEvent(StaleModelEvent.SERVER_INSTANCES));
                                            }
                                        });

                                    }

                                    // notify scheduler
                                    callback.onSuccess(keepPolling);
                                }
                            });

                        }
                    }, limit);

                    // kick of the polling request
                    poll.schedule(500);

                }
            }
        });
    }

    public void loadEnvironment(ServerInstance selectedObject) {
        // /host=master/server=server-one/core-service=platform-mbean/type=runtime:read-attribute(name=system-properties)

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(RuntimeBaseAddress.get());
        operation.get(ADDRESS).add("core-service", "platform-mbean");
        operation.get(ADDRESS).add("type", "runtime");
        operation.get(OP).set(READ_ATTRIBUTE_OPERATION);
        operation.get(NAME).set("system-properties");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if(!response.isFailure())
                {

                    List<Property> properties = response.get(RESULT).asPropertyList();
                    List<PropertyRecord> environment = new ArrayList<PropertyRecord>(properties.size());

                    for(Property prop : properties)
                    {
                        PropertyRecord model = factory.property().as();
                        model.setKey(prop.getName());
                        model.setValue(prop.getValue().asString());

                        environment.add(model);

                    }

                    getView().setEnvironment(environment);
                }
            }
        });

    }
}
