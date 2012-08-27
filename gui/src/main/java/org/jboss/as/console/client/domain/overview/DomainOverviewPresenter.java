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

package org.jboss.as.console.client.domain.overview;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Random;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DomainGateKeeper;
import org.jboss.as.console.client.core.Header;
import org.jboss.as.console.client.core.MainLayoutPresenter;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ProfileStore;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerGroupStore;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.as.console.client.shared.model.DeploymentStore;
import org.jboss.as.console.client.shared.state.CurrentServerSelection;
import org.jboss.as.console.client.shared.state.ServerSelectionEvent;
import org.jboss.ballroom.client.widgets.window.Feedback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * @author Heiko Braun
 * @date 2/11/11
 */
public class DomainOverviewPresenter
        extends Presenter<DomainOverviewPresenter.MyView, DomainOverviewPresenter.MyProxy>
        implements ServerSelectionEvent.ServerSelectionListener {

    private final PlaceManager placeManager;
    private ProfileStore profileStore;
    private ServerGroupStore serverGroupStore;
    private DeploymentStore deploymentStore;
    private DispatchAsync dispatcher;
    private HostInformationStore hostInfo;
    private BeanFactory factory;
    private CurrentServerSelection serverSelection;
    private Header header;
    private ServerPanelReference preselectedServer;

    @ProxyCodeSplit
    @NameToken(NameTokens.DomainOverviewPresenter)
    @UseGatekeeper( DomainGateKeeper.class )
    public interface MyProxy extends Proxy<DomainOverviewPresenter>, Place {
    }

    public interface MyView extends SuspendableView {
        void setPresenter(DomainOverviewPresenter presenter);
        void updateHosts(List<HostInfo> hosts, ServerPanelReference preselectedServer);
    }

    @Inject
    public DomainOverviewPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, ProfileStore profileStore,
            ServerGroupStore serverGroupStore,
            DispatchAsync dispatcher, HostInformationStore hostInfo,
            BeanFactory factory, CurrentServerSelection serverSelection, Header header) {

        super(eventBus, view, proxy);
        this.placeManager = placeManager;
        this.profileStore = profileStore;
        this.serverGroupStore = serverGroupStore;
        this.dispatcher = dispatcher;
        this.hostInfo = hostInfo;
        this.factory = factory;
        this.serverSelection  = serverSelection;
        this.header = header;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
        getEventBus().addHandler(ServerSelectionEvent.TYPE, this);


        // synchronize preselection
        if(serverSelection.isSet())
        {
            this.preselectedServer = new ServerPanelReference(serverSelection.getHost(), serverSelection.getServer());
        }
    }

    @Override
    protected void onReset() {

        header.highlight(NameTokens.DomainOverviewPresenter);

        loadHostsData();
    }

    private void loadHostsData() {

        hostInfo.getHosts(new SimpleCallback<List<Host>>() {
            @Override
            public void onSuccess(final List<Host> hosts) {

                final List<HostInfo> hostInfos = new ArrayList<HostInfo>();

                for(final Host host : hosts)
                {
                    hostInfo.getServerInstances(host.getName(), new SimpleCallback<List<ServerInstance>>() {
                        @Override
                        public void onSuccess(List<ServerInstance> serverInstances) {

                            HostInfo info = new HostInfo(host.getName(), host.isController());
                            info.setServerInstances(serverInstances);

                            hostInfos.add(info);


                            if(hostInfos.size() == hosts.size())
                            {
                                // done

                                Collections.sort(hostInfos, new Comparator<HostInfo>() {
                                    @Override
                                    public int compare(HostInfo host, HostInfo host1) {
                                        return host.getName().compareTo(host1.getName());
                                    }
                                });

                                getView().updateHosts(hostInfos, preselectedServer);

                            }
                        }
                    });
                }
            }
        });


        //getView().updateHosts(generateFakeDomain());


    }

    private List<HostInfo> generateFakeDomain() {

        String[] hostNames =    new String[] {"lightning", "eeak-a-mouse", "dirty-harry"};
        String[] groupNames =   new String[] {"staging", "production", "messaging-back-server-test", "uat", "messaging", "backoffice", "starlight"};
        String[] profiles =     new String[] {"default", "default", "default", "messaging", "web", "full-ha", "full-ha"};

        final List<HostInfo> hostInfos = new ArrayList<HostInfo>();

        int numHosts = Random.nextInt(5) + 10;
        for(int i=0;i<numHosts; i++)
        {
            // host info
            String name = hostNames[Random.nextInt(2)]+"-"+i;
            boolean isController = (i<1);

            HostInfo host = new HostInfo(name, isController);
            host.setServerInstances(new ArrayList<ServerInstance>());

            // server instances
            for(int x = 0; x<(Random.nextInt(5)+1); x++)
            {
                int groupIndex = Random.nextInt(groupNames.length-1);
                ServerInstance serverInstance = factory.serverInstance().as();
                serverInstance.setGroup(groupNames[groupIndex]);
                serverInstance.setRunning((groupIndex%2==0));
                serverInstance.setName(groupNames[groupIndex]+"-"+x);

                host.getServerInstances().add(serverInstance);
            }

            hostInfos.add(host);
        }

        return hostInfos;
    }

    /*private void refreshGroups() {
        serverGroupStore.loadServerGroups(new SimpleCallback<List<ServerGroupRecord>>() {
            @Override
            public void onSuccess(List<ServerGroupRecord> result) {
                getView().updateGroups(result);
            }
        });
    } */

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), MainLayoutPresenter.TYPE_MainContent, this);
    }

    public void onSelectServer(final ServerPanelReference serverTuple) {


        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                Console.getEventBus().fireEvent(
                        new ServerSelectionEvent(
                                serverTuple.getHostName(),
                                serverTuple.getServer())
                );
            }
        });
    }

    @Override
    public void onServerSelection(String hostName, ServerInstance server, ServerSelectionEvent.Source source) {
        if(source.equals(ServerSelectionEvent.Source.Picker))
        {
            this.preselectedServer = new ServerPanelReference(hostName, server);
        }
    }

    public void onStartStopServer(final String hostName, final ServerInstance server) {
        final String next = server.isRunning() ?  "stop" : "start";

        Feedback.confirm("Modify Server", "Do really want to "+next+ " server "+server.getName()+"?",
                new Feedback.ConfirmationHandler() {
                    @Override
                    public void onConfirmation(boolean isConfirmed) {
                        if(isConfirmed)
                            System.out.println(next + " server "+server.getName() + " on host: " +hostName);
                    }
                });

    }

    public void onStartStopGroup(final String hostName, final String group, boolean startIt) {
        final String next = startIt ?  "start" : "stop";

        Feedback.confirm("Modify Server", "Do really want to "+next+ " all servers in group "+group+"?",
                new Feedback.ConfirmationHandler() {
                    @Override
                    public void onConfirmation(boolean isConfirmed) {
                        if(isConfirmed)
                            System.out.println(next + " group "+group);
                    }
                });
    }
}
