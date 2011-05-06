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

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.domain.groups.PropertyRecord;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.model.ModelAdapter;
import org.jboss.as.console.client.widgets.forms.PropertyBinding;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 4/19/11
 */
public class DataSourceStoreImpl implements DataSourceStore {


    private DispatchAsync dispatcher;
    private BeanFactory factory;
    private PropertyMetaData propertyMetaData;

    @Inject
    public DataSourceStoreImpl(DispatchAsync dispatcher, BeanFactory factory,PropertyMetaData propertyMetaData) {
        this.dispatcher = dispatcher;
        this.factory = factory;
        this.propertyMetaData = propertyMetaData;
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


                        String driverToken = ds.get("driver").asString();

                        if(driverToken.indexOf("#")!=-1)
                        {
                            String[] split = driverToken.split("#");
                            model.setDriverName(split[0]);
                            model.setDriverVersion(split[1]);
                        }
                        else
                        {
                            model.setDriverName(driverToken);
                        }

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

    public void loadXADataSources(String profile, final AsyncCallback<List<XADataSource>> callback) {

        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(ADDRESS).add("profile", profile);
        operation.get(ADDRESS).add("subsystem", "datasources");
        operation.get(CHILD_TYPE).set("xa-data-source");

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = ModelNode.fromBase64(result.getResponseText());
                List<ModelNode> payload = response.get("result").asList();

                List<XADataSource> datasources = new ArrayList<XADataSource>(payload.size());
                for(ModelNode item : payload)
                {
                    // returned as type property (key=ds name)
                    Property property = item.asProperty();
                    ModelNode ds = property.getValue().asObject();
                    String name = property.getName();
                    //System.out.println(ds.toJSONString(false));

                    try {
                        XADataSource model = factory.xaDataSource().as();
                        model.setName(name);
                        model.setJndiName(ds.get("jndi-name").asString());
                        model.setDataSourceClass(ds.get("xa-data-source-class").asString());

                        if(ds.hasDefined("driver"))
                        {
                            String driverToken = ds.get("driver").asString();

                            if(driverToken.indexOf("#")!=-1)
                            {
                                String[] split = driverToken.split("#");
                                model.setDriverName(split[0]);
                                model.setDriverVersion(split[1]);
                            }
                            else
                            {
                                model.setDriverName(driverToken);
                            }
                        }

                        model.setEnabled(ds.get("enabled").asBoolean());
                        model.setUsername(ds.get("user-name").asString());
                        model.setPassword(ds.get("password").asString());
                        model.setPoolName(ds.get("pool-name").asString());


                        List<PropertyRecord> xaProperties = Collections.EMPTY_LIST;

                        // System properties
                        if(ds.hasDefined("xa-data-source-properties"))
                        {
                            List<ModelNode> properties = ds.get("xa-data-source-properties").asList();
                            xaProperties = new ArrayList<PropertyRecord>(properties.size());
                            for(ModelNode xaProp : properties)
                            {
                                Property p = xaProp.asProperty();
                                PropertyRecord propRecord = factory.property().as();

                                propRecord.setKey(p.getName());
                                ModelNode value = p.getValue();
                                propRecord.setValue(value.asString());

                                xaProperties.add(propRecord);
                            }
                        }

                        model.setProperties(xaProperties);
                        datasources.add(model);


                    } catch (IllegalArgumentException e) {
                        Log.error("Failed to parse xa data source representation", e);
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

        operation.get("driver").set(datasource.getDriverName()+"#"+datasource.getDriverVersion());
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
    public void enableDataSource(String profile, DataSource dataSource, boolean doEnable, final AsyncCallback<Boolean> callback) {

        final String dataSourceName = dataSource.getName();
        final String opName = doEnable ? "enable" : "disable";

        ModelNode operation = new ModelNode();
        operation.get(OP).set(opName);
        operation.get(ADDRESS).add("profile", profile);
        operation.get(ADDRESS).add("subsystem", "datasources");
        operation.get(ADDRESS).add("data-source", dataSourceName);

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

    @Override
    public void updateDataSource(String profile, String name, Map<String, Object> changedValues, final AsyncCallback<Boolean> callback) {
        ModelNode proto = new ModelNode();
        proto.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
        proto.get(ADDRESS).add("profile", profile);
        proto.get(ADDRESS).add("subsystem", "datasources");
        proto.get(ADDRESS).add("data-source", name);

        List<PropertyBinding> bindings = propertyMetaData.getBindingsForType(DataSource.class);
        ModelNode operation  = ModelAdapter.detypedFromChangeset(proto, changedValues, bindings);

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
}
