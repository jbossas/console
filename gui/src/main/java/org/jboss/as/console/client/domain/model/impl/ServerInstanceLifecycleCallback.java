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
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.events.StaleModelEvent;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.dmr.client.dispatch.AsyncCommand;
import org.jboss.as.console.client.shared.schedule.LongRunningTask;

import static org.jboss.as.console.client.domain.model.impl.LifecycleOperation.RELOAD;
import static org.jboss.as.console.client.domain.model.impl.LifecycleOperation.START;

/**
 * Callback which can be used in {@link HostInfoStoreImpl#startServer(String, String, boolean,
 * com.google.gwt.user.client.rpc.AsyncCallback)} and {@link HostInfoStoreImpl#reloadServer(String, String,
 * com.google.gwt.user.client.rpc.AsyncCallback)}. This callback uses a {@link LongRunningTask} to poll for the
 * requested status. Once the status is available the {@link SimpleCallback#onSuccess(Object)} method specified
 * as constructor parameter is executed.
 *
 * @author Harald Pehl
 * @date 10/22/2012
 */
public class ServerInstanceLifecycleCallback extends SimpleCallback<Boolean>
{
    private final String host;
    private final String server;
    private final LifecycleOperation lifecycleOp;
    private final HostInformationStore hostInfoStore;
    private final SimpleCallback<Server> callback;


    public ServerInstanceLifecycleCallback(final HostInformationStore hostInfoStore, final String host,
            final String server, final LifecycleOperation lifecycleOp, final SimpleCallback<Server> callback)
    {
        this.hostInfoStore = hostInfoStore;
        this.host = host;
        this.server = server;
        this.lifecycleOp = lifecycleOp;
        this.callback = callback;
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
                    hostInfoStore.getServerConfiguration(host, server, new SimpleCallback<Server>()
                    {
                        @Override
                        public void onSuccess(final Server server)
                        {
                            boolean keepPolling = lifecycleOp == START || lifecycleOp == RELOAD ? !server
                                    .isStarted() : server.isStarted();
                            if (!keepPolling)
                            {
                                ServerInstanceLifecycleCallback.this.callback.onSuccess(server);
                                Console.MODULES.getEventBus().fireEvent(
                                        new StaleModelEvent(StaleModelEvent.SERVER_INSTANCES)
                                );
                            }
                            callback.onSuccess(keepPolling);
                        }
                    });
                }
            }, lifecycleOp.limit());
            poll.schedule(500);
        }
    }
}
