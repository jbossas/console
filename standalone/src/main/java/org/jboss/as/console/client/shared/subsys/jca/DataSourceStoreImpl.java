/*
 * JBoss, Home of Professional Open Source
 * Copyright <YEAR> Red Hat Inc. and/or its affiliates and other contributors
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

package org.jboss.as.console.client.shared.subsys.jca;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.message.Message;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 4/19/11
 */
public class DataSourceStoreImpl implements DataSourceStore {


    private DispatchAsync dispatcher;
    private BeanFactory factory;

    @Inject
    public DataSourceStoreImpl(DispatchAsync dispatcher, BeanFactory factory) {
        this.dispatcher = dispatcher;
        this.factory = factory;
    }

    @Override
    public void loadDataSources(String profile, final AsyncCallback<List<DataSource>> callback) {
        // /profile=default/subsystem=datasources:read-children-resources(child-type=data-source)

        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(ADDRESS).add("profile", profile);
        operation.get(ADDRESS).add("subsystem", "datasources");
        operation.get(CHILD_TYPE).set("data-source");

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = ModelNode.fromBase64(result.getResponseText());
                List<ModelNode> payload = response.get("result").asList();

                List<DataSource> datasources = new ArrayList<DataSource>(payload.size());
                for(ModelNode item : payload)
                {
                    // returned as type property (key=ds name)
                    Property property = item.asProperty();
                    ModelNode ds = property.getValue().asObject();
                    String name = property.getName();
                    //System.out.println(ds.toJSONString(false));

                    try {
                        DataSource model = factory.dataSource().as();
                        model.setName(name);
                        model.setConnectionUrl(ds.get("connection-url").asString());
                        model.setJndiName(ds.get("jndi-name").asString());
                        model.setDriverClass(ds.get("driver-class").asString());
                        model.setDriverName(ds.get("driver").asString());
                        model.setEnabled(ds.get("enabled").asBoolean());
                        model.setUsername(ds.get("user-name").asString());
                        model.setPassword(ds.get("password").asString());
                        model.setPoolName(ds.get("pool-name").asString());

                        datasources.add(model);

                    } catch (IllegalArgumentException e) {
                        Log.error("Failed to parse data source representation", e);
                    }
                }

                callback.onSuccess(datasources);
            }
        });
    }


    @Override
    public void createDataSource(String profile, final DataSource datasource, final AsyncCallback<Boolean> callback) {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(ADD);
        operation.get(ADDRESS).add("profile", profile);
        operation.get(ADDRESS).add("subsystem", "datasources");
        operation.get(ADDRESS).add("data-source", datasource.getName());


        operation.get("name").set(datasource.getName());
        operation.get("jndi-name").set(datasource.getJndiName());
        operation.get("enabled").set(datasource.isEnabled());

        operation.get("driver").set(datasource.getDriverName());
        operation.get("driver-class").set(datasource.getDriverClass());
        operation.get("pool-name").set(datasource.getName()+"_Pool");

        operation.get("connection-url").set(datasource.getConnectionUrl());
        operation.get("user-name").set(datasource.getUsername());

        String pw = datasource.getPassword() != null ? datasource.getPassword() : "";
        operation.get("password").set(pw);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                boolean wasSuccessful = responseIndicatesSuccess(result);
                callback.onSuccess(wasSuccessful);
            }
        });
    }

    @Override
    public void deleteDataSource(String profile, final DataSource dataSource, final AsyncCallback<Boolean> callback) {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(REMOVE);
        operation.get(ADDRESS).add("profile", profile);
        operation.get(ADDRESS).add("subsystem", "datasources");
        operation.get(ADDRESS).add("data-source", dataSource.getName());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                boolean wasSuccessful = responseIndicatesSuccess(result);
                callback.onSuccess(wasSuccessful);
            }
        });


    }

    @Override
    public void enableDataSource(String profile, DataSource dataSource, boolean isEnabled, final AsyncCallback<Boolean> callback) {
        final String dataSourceName = dataSource.getName();

        ModelNode operation = new ModelNode();
        operation.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
        operation.get(ADDRESS).add("profile", profile);
        operation.get(ADDRESS).add("subsystem", "datasources");
        operation.get(ADDRESS).add("data-source", dataSourceName);
        operation.get("name").set("enabled");
        operation.get("value").set(isEnabled);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                callback.onSuccess(responseIndicatesSuccess(result));
            }
        });
    }

    private boolean responseIndicatesSuccess(DMRResponse result) {
        ModelNode response = ModelNode.fromBase64(result.getResponseText());
        return response.get(OUTCOME).asString().equals(SUCCESS);
    }
}
