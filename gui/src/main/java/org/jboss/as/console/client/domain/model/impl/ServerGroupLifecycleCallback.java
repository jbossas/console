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
package org.jboss.as.console.client.domain.model.impl;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.domain.topology.HostInfo;
import org.jboss.as.console.client.shared.dispatch.AsyncCommand;
import org.jboss.as.console.client.shared.schedule.LongRunningTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.jboss.as.console.client.domain.model.impl.LifecycleOperation.RELOAD;
import static org.jboss.as.console.client.domain.model.impl.LifecycleOperation.START;

/**
 * @author Harald Pehl
 * @date 10/23/2012
 */
public class ServerGroupLifecycleCallback extends SimpleCallback<Boolean>
{
    int servers;
    private final Map<HostInfo, List<ServerInstance>> serversPerHost;
    private final LifecycleOperation lifecycleOp;
    private final HostInformationStore hostInfoStore;
    private final SimpleCallback<List<Server>> callback;

    public ServerGroupLifecycleCallback(final HostInformationStore hostInfoStore,
            final Map<HostInfo, List<ServerInstance>> serversPerHost,
            final LifecycleOperation lifecycleOp, final SimpleCallback<List<Server>> callback)
    {
        this.hostInfoStore = hostInfoStore;
        this.serversPerHost = serversPerHost;
        this.lifecycleOp = lifecycleOp;
        this.callback = callback;

        this.servers = 0;
        for (List<ServerInstance> serverInstances : serversPerHost.values())
        {
            servers += serverInstances.size();
        }
    }


    @Override
    public void onSuccess(final Boolean wasSuccessful)
    {
        if (wasSuccessful)
        {
            LongRunningTask poll = new LongRunningTask(new AsyncCommand<Boolean>()
            {
                @Override
                public void execute(final AsyncCallback<Boolean> callback)
                {
                    final List<Server> finishedServers = new ArrayList<Server>();

                    for (Map.Entry<HostInfo, List<ServerInstance>> entry : serversPerHost
                            .entrySet())
                    {
                        HostInfo hostInfo = entry.getKey();
                        List<ServerInstance> serverInstances = entry.getValue();
                        for (ServerInstance serverInstance : serverInstances)
                        {
                            hostInfoStore.getServerConfiguration(hostInfo.getName(), serverInstance.getServer(),
                                    new SimpleCallback<Server>()
                                    {
                                        @Override
                                        public void onSuccess(final Server server)
                                        {
                                            finishedServers.add(server);
                                            boolean keepPolling = lifecycleOp == START || lifecycleOp == RELOAD ? !server
                                                    .isStarted() : server.isStarted();
                                            if (!keepPolling && finishedServers.size() == servers)
                                            {
                                                ServerGroupLifecycleCallback.this.callback.onSuccess(finishedServers);
                                            }
                                            callback.onSuccess(keepPolling);
                                        }
                                    });
                        }
                    }
                }
            }, lifecycleOp.limit());
            poll.schedule(500);
        }
    }
}
