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
package org.jboss.as.console.client.domain.topology;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.core.DomainGateKeeper;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.domain.DomainPresenter;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerGroupStore;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.domain.model.impl.LifecycleOperation;
import org.jboss.as.console.client.domain.model.impl.ServerInstanceLifecycleCallback;
import org.jboss.as.console.client.shared.BeanFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.jboss.as.console.client.domain.model.ServerFlag.RELOAD_REQUIRED;
import static org.jboss.as.console.client.domain.model.ServerFlag.RESTART_REQUIRED;
import static org.jboss.as.console.client.domain.model.impl.LifecycleOperation.START;

/**
 * TODO Remove fake code when in production
 *
 * @author Harald Pehl
 * @date 10/15/12
 */
public class TopologyPresenter extends
        Presenter<TopologyPresenter.MyView, TopologyPresenter.MyProxy>
{
    @ProxyCodeSplit
    @NameToken(NameTokens.Topology)
    @UseGatekeeper(DomainGateKeeper.class)
    public interface MyProxy extends Proxy<TopologyPresenter>, Place
    {

    }


    public interface MyView extends SuspendableView
    {
        void setPresenter(TopologyPresenter presenter);

        void updateHosts(List<HostInfo> hosts);
    }


    private final ServerGroupStore serverGroupStore;
    private final HostInformationStore hostInfoStore;
    private final BeanFactory beanFactory;
    private boolean fake;


    @Inject
    public TopologyPresenter(final EventBus eventBus, final MyView view,
            final MyProxy proxy, final HostInformationStore hostInfoStore,
            final ServerGroupStore serverGroupStore, final BeanFactory beanFactory)
    {
        super(eventBus, view, proxy);
        this.serverGroupStore = serverGroupStore;
        this.hostInfoStore = hostInfoStore;
        this.beanFactory = beanFactory;
    }

    @Override
    protected void onBind()
    {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    protected void onReset()
    {
        super.onReset();
        loadHostsData();
    }

    @Override
    public void prepareFromRequest(final PlaceRequest request)
    {
        super.prepareFromRequest(request);
        fake = Boolean.valueOf(request.getParameter("fake", "false"));
    }

    @Override
    protected void revealInParent()
    {
        RevealContentEvent.fire(this, DomainPresenter.TYPE_MainContent, this);
    }

    private void loadHostsData()
    {
        if (fake)
        {
            getView().updateHosts(generateFakeDomain());
        }
        else
        {
            hostInfoStore.getHosts(new SimpleCallback<List<Host>>()
            {
                @Override
                public void onSuccess(final List<Host> hosts)
                {
                    // The command is used to defer the call to getView().updateHosts()
                    // until all server instances of all hosts are available
                    final Command cmd = new Command()
                    {
                        int numRequests = 0;
                        int numResponses = 0;
                        final List<HostInfo> hostInfos = new ArrayList<HostInfo>();

                        @Override
                        public void execute()
                        {
                            for (final Host host : hosts)
                            {
                                numRequests++;
                                hostInfoStore.getServerInstances(host.getName(), new SimpleCallback<List<ServerInstance>>()
                                {
                                    @Override
                                    public void onFailure(final Throwable caught)
                                    {
                                        numResponses++;
                                        HostInfo info = new HostInfo(host.getName(), host.isController());
                                        info.setServerInstances(Collections.<ServerInstance>emptyList());
                                        hostInfos.add(info);
                                        checkComplete();
                                    }

                                    @Override
                                    public void onSuccess(List<ServerInstance> serverInstances)
                                    {
                                        numResponses++;
                                        HostInfo info = new HostInfo(host.getName(), host.isController());
                                        info.setServerInstances(serverInstances);
                                        hostInfos.add(info);
                                        checkComplete();
                                    }
                                });
                            }
                        }

                        private void checkComplete()
                        {
                            if(numRequests == numResponses)
                            {
                                getView().updateHosts(hostInfos);
                            }
                        }
                    };
                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand()
                    {
                        @Override
                        public void execute()
                        {
                            cmd.execute();
                        }
                    });
                }
            });
        }
    }

    private List<HostInfo> generateFakeDomain()
    {
        String[] hostNames = new String[]{"lightning", "eeak-a-mouse", "dirty-harry"};
        String[] groupNames = new String[]{"staging", "production", "messaging-back-server-test", "uat", "messaging", "backoffice", "starlight"};
        String[] profiles = new String[]{"default", "default", "default", "messaging", "web", "full-ha", "full-ha"};

        final List<HostInfo> hostInfos = new ArrayList<HostInfo>();
        int numHosts = Random.nextInt(5) + 10;

        for (int i = 0; i < numHosts; i++)
        {
            // host info
            String name = hostNames[Random.nextInt(2)] + "-" + i;
            boolean isController = (i < 1);

            HostInfo host = new HostInfo(name, isController);
            host.setServerInstances(new ArrayList<ServerInstance>());

            // server instances
            for (int x = 0; x < (Random.nextInt(5) + 1); x++)
            {
                int groupIndex = Random.nextInt(groupNames.length - 1);
                ServerInstance serverInstance = beanFactory.serverInstance().as();
                serverInstance.setGroup(groupNames[groupIndex]);
                serverInstance.setRunning((groupIndex % 2 == 0));
                if (serverInstance.isRunning())
                {
                    if (Random.nextBoolean())
                    {
                        serverInstance.setFlag(Random.nextBoolean() ? RESTART_REQUIRED : RELOAD_REQUIRED);
                    }
                    else
                    {
                        serverInstance.setFlag(null);
                    }
                }
                serverInstance.setName(groupNames[groupIndex] + "-" + x);
                serverInstance.setSocketBindings(Collections.<String, String>emptyMap());
                serverInstance.setInterfaces(Collections.<String, String>emptyMap());

                host.getServerInstances().add(serverInstance);
            }
            hostInfos.add(host);
        }
        return hostInfos;
    }

    public void onServerInstanceLifecycle(final String host, final String server, final LifecycleOperation op)
    {
        ServerInstanceLifecycleCallback lifecycleCallback = new ServerInstanceLifecycleCallback(
                hostInfoStore, host, server, op, new SimpleCallback<Server>()
                {
                    @Override
                    public void onSuccess(final Server server)
                    {
                        loadHostsData();
                    }
                });
        switch (op)
        {
            case START:
                hostInfoStore.startServer(host, server, true, lifecycleCallback);
                break;
            case STOP:
                hostInfoStore.startServer(host, server, false, lifecycleCallback);
                break;
            case RELOAD:
                hostInfoStore.reloadServer(host, server, lifecycleCallback);
                break;
        }
    }

    public void onGroupLifecycle(final String group, final LifecycleOperation op)
    {
        if (op == START)
        {
            serverGroupStore.startServerGroup(group, new AsyncCallback<Boolean>()
            {
                @Override
                public void onFailure(final Throwable caught)
                {

                }

                @Override
                public void onSuccess(final Boolean result)
                {
                    loadHostsData();
                }
            });
        }
    }
}
