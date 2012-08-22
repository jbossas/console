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

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Random;
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
import org.jboss.as.console.client.core.DomainGateKeeper;
import org.jboss.as.console.client.core.MainLayoutPresenter;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.domain.events.StaleModelEvent;
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
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.as.console.client.shared.model.DeploymentStore;
import org.jboss.as.console.client.shared.viewframework.DmrCallback;
import org.jboss.dmr.client.ModelNode;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;


/**
 * @author Heiko Braun
 * @date 2/11/11
 */
public class DomainOverviewPresenter
        extends Presenter<DomainOverviewPresenter.MyView, DomainOverviewPresenter.MyProxy>
        implements StaleModelEvent.StaleModelListener {

    private final PlaceManager placeManager;
    private ProfileStore profileStore;
    private ServerGroupStore serverGroupStore;
    private DeploymentStore deploymentStore;
    private DispatchAsync dispatcher;
    private HostInformationStore hostInfo;
    private BeanFactory factory;

    @ProxyCodeSplit
    @NameToken(NameTokens.DomainOverviewPresenter)
    @UseGatekeeper( DomainGateKeeper.class )
    public interface MyProxy extends Proxy<DomainOverviewPresenter>, Place {
    }

    public interface MyView extends SuspendableView {
        void setPresenter(DomainOverviewPresenter presenter);
        void updateProfiles(List<ProfileRecord> profiles);
        void updateGroups(List<ServerGroupRecord> groups);
        void updateDeployments(List<DeploymentRecord> deploymentRecords);
        void updateHosts(List<HostInfo> hosts);
    }

    @Inject
    public DomainOverviewPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, ProfileStore profileStore,
            ServerGroupStore serverGroupStore,
            DispatchAsync dispatcher, HostInformationStore hostInfo,
            BeanFactory factory) {

        super(eventBus, view, proxy);
        this.placeManager = placeManager;
        this.profileStore = profileStore;
        this.serverGroupStore = serverGroupStore;
        this.dispatcher = dispatcher;
        this.hostInfo = hostInfo;
        this.factory = factory;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {
        super.prepareFromRequest(request);
        String profileName = request.getParameter("name", "none");
        Log.debug("requested profile: "+ profileName);

    }

    @Override
    protected void onReset() {
        loadHostsData();
    }

    private void loadHostsData() {

       /*hostInfo.getHosts(new SimpleCallback<List<Host>>() {
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
                               getView().updateHosts(hostInfos);

                           }
                       }
                   });
               }
           }
       });*/


       getView().updateHosts(generateFakeDomain());


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

    private void refreshGroups() {
        serverGroupStore.loadServerGroups(new SimpleCallback<List<ServerGroupRecord>>() {
            @Override
            public void onSuccess(List<ServerGroupRecord> result) {
                getView().updateGroups(result);
            }
        });
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), MainLayoutPresenter.TYPE_MainContent, this);
    }

    // --------------------------------

    @Override
    public void onStaleModel(String modelName) {
        if(modelName.equals(StaleModelEvent.SERVER_GROUPS))
        {
            //refreshGroups();
        }
    }
}
