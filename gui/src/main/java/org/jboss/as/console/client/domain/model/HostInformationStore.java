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

package org.jboss.as.console.client.domain.model;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.shared.jvm.Jvm;
import org.jboss.as.console.client.shared.properties.PropertyRecord;

import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 3/2/11
 */
public interface HostInformationStore {
    void getHosts(AsyncCallback<List<Host>> callback);
    void getServerConfigurations(String name, AsyncCallback<List<Server>> callback);
    void getServerConfiguration(String host, String server, final AsyncCallback<Server> callback);
    void getServerInstances(String host, AsyncCallback<List<ServerInstance>> callback);
    void updateServerInstance(String host, final Server handle, final AsyncCallback<ServerInstance> callback);
    void getVirtualMachines(String host, final AsyncCallback<List<String>> callback) ;

    void startServer(String host, String configName, boolean startIt, final AsyncCallback<Boolean> callback);

    void reloadServer(String host, String configName, final AsyncCallback<Boolean> callback);

    void createServerConfig(String host, Server newServer, AsyncCallback<Boolean> callback);

    void saveServerConfig(String host, String name, Map<String, Object> changedValues, AsyncCallback<Boolean> callback);

    void deleteServerConfig(String selectedHost, Server selectedRecord, AsyncCallback<Boolean> asyncCallback);

    void loadJVMConfiguration(String host, Server server, AsyncCallback<Jvm> callback);

    void loadProperties(String host, Server server, AsyncCallback<List<PropertyRecord>> callback);
}
