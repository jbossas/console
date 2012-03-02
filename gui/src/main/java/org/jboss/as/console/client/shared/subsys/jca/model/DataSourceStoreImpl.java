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
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.domain.profiles.CurrentProfileSelection;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.model.ModelAdapter;
import org.jboss.as.console.client.shared.model.ResponseWrapper;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.widgets.forms.AddressBinding;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.as.console.client.widgets.forms.KeyAssignment;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
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
    private ApplicationMetaData metaData;
    private CurrentProfileSelection currentProfile;

    private EntityAdapter<DataSource> dataSourceAdapter;
    private EntityAdapter<XADataSource> xaDataSourceAdapter ;
    private EntityAdapter<PoolConfig> datasourcePoolAdapter;
    private BeanMetaData dsMetaData;
    private BeanMetaData xadsMetaData;
    private BeanMetaData poolMetaData;
    private Baseadress baseadress;

    @Inject
    public DataSourceStoreImpl(
            DispatchAsync dispatcher,
            BeanFactory factory,
            ApplicationMetaData propertyMetaData,
            CurrentProfileSelection currentProfile, Baseadress baseadress) {
        this.dispatcher = dispatcher;
        this.factory = factory;
        this.metaData = propertyMetaData;
        this.currentProfile = currentProfile;
        this.baseadress = baseadress;

        this.dataSourceAdapter = new EntityAdapter<DataSource>(DataSource.class, propertyMetaData);
        this.xaDataSourceAdapter = new EntityAdapter<XADataSource>(XADataSource.class, propertyMetaData);
        this.datasourcePoolAdapter = new EntityAdapter<PoolConfig>(PoolConfig.class, propertyMetaData);


        this.dsMetaData = metaData.getBeanMetaData(DataSource.class);
        this.xadsMetaData = metaData.getBeanMetaData(XADataSource.class);
        this.poolMetaData = metaData.getBeanMetaData(PoolConfig.class);
    }


    @Override
    public void loadDataSources(final AsyncCallback<List<DataSource>> callback) {

        AddressBinding address = dsMetaData.getAddress();
        ModelNode operation = address.asSubresource(baseadress.getAdress());
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode response  = result.get();

                if(response.isFailure())
                {
                    callback.onFailure(new RuntimeException(response.getFailureDescription()));
                }
                else
                {
                    List<DataSource> datasources = dataSourceAdapter.fromDMRList(response.get(RESULT).asList());
                    callback.onSuccess(datasources);
                }
            }
        });
    }

    @Override
    public void loadDataSource(String name, boolean isXA, final AsyncCallback<DataSource> callback) {

        AddressBinding address = isXA ? xadsMetaData.getAddress() : dsMetaData.getAddress();
        ModelNode operation = address.asResource(baseadress.getAdress(), name);
        operation.get(OP).set(READ_RESOURCE_OPERATION);

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode response  = result.get();

                if(response.isFailure())
                {
                    callback.onFailure(new RuntimeException(response.getFailureDescription()));
                }
                else
                {
                    DataSource datasource = dataSourceAdapter.fromDMR(response.get(RESULT).asObject());
                    callback.onSuccess(datasource);
                }
            }
        });
    }

    public void loadXADataSources(final AsyncCallback<List<XADataSource>> callback) {

        AddressBinding address = xadsMetaData.getAddress();
        ModelNode operation =  address.asSubresource(baseadress.getAdress());
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode response  = result.get();

                if(response.isFailure())
                {
                    callback.onFailure(new RuntimeException(response.getFailureDescription()));
                }
                else
                {
                    List<XADataSource> datasources = xaDataSourceAdapter.fromDMRList(response.get(RESULT).asList());
                    callback.onSuccess(datasources);
                }

            }
        });
    }

    @Override
    public void loadXAProperties(final String dataSourceName, final AsyncCallback<List<PropertyRecord>> callback) {

        AddressBinding address = xadsMetaData.getAddress();
        ModelNode operation = address.asResource(baseadress.getAdress(), dataSourceName);
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(CHILD_TYPE).set("xa-datasource-properties");

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }

            @Override
            public void onSuccess(DMRResponse response) {
                ModelNode result = response.get();

                List<Property> properties = result.get(RESULT).asPropertyList();
                List<PropertyRecord> records = new ArrayList<PropertyRecord>(properties.size());
                for(Property prop : properties)
                {
                    String name = prop.getName();
                    String value = prop.getValue().asObject().get("value").asString();
                    PropertyRecord propertyRecord = factory.property().as();
                    propertyRecord.setKey(name);
                    propertyRecord.setValue(value);
                    records.add(propertyRecord);
                }

                callback.onSuccess(records);
            }
        });

    }

    @Override
    public void createDataSource(final DataSource datasource, final AsyncCallback<ResponseWrapper<Boolean>> callback) {

        AddressBinding address = dsMetaData.getAddress();
        ModelNode addressModel =  address.asResource(baseadress.getAdress(), datasource.getName());

        ModelNode operation = dataSourceAdapter.fromEntity(datasource);
        operation.get(OP).set(ADD);
        operation.get(ADDRESS).set(addressModel.get(ADDRESS));

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode modelNode = result.get();
                boolean wasSuccessful = modelNode.get(OUTCOME).asString().equals(SUCCESS);

                callback.onSuccess(new ResponseWrapper<Boolean>(wasSuccessful, modelNode));
            }
        });
    }

    @Override
    public void createXADataSource(XADataSource datasource, final AsyncCallback<ResponseWrapper<Boolean>> callback) {


        ModelNode operation = new ModelNode();
        operation.get(OP).set(COMPOSITE);
        operation.get(ADDRESS).setEmptyList();

        List<ModelNode> steps = new ArrayList<ModelNode>();

        // create XA datasource
        AddressBinding address = xadsMetaData.getAddress();
        ModelNode addressModel =  address.asResource(baseadress.getAdress(), datasource.getName());

        ModelNode createResource = xaDataSourceAdapter.fromEntity(datasource);
        createResource.get(OP).set(ADD);
        createResource.get(ADDRESS).set(addressModel.get(ADDRESS));

        steps.add(createResource);

        // create xa properties
        for(PropertyRecord prop : datasource.getProperties())
        {
            ModelNode createProperty = new ModelNode();
            createProperty.get(ADDRESS).set(addressModel.get(ADDRESS));
            createProperty.get(ADDRESS).add("xa-datasource-properties", prop.getKey());
            createProperty.get(OP).set(ADD);
            createProperty.get(VALUE).set(prop.getValue());

            steps.add(createProperty);
        }

        operation.get(STEPS).set(steps);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {

                callback.onSuccess(ModelAdapter.wrapBooleanResponse(result));
            }
        });
    }

    @Override
    public void deleteDataSource(final DataSource dataSource, final AsyncCallback<Boolean> callback) {

        AddressBinding address = dsMetaData.getAddress();
        ModelNode addressModel =  address.asResource(baseadress.getAdress(), dataSource.getName());

        ModelNode operation = dataSourceAdapter.fromEntity(dataSource);
        operation.get(OP).set(REMOVE);
        operation.get(ADDRESS).set(addressModel.get(ADDRESS));

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
    public void deleteXADataSource(XADataSource dataSource, final AsyncCallback<Boolean> callback) {

        AddressBinding address = xadsMetaData.getAddress();
        ModelNode addressModel =  address.asResource(baseadress.getAdress(), dataSource.getName());

        ModelNode operation = xaDataSourceAdapter.fromEntity(dataSource);
        operation.get(OP).set(REMOVE);
        operation.get(ADDRESS).set(addressModel.get(ADDRESS));

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
    public void enableDataSource(DataSource dataSource, boolean doEnable, final AsyncCallback<ResponseWrapper<Boolean>> callback) {

        final String opName = doEnable ? "enable" : "disable";

        AddressBinding address = dsMetaData.getAddress();
        ModelNode addressModel =  address.asResource(baseadress.getAdress(), dataSource.getName());

        ModelNode operation = dataSourceAdapter.fromEntity(dataSource);
        operation.get(OP).set(opName);
        operation.get(ADDRESS).set(addressModel.get(ADDRESS));

        if(!doEnable)
            operation.get(OPERATION_HEADERS).get(ALLOW_RESOURCE_SERVICE_RESTART).set(true);

        //System.out.println(operation);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode modelNode = result.get();
                ResponseWrapper<Boolean> response =
                        new ResponseWrapper<Boolean>(
                                modelNode.get(OUTCOME).asString().equals(SUCCESS), modelNode
                        );

                callback.onSuccess(response);
            }
        });
    }

    @Override
    public void enableXADataSource(XADataSource dataSource, boolean doEnable, final AsyncCallback<ResponseWrapper<Boolean>> callback) {

        final String opName = doEnable ? "enable" : "disable";

        AddressBinding address = xadsMetaData.getAddress();
        ModelNode addressModel =  address.asResource(baseadress.getAdress(), dataSource.getName());

        ModelNode operation = xaDataSourceAdapter.fromEntity(dataSource);
        operation.get(OP).set(opName);
        operation.get(ADDRESS).set(addressModel.get(ADDRESS));

        if(!doEnable)
            operation.get(OPERATION_HEADERS).get(ALLOW_RESOURCE_SERVICE_RESTART).set(true);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                ResponseWrapper<Boolean> wrapper =
                        new ResponseWrapper<Boolean>(response.get("outcome").asString().equals("success"), response);
                callback.onSuccess(wrapper);
            }
        });
    }

    private boolean responseIndicatesSuccess(DMRResponse result) {
        ModelNode response = result.get();
        return response.get(OUTCOME).asString().equals(SUCCESS);
    }

    @Override
    public void updateDataSource(String name, Map<String, Object> changedValues, final AsyncCallback<ResponseWrapper<Boolean>> callback) {


        AddressBinding address = dsMetaData.getAddress();
        ModelNode addressModel = address.asResource(baseadress.getAdress(), name);
        ModelNode operation = dataSourceAdapter.fromChangeset(changedValues, addressModel);

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {

                callback.onSuccess(ModelAdapter.wrapBooleanResponse(result));
            }
        });
    }

    @Override
    public void updateXADataSource(String name, Map<String, Object> changedValues, final AsyncCallback<ResponseWrapper<Boolean>> callback) {


        AddressBinding address = xadsMetaData.getAddress();
        ModelNode addressModel = address.asResource(baseadress.getAdress(), name);
        ModelNode operation = xaDataSourceAdapter.fromChangeset(changedValues, addressModel);

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {

                callback.onSuccess(ModelAdapter.wrapBooleanResponse(result));
            }
        });
    }

    @Override
    public void doFlush(boolean xa, String name, final AsyncCallback<Boolean> callback) {
        String parentAddress = xa ? "xa-data-source" : "data-source";
        AddressBinding address = poolMetaData.getAddress();

        ModelNode operation = address.asResource(baseadress.getAdress(), parentAddress, name);
        operation.get(OP).set("flush-all-connection-in-pool");

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode response  = result.get();
                callback.onSuccess(!response.isFailure());
            }
        });
    }

    @Override
    public void loadPoolConfig(boolean isXA, final String name, final AsyncCallback<ResponseWrapper<PoolConfig>> callback) {

        String parentAddress = isXA ? "xa-data-source" : "data-source";
        AddressBinding address = poolMetaData.getAddress();

        ModelNode operation = address.asResource(baseadress.getAdress(), parentAddress, name);
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(INCLUDE_RUNTIME).set(Boolean.TRUE);

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode response  = result.get();

                EntityAdapter<PoolConfig> adapter = new EntityAdapter<PoolConfig>(PoolConfig.class, metaData)
                        .with(new KeyAssignment() {
                            @Override
                            public Object valueForKey(String key) {
                                return name;
                            }
                        });
                PoolConfig poolConfig = adapter.fromDMR(response.get(RESULT));
                callback.onSuccess(new ResponseWrapper<PoolConfig>(poolConfig, response));

            }
        });
    }

    @Override
    public void savePoolConfig(boolean isXA, String name, Map<String, Object> changeset, final AsyncCallback<ResponseWrapper<Boolean>> callback) {

        String parentAddress = isXA ? "xa-data-source" : "data-source";

        AddressBinding address = poolMetaData.getAddress();
        ModelNode addressModel = address.asResource(baseadress.getAdress(), parentAddress, name);

        ModelNode operation = datasourcePoolAdapter .fromChangeset(changeset, addressModel);

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {

                callback.onSuccess(ModelAdapter.wrapBooleanResponse(result));
            }
        });
    }

    @Override
    public void deletePoolConfig(boolean isXA, final String dsName, final AsyncCallback<ResponseWrapper<Boolean>> callback) {

        Map<String, Object> resetValues = new HashMap<String, Object>();
        resetValues.put("minPoolSize", 0);
        resetValues.put("maxPoolSize", 20);
        resetValues.put("poolStrictMin", false);
        resetValues.put("poolPrefill", false);

        savePoolConfig(isXA, dsName, resetValues, callback);

    }

    @Override
    public void verifyConnection(final String dataSourceName, boolean isXA, final AsyncCallback<ResponseWrapper<Boolean>> callback) {
        AddressBinding address = isXA ? xadsMetaData.getAddress() : dsMetaData.getAddress();
        ModelNode operation = address.asResource(baseadress.getAdress(), dataSourceName);
        operation.get(OP).set("test-connection-in-pool");

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }

            @Override
            public void onSuccess(DMRResponse response) {
                ModelNode result = response.get();

                ResponseWrapper<Boolean> wrapped = new ResponseWrapper<Boolean>(
                    !result.isFailure(),result
                );

                callback.onSuccess(wrapped);
            }
        });

    }

    @Override
    public void loadConnectionProperties(String reference, final AsyncCallback<List<PropertyRecord>> callback) {
        AddressBinding address = dsMetaData.getAddress();
        ModelNode operation = address.asResource(baseadress.getAdress(), reference);
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(CHILD_TYPE).set("connection-properties");

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }

            @Override
            public void onSuccess(DMRResponse response) {
                ModelNode result = response.get();

                List<Property> properties = result.get(RESULT).asPropertyList();
                List<PropertyRecord> records = new ArrayList<PropertyRecord>(properties.size());
                for(Property prop : properties)
                {
                    String name = prop.getName();
                    String value = prop.getValue().asObject().get("value").asString();
                    PropertyRecord propertyRecord = factory.property().as();
                    propertyRecord.setKey(name);
                    propertyRecord.setValue(value);
                    records.add(propertyRecord);
                }

                callback.onSuccess(records);
            }
        });
    }

    @Override
    public void createConnectionProperty(String reference, PropertyRecord prop, final AsyncCallback<Boolean> callback) {
        AddressBinding address = dsMetaData.getAddress();
        ModelNode operation = address.asResource(baseadress.getAdress(), reference);
        operation.get(ADDRESS).add("connection-properties", prop.getKey());
        operation.get(OPERATION_HEADERS).get(ALLOW_RESOURCE_SERVICE_RESTART).set(true);
        operation.get(OP).set(ADD);
        operation.get(VALUE).set(prop.getValue());

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }

            @Override
            public void onSuccess(DMRResponse response) {
                ModelNode result = response.get();
                callback.onSuccess(ModelAdapter.wasSuccess(result));
            }
        });
    }

    @Override
    public void deleteConnectionProperty(String reference, PropertyRecord prop, final AsyncCallback<Boolean> callback) {
        AddressBinding address = dsMetaData.getAddress();
        ModelNode operation = address.asResource(baseadress.getAdress(), reference);
        operation.get(ADDRESS).add("connection-properties", prop.getKey());
        operation.get(OP).set(REMOVE);
        operation.get(OPERATION_HEADERS).get(ALLOW_RESOURCE_SERVICE_RESTART).set(true);

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }

            @Override
            public void onSuccess(DMRResponse response) {
                ModelNode result = response.get();
                callback.onSuccess(ModelAdapter.wasSuccess(result));
            }
        });
    }

    @Override
    public void createXAConnectionProperty(String reference, PropertyRecord prop, final AsyncCallback<Boolean> callback) {
        AddressBinding address = xadsMetaData.getAddress();
        ModelNode operation = address.asResource(baseadress.getAdress(), reference);
        operation.get(ADDRESS).add("xa-datasource-properties", prop.getKey());
        operation.get(OPERATION_HEADERS).get(ALLOW_RESOURCE_SERVICE_RESTART).set(true);
        operation.get(OP).set(ADD);
        operation.get(VALUE).set(prop.getValue());

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }

            @Override
            public void onSuccess(DMRResponse response) {
                ModelNode result = response.get();
                callback.onSuccess(ModelAdapter.wasSuccess(result));
            }
        });
    }

    @Override
    public void deleteXAConnectionProperty(String reference, PropertyRecord prop, final AsyncCallback<Boolean> callback) {
        AddressBinding address = xadsMetaData.getAddress();
        ModelNode operation = address.asResource(baseadress.getAdress(), reference);
        operation.get(ADDRESS).add("xa-datasource-properties", prop.getKey());
        operation.get(OPERATION_HEADERS).get(ALLOW_RESOURCE_SERVICE_RESTART).set(true);
        operation.get(OP).set(REMOVE);

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }

            @Override
            public void onSuccess(DMRResponse response) {
                ModelNode result = response.get();
                callback.onSuccess(ModelAdapter.wasSuccess(result));
            }
        });
    }
}
