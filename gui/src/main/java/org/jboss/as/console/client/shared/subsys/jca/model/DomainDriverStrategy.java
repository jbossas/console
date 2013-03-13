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

package org.jboss.as.console.client.shared.subsys.jca.model;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.dmr.client.dispatch.DispatchAsync;
import org.jboss.dmr.client.dispatch.impl.DMRAction;
import org.jboss.dmr.client.dispatch.impl.DMRResponse;
import org.jboss.dmr.client.ModelNode;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 5/16/11
 */
public class DomainDriverStrategy implements DriverStrategy {


    private DispatchAsync dispatcher;
    private HostInformationStore hostInformationStore;
    private BeanFactory factory;

    @Inject
    public DomainDriverStrategy(
            DispatchAsync dispatcher,
            HostInformationStore hostInformationStore,
            BeanFactory factory) {
        this.dispatcher = dispatcher;
        this.hostInformationStore = hostInformationStore;
        this.factory = factory;
    }

    @Override
    public void refreshDrivers(final AsyncCallback<List<JDBCDriver>> callback) {

        final Counter counter = new Counter();
        final List<JDBCDriver> drivers = new ArrayList<JDBCDriver>();

        hostInformationStore.getHosts(new SimpleCallback<List<Host>>() {
            @Override
            public void onSuccess(List<Host> hosts) {

                for(Host host : hosts)
                {
                    counter.numRequests++;

                    driversOnHost(host.getName(), new SimpleCallback<List<JDBCDriver>>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            super.onFailure(caught);
                            counter.numResponses++;
                        }

                        @Override
                        public void onSuccess(List<JDBCDriver> jdbcDrivers) {
                            // for each host

                            counter.numResponses++;


                            for(JDBCDriver driver : jdbcDrivers)
                                addIfNotExists(driver, drivers);

                            if(counter.numRequests==counter.numResponses)
                            {
                                callback.onSuccess(drivers);
                            }
                        }
                    });
                }
            }
        });

    }

    class Counter {
        int numRequests = 0;
        int numResponses = 0;
    }

    private void driversOnHost(final String host, final AsyncCallback<List<JDBCDriver>> callback) {

        final List<JDBCDriver> drivers = new ArrayList<JDBCDriver>();
        final Counter counter = new Counter();

        hostInformationStore.getServerInstances(host, new SimpleCallback<List<ServerInstance>>() {
            @Override
            public void onSuccess(List<ServerInstance> result) {

                int numSkipped = 0;
                for(final ServerInstance server : result){

                    if(!server.isRunning())
                    {
                        numSkipped++;
                        continue;
                    }

                    ModelNode operation = new ModelNode();
                    operation.get(OP).set("installed-drivers-list");
                    operation.get(ADDRESS).add("host", host);
                    operation.get(ADDRESS).add("server", server.getName());
                    operation.get(ADDRESS).add("subsystem", "datasources");

                    counter.numRequests++;

                    dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

                        @Override
                        public void onFailure(Throwable caught) {

                            counter.numResponses++;
                            checkComplete(counter, callback, caught);
                        }

                        @Override
                        public void onSuccess(DMRResponse result) {

                            counter.numResponses++;

                            ModelNode response = result.get();

                            if(SUCCESS.equals(response.get(OUTCOME).asString())) {


                                List<ModelNode> payload = response.get(RESULT).asList();

                                for(ModelNode item : payload)
                                {

                                    JDBCDriver driver = factory.jdbcDriver().as();
                                    driver.setGroup(server.getGroup());
                                    driver.setDriverClass(item.get("driver-class-name").asString());
                                    driver.setName(item.get("driver-name").asString());
                                    driver.setDeploymentName(item.get("deployment-name").asString());
                                    driver.setMajorVersion(item.get("driver-major-version").asInt());
                                    driver.setMinorVersion(item.get("driver-minor-version").asInt());

                                    if(item.hasDefined("driver-xa-datasource-class-name"))
                                        driver.setXaDataSourceClass(item.get("driver-xa-datasource-class-name").asString());

                                    addIfNotExists(driver, drivers);

                                }

                            }
                            else {
                                checkComplete(counter, callback, new RuntimeException(response.toString()));
                            }

                            checkComplete(counter, drivers, callback);

                        }
                    });
                }

                if(numSkipped==result.size())
                    callback.onSuccess(Collections.EMPTY_LIST);

            }
        });

    }

    private void addIfNotExists(JDBCDriver driver, List<JDBCDriver> drivers) {

        boolean doesExist = false;
        for(JDBCDriver existing : drivers) // we don't control the AutoBean hash() or equals() method.
        {
            if(existing.getName().equals(driver.getName()) &&
                    existing.getGroup().equals(driver.getGroup()))
            {
                doesExist = true;
                break;
            }
        }
        if(!doesExist)
            drivers.add(driver);
    }

    private void checkComplete(Counter counter, List<JDBCDriver> drivers, AsyncCallback<List<JDBCDriver>> callback) {
        if(counter.numResponses==counter.numResponses)
            callback.onSuccess(drivers);
    }

    private void checkComplete(Counter counter, AsyncCallback<List<JDBCDriver>> callback, Throwable caught) {
        if(counter.numResponses==counter.numRequests)
            callback.onFailure(caught);
        else
            Console.error("Failed to query JDBC drivers", caught.getMessage());
    }
}
