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

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Random;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.core.DomainGateKeeper;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.domain.DomainPresenter;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.ballroom.client.widgets.window.Feedback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
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


    private final HostInformationStore hostInfoStore;
    private final BeanFactory factory;

    @Inject
    public TopologyPresenter(final EventBus eventBus, final MyView view,
            final MyProxy proxy, final HostInformationStore hostInfoStore, final BeanFactory factory)
    {
        super(eventBus, view, proxy);
        this.hostInfoStore = hostInfoStore;
        this.factory = factory;
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
    protected void revealInParent()
    {
        RevealContentEvent.fire(getEventBus(), DomainPresenter.TYPE_MainContent, this);
    }


    private void loadHostsData()
    {
        hostInfoStore.getHosts(new SimpleCallback<List<Host>>()
        {
            @Override
            public void onSuccess(final List<Host> hosts)
            {
                final List<HostInfo> hostInfos = new ArrayList<HostInfo>();
                for (final Host host : hosts)
                {
                    hostInfoStore.getServerInstances(host.getName(), new SimpleCallback<List<ServerInstance>>()
                    {
                        @Override
                        public void onSuccess(List<ServerInstance> serverInstances)
                        {
                            HostInfo info = new HostInfo(host.getName(), host.isController());
                            info.setServerInstances(serverInstances);
                            hostInfos.add(info);
                            getView().updateHosts(hostInfos);
                        }
                    });
                }
            }
        });
        //        getView().updateHosts(generateFakeDomain());
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
                ServerInstance serverInstance = factory.serverInstance().as();
                serverInstance.setGroup(groupNames[groupIndex]);
                serverInstance.setRunning((groupIndex % 2 == 0));
                serverInstance.setName(groupNames[groupIndex] + "-" + x);
                serverInstance.setSocketBindings(Collections.<String, String>emptyMap());
                serverInstance.setInterfaces(Collections.<String, String>emptyMap());

                host.getServerInstances().add(serverInstance);
            }
            hostInfos.add(host);
        }
        return hostInfos;
    }

    public void onStartStopServer(final String hostName, final ServerInstance server)
    {
        final String next = server.isRunning() ? "stop" : "start";

        Feedback.confirm("Modify Server", "Do really want to " + next + " server " + server.getName() + "?",
                new Feedback.ConfirmationHandler()
                {
                    @Override
                    public void onConfirmation(boolean isConfirmed)
                    {
                        if (isConfirmed)
                        { System.out.println(next + " server " + server.getName() + " on host: " + hostName); }
                    }
                });
    }

    public void onStartStopGroup(final String hostName, final String group, boolean startIt)
    {
        final String next = startIt ? "start" : "stop";

        Feedback.confirm("Modify Server", "Do really want to " + next + " all servers in group " + group + "?",
                new Feedback.ConfirmationHandler()
                {
                    @Override
                    public void onConfirmation(boolean isConfirmed)
                    {
                        if (isConfirmed)
                        { System.out.println(next + " group " + group); }
                    }
                });
    }
}
