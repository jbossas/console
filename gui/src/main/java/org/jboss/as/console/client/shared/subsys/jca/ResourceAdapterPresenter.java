package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.Proxy;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.model.ModelAdapter;
import org.jboss.as.console.client.shared.model.ResponseWrapper;
import org.jboss.as.console.client.shared.properties.NewPropertyWizard;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.jca.model.AdminObject;
import org.jboss.as.console.client.shared.subsys.jca.model.ConnectionDefinition;
import org.jboss.as.console.client.shared.subsys.jca.model.PoolConfig;
import org.jboss.as.console.client.shared.subsys.jca.model.ResourceAdapter;
import org.jboss.as.console.client.shared.subsys.jca.wizard.NewAdapterWizard;
import org.jboss.as.console.client.shared.subsys.jca.wizard.NewAdminWizard;
import org.jboss.as.console.client.shared.subsys.jca.wizard.NewConnectionWizard;
import org.jboss.as.console.client.widgets.forms.AddressBinding;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.as.console.client.widgets.forms.KeyAssignment;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.ModelNodeUtil;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 7/19/11
 */
public class ResourceAdapterPresenter
        extends Presenter<ResourceAdapterPresenter.MyView, ResourceAdapterPresenter.MyProxy> {

    private final PlaceManager placeManager;
    private RevealStrategy revealStrategy;
    private DispatchAsync dispatcher;
    private BeanFactory factory;
    private DefaultWindow window;
    private DefaultWindow propertyWindow;

    private ApplicationMetaData metaData;

    private BeanMetaData raMetaData;
    private BeanMetaData connectionMetaData;
    private BeanMetaData adminMetaData;
    private String selectedAdapter;

    private EntityAdapter<ConnectionDefinition> connectionAdapter;
    private EntityAdapter<ResourceAdapter> adapter;
    private EntityAdapter<PropertyRecord> propertyAdapter;
    private EntityAdapter<PoolConfig> poolAdapter;
    private EntityAdapter<AdminObject> adminAdapter;


    @ProxyCodeSplit
    @NameToken(NameTokens.ResourceAdapterPresenter)
    public interface MyProxy extends Proxy<ResourceAdapterPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(ResourceAdapterPresenter presenter);
        void setAdapters(List<ResourceAdapter> adapters);

        void setSelectedAdapter(String selectedAdapter);
    }

    @Inject
    public ResourceAdapterPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, RevealStrategy revealStrategy,
            DispatchAsync dispatcher, BeanFactory factory, ApplicationMetaData propertyMetaData) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.revealStrategy = revealStrategy;
        this.dispatcher = dispatcher;
        this.factory = factory;
        this.metaData = propertyMetaData;

        this.raMetaData = metaData.getBeanMetaData(ResourceAdapter.class);
        this.connectionMetaData = metaData.getBeanMetaData(ConnectionDefinition.class);
        this.adminMetaData = metaData.getBeanMetaData(AdminObject.class);

        adapter  = new EntityAdapter<ResourceAdapter>(ResourceAdapter.class, metaData);
        connectionAdapter = new EntityAdapter<ConnectionDefinition>(ConnectionDefinition.class, metaData);
        propertyAdapter = new EntityAdapter<PropertyRecord>(PropertyRecord.class, metaData);
        poolAdapter = new EntityAdapter<PoolConfig>(PoolConfig.class, metaData);
        adminAdapter = new EntityAdapter<AdminObject>(AdminObject.class, metaData);
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {
        this.selectedAdapter = request.getParameter("name", null);
    }

    private void loadAdapter(final boolean refreshDetail) {

        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "resource-adapters");
        operation.get(CHILD_TYPE).set("resource-adapter");
        operation.get(RECURSIVE).set(true);


        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse response) {
                ModelNode result = response.get();

                List<Property> children = result.get(RESULT).asPropertyList();
                List<ResourceAdapter> resourceAdapters = new ArrayList<ResourceAdapter>(children.size());

                for(Property child : children)
                {
                    ModelNode raModel = child.getValue();

                    ResourceAdapter resourceAdapter = adapter.fromDMR(raModel);

                    List<PropertyRecord> props = parseConfigProperties(raModel);
                    resourceAdapter.setProperties(props);

                    resourceAdapter.setConnectionDefinitions(new ArrayList<ConnectionDefinition>());

                    // connection definition
                    if(raModel.hasDefined("connection-definitions"))
                    {
                        List<Property> connections = raModel.get("connection-definitions").asPropertyList();
                        for(Property con : connections )
                        {
                            ModelNode connectionModel = con.getValue();
                            ConnectionDefinition connectionDefinition = connectionAdapter.fromDMR(connectionModel);

                            // config properties
                            List<PropertyRecord> connectionProps = parseConfigProperties(connectionModel);
                            connectionDefinition.setProperties(connectionProps);

                            // pool
                            PoolConfig poolConfig = poolAdapter.with(new KeyAssignment() {
                                @Override
                                public Object valueForKey(String key) {
                                    //return connectionModel.get("");
                                    return "";
                                }
                            }).fromDMR(connectionModel);
                            connectionDefinition.setPoolConfig(poolConfig);

                            resourceAdapter.getConnectionDefinitions().add(connectionDefinition);

                        }

                    }


                    // admin objects
                    if(raModel.hasDefined("admin-objects"))
                    {
                        List<Property> admins = raModel.get("admin-objects").asPropertyList();
                        List<AdminObject> adminEntities = new ArrayList<AdminObject>(admins.size());

                        for(Property admin : admins)
                        {
                            ModelNode adminModel = admin.getValue();
                            AdminObject adminObject = adminAdapter.fromDMR(adminModel);
                            adminObject.setName(admin.getName()); // just to make sure
                            List<PropertyRecord> adminConfig = parseConfigProperties(adminModel);
                            adminObject.setProperties(adminConfig);

                            adminEntities.add(adminObject);
                        }

                        resourceAdapter.setAdminObjects(adminEntities);
                    }
                    else
                    {
                        resourceAdapter.setAdminObjects(Collections.EMPTY_LIST);
                    }


                    // append result
                    resourceAdapters.add(resourceAdapter);
                }

                getView().setAdapters(resourceAdapters);

                if(refreshDetail)
                    getView().setSelectedAdapter(selectedAdapter);
            }
        });
    }

    private List<PropertyRecord> parseConfigProperties(ModelNode modelNode) {

        List<PropertyRecord> result = null;
        // connection properties
        if(modelNode.hasDefined("config-properties"))
        {
            List<Property> model = modelNode.get("config-properties").asPropertyList();
            result = new ArrayList<PropertyRecord>(model.size());
            for(Property prop : model)
            {
                PropertyRecord record = propertyAdapter.fromDMR(prop.getValue());
                record.setKey(prop.getName());
                result.add(record);
            }
        }
        else
        {
            result = Collections.EMPTY_LIST;
        }

        return result;
    }

    @Override
    protected void onReset() {
        super.onReset();
        loadAdapter(true);
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }

    public void onDelete(final ResourceAdapter ra) {

        AddressBinding address = raMetaData.getAddress();
        ModelNode operation = address.asResource(Baseadress.get(), ra.getArchive());
        operation.get(OP).set(REMOVE);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                loadAdapter(false);
            }

            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode result = dmrResponse.get();
                if(ModelNodeUtil.indicatesSuccess(result))
                    Console.info(Console.MESSAGES.deleted("Resource Adapter "+ra.getArchive()));
                else
                    Console.error(Console.MESSAGES.deletionFailed("Resource Adapter "+ra.getArchive()), result.toString());

                loadAdapter(false);
            }
        });

    }

    public void onSave(final ResourceAdapter ra, Map<String, Object> changedValues) {

        AddressBinding address = raMetaData.getAddress();
        ModelNode addressModel = address.asResource(Baseadress.get(), ra.getArchive());
        addressModel.get(OP).set(WRITE_ATTRIBUTE_OPERATION);


        EntityAdapter<ResourceAdapter> adapter = new EntityAdapter<ResourceAdapter>(
                ResourceAdapter.class, metaData
        );

        ModelNode operation = adapter.fromChangeset(
                changedValues,
                addressModel
        );

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                boolean success = response.get(OUTCOME).asString().equals(SUCCESS);

                if(success)
                    Console.info(Console.MESSAGES.saved("Resource Adapter " + ra.getArchive()));
                else
                    Console.error(Console.MESSAGES.saveFailed("Resource Adapter " + ra.getArchive()),
                            response.getFailureDescription());

                loadAdapter(false);
            }
        });

    }

    public void launchNewAdapterWizard() {
        window = new DefaultWindow(Console.MESSAGES.createTitle("Resource Adapter"));
        window.setWidth(480);
        window.setHeight(360);

        window.trapWidget(
                new NewAdapterWizard(this).asWidget()
        );

        window.setGlassEnabled(true);
        window.center();
    }

    public void closeDialoge() {
        window.hide();
    }

    public void onCreateAdapter(final ResourceAdapter ra) {
        closeDialoge();

        ModelNode addressModel = raMetaData.getAddress().asResource(Baseadress.get(), ra.getArchive());

        ModelNode operation = adapter.fromEntity(ra);
        operation.get(OP).set(ADD);
        operation.get(ADDRESS).set(addressModel.get(ADDRESS).asObject());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                loadAdapter(false);
            }

            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode result = dmrResponse.get();
                if(ModelNodeUtil.indicatesSuccess(result))
                    Console.info(Console.MESSAGES.added("Resource Adapter " + ra.getArchive()));
                else
                    Console.error(Console.MESSAGES.addingFailed("Resource Adapter " + ra.getArchive()), result.toString());

                loadAdapter(false);
            }
        });

    }

    public void createProperty(final ResourceAdapter ra, final PropertyRecord prop) {
        closePropertyDialoge();

        ModelNode createProp = new ModelNode();
        createProp.get(OP).set(ADD);
        createProp.get(ADDRESS).set(Baseadress.get());
        createProp.get(ADDRESS).add("subsystem","resource-adapters");
        createProp.get(ADDRESS).add("resource-adapter", ra.getArchive());
        //createProp.get(ADDRESS).add("connection-definitions", ra.getJndiName());
        createProp.get(ADDRESS).add("config-properties", prop.getKey());
        createProp.get("value").set(prop.getValue());

        dispatcher.execute(new DMRAction(createProp), new SimpleCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                loadAdapter(false);
            }

            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode result = dmrResponse.get();
                if(ModelNodeUtil.indicatesSuccess(result))
                    Console.info(Console.MESSAGES.added("Property " + prop.getKey()));
                else
                    Console.error(Console.MESSAGES.addingFailed("Property " + prop.getKey()), result.toString());

                loadAdapter(false);
            }
        });

    }

    public void onDeleteProperty(ResourceAdapter ra, final PropertyRecord prop) {

        ModelNode operation = new ModelNode();
        operation.get(OP).set(REMOVE);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem","resource-adapters");
        operation.get(ADDRESS).add("resource-adapter", ra.getArchive());
        //operation.get(ADDRESS).add("connection-definitions", ra.getJndiName());
        operation.get(ADDRESS).add("config-properties", prop.getKey());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                loadAdapter(false);
            }

            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode result = dmrResponse.get();
                if(ModelNodeUtil.indicatesSuccess(result))
                    Console.info(Console.MESSAGES.deleted("Property " + prop.getKey()));
                else
                    Console.error(Console.MESSAGES.deletionFailed("Property " + prop.getKey()), result.toString());

                loadAdapter(false);
            }
        });
    }

    public void launchNewPropertyDialoge(final ResourceAdapter ra) {
        propertyWindow = new DefaultWindow(Console.MESSAGES.createTitle("Config Property"));
        propertyWindow.setWidth(320);
        propertyWindow.setHeight(240);
        propertyWindow.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {

            }
        });

        propertyWindow.trapWidget(
                new NewPropertyWizard(new PropertyManagement() {
                    @Override
                    public void onCreateProperty(String reference, PropertyRecord prop) {
                        createProperty(ra, prop);
                    }

                    @Override
                    public void onDeleteProperty(String reference, PropertyRecord prop) {

                    }

                    @Override
                    public void onChangeProperty(String reference, PropertyRecord prop) {

                    }

                    @Override
                    public void launchNewPropertyDialoge(String reference) {

                    }

                    @Override
                    public void closePropertyDialoge() {
                        propertyWindow.hide();
                    }
                }, "").asWidget()
        );

        propertyWindow.setGlassEnabled(true);
        propertyWindow.center();
    }

    public void onSavePoolConfig(final ConnectionDefinition connection, Map<String, Object> changeset) {

        if(null==selectedAdapter)
            throw new RuntimeException("selected adapter is null!");


        ModelNode proto = new ModelNode();
        proto.get(ADDRESS).set(Baseadress.get());
        proto.get(ADDRESS).add("subsystem", "resource-adapters");
        proto.get(ADDRESS).add("resource-adapter", selectedAdapter);
        proto.get(ADDRESS).add("connection-definitions", connection.getJndiName());

        ModelNode operation = poolAdapter.fromChangeset(changeset, proto);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {

                ResponseWrapper<Boolean> response = ModelAdapter.wrapBooleanResponse(result);
                if(response.getUnderlying())
                    Console.info(Console.MESSAGES.saved("Pool Settings"));
                else
                    Console.error(Console.MESSAGES.saveFailed("Pool Settings "+ connection.getJndiName()), response.getResponse().toString());

                loadAdapter(true);
            }
        });
    }

    public void onDeletePoolConfig(final ConnectionDefinition ra, PoolConfig entity) {
        Map<String, Object> resetValues = new HashMap<String, Object>();
        resetValues.put("minPoolSize", 0);
        resetValues.put("maxPoolSize", 20);
        resetValues.put("poolStrictMin", false);
        resetValues.put("poolPrefill", false);

        onSavePoolConfig(ra, resetValues);

    }

    public BeanFactory getFactory() {
        return factory;
    }

    public void closePropertyDialoge() {
        propertyWindow.hide();
    }

    public PlaceManager getPlaceManager() {
        return placeManager;
    }

    public void launchNewConnectionWizard() {
        window = new DefaultWindow(Console.MESSAGES.createTitle("Connection Definition"));
        window.setWidth(480);
        window.setHeight(360);

        window.trapWidget(
                new NewConnectionWizard(this).asWidget()
        );

        window.setGlassEnabled(true);
        window.center();
    }

    public void onDeleteConnection(ConnectionDefinition selection) {
        ModelNode operation = connectionMetaData.getAddress().asResource(
                Baseadress.get(),
                selectedAdapter, selection.getJndiName()
        );

        operation.get(OP).set(REMOVE);
        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode response = result.get();
                if(response.isFailure())
                    Console.error(Console.MESSAGES.deletionFailed("Connection Definition"));
                else
                    Console.info(Console.MESSAGES.deleted("Connection Definition"));

                loadAdapter(true);
            }
        });
    }

    public void onCreateConnection(ConnectionDefinition connectionDefinition) {
        closeDialoge();

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).setEmptyList();
        operation.get(OP).set(COMPOSITE);

        List<ModelNode> steps = new ArrayList<ModelNode>();

        ModelNode createConnectionOp = connectionAdapter.fromEntity(connectionDefinition);
        createConnectionOp.get(OP).set(ADD);
        ModelNode addressModel = connectionMetaData.getAddress().asResource(
                Baseadress.get(),
                selectedAdapter,
                connectionDefinition.getJndiName());

        createConnectionOp.get(ADDRESS).set(addressModel.get(ADDRESS));

        steps.add(createConnectionOp);

        // --

        if(connectionDefinition.getProperties()!=null && !connectionDefinition.getProperties().isEmpty())
        {

            ModelNode createPropOp = new ModelNode();
            createPropOp.get(OP).set(ADD);
            createPropOp.get(ADDRESS).set(addressModel.get(ADDRESS));

            for(PropertyRecord prop : connectionDefinition.getProperties())
            {
                createPropOp.get(ADDRESS).add("config-properties", prop.getKey());
                createPropOp.get(VALUE).set(prop.getValue());
            }

            steps.add(createPropOp);
        }


        operation.get(STEPS).set(steps);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                if(response.isFailure())
                    Console.error(Console.MESSAGES.addingFailed("Connection Definition"), response.getFailureDescription());
                else
                    Console.info(Console.MESSAGES.added("Connection Definition"));
                loadAdapter(true);

            }
        });

    }

    public void onSaveConnection(ConnectionDefinition entity, Map<String, Object> changedValues) {

        ModelNode address = connectionMetaData.getAddress().asResource(
                Baseadress.get(), selectedAdapter, entity.getJndiName());
        ModelNode operation = connectionAdapter.fromChangeset(changedValues, address);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                if(response.isFailure())
                    Console.error(Console.MESSAGES.modificationFailed("Connection Definition"), response.getFailureDescription());
                else
                    Console.info(Console.MESSAGES.modified("Connection Definition"));
                loadAdapter(true);
            }
        });
    }

    public void onCreateConnectionProperty(ConnectionDefinition connection, PropertyRecord prop) {
        ModelNode operation = connectionMetaData.getAddress().asResource(
                Baseadress.get(), selectedAdapter, connection.getJndiName());

        operation.get(ADDRESS).add("config-properties", prop.getKey());
        operation.get(OP).set(ADD);
        operation.get(VALUE).set(prop.getValue());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                if(response.isFailure())
                    Console.error(Console.MESSAGES.addingFailed("Connection Property"), response.getFailureDescription());
                else
                    Console.info(Console.MESSAGES.added("Connection Property"));
                loadAdapter(true);
            }
        });

    }

    public void onDeleteConnectionProperty(ConnectionDefinition connection, PropertyRecord prop) {

        if(null==selectedAdapter)
            throw new RuntimeException("selected adapter is null!");

        ModelNode operation = connectionMetaData.getAddress().asResource(
                Baseadress.get(), selectedAdapter, connection.getJndiName());

        operation.get(ADDRESS).add("config-properties", prop.getKey());
        operation.get(OP).set(REMOVE);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                if(response.isFailure())
                    Console.error(Console.MESSAGES.deletionFailed("Connection Property"), response.getFailureDescription());
                else
                    Console.info(Console.MESSAGES.deleted("Connection Property"));
                loadAdapter(true);
            }
        });
    }


    public void onCreateAdapterProperty(ResourceAdapter adapter, PropertyRecord prop) {
        ModelNode operation = raMetaData.getAddress().asResource(
                Baseadress.get(), adapter.getArchive());

        operation.get(ADDRESS).add("config-properties", prop.getKey());
        operation.get(OP).set(ADD);
        operation.get(VALUE).set(prop.getValue());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                if(response.isFailure())
                    Console.error(Console.MESSAGES.addingFailed("Config Property"), response.getFailureDescription());
                else
                    Console.info(Console.MESSAGES.added("Config Property"));
                loadAdapter(false);
            }
        });

    }

    public void onRemoveAdapterProperty(ResourceAdapter adapter, PropertyRecord prop) {
        ModelNode operation = raMetaData.getAddress().asResource(
                Baseadress.get(), adapter.getArchive());

        operation.get(ADDRESS).add("config-properties", prop.getKey());
        operation.get(OP).set(REMOVE);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                if(response.isFailure())
                    Console.error(Console.MESSAGES.deletionFailed("Config Property"), response.getFailureDescription());
                else
                    Console.info(Console.MESSAGES.deleted("Config Property"));
                loadAdapter(false);
            }
        });
    }

    public void onCreateAdminProperty(AdminObject entity, PropertyRecord prop) {
        ModelNode operation = raMetaData.getAddress().asResource(
                Baseadress.get(), selectedAdapter);
        operation.get(ADDRESS).add("admin-objects", entity.getName());
        operation.get(ADDRESS).add("config-properties", prop.getKey());

        operation.get(OP).set(ADD);
        operation.get(VALUE).set(prop.getValue());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                if(response.isFailure())
                    Console.error(Console.MESSAGES.addingFailed("Config Property"));
                else
                    Console.info(Console.MESSAGES.added("Config Property"));
                loadAdapter(true);
            }
        });
    }

    public void onRemoveAdminProperty(AdminObject entity, PropertyRecord prop) {
        ModelNode operation = raMetaData.getAddress().asResource(
                Baseadress.get(), selectedAdapter);
        operation.get(ADDRESS).add("admin-objects", entity.getName());
        operation.get(ADDRESS).add("config-properties", prop.getKey());

        operation.get(OP).set(REMOVE);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                if(response.isFailure())
                    Console.error(Console.MESSAGES.deletionFailed("Config Property"), response.getFailureDescription());
                else
                    Console.info(Console.MESSAGES.deleted("Config Property"));
                loadAdapter(true);
            }
        });
    }

    public void onSaveAdmin(AdminObject entity, Map<String, Object> changeset) {
        ModelNode addressModel = raMetaData.getAddress().asResource(
                Baseadress.get(), selectedAdapter);
        addressModel.get(ADDRESS).add("admin-objects", entity.getName());

        ModelNode operation = adminAdapter.fromChangeset(changeset, addressModel);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                if(response.isFailure())
                    Console.error(Console.MESSAGES.modificationFailed("Admin Object"), response.getFailureDescription());
                else
                    Console.info(Console.MESSAGES.modified("Admin Object"));
                loadAdapter(true);
            }
        });
    }

    public void launchNewAdminWizard() {
        window = new DefaultWindow(Console.MESSAGES.createTitle("admin object"));
        window.setWidth(480);
        window.setHeight(360);

        window.trapWidget(
                new NewAdminWizard(this).asWidget()
        );

        window.setGlassEnabled(true);
        window.center();
    }

    public void onCreateAdmin(AdminObject entity) {

        closeDialoge();

        if(null==entity.getName())
            entity.setName(entity.getJndiName());

        ModelNode addressModel = raMetaData.getAddress().asResource(
                Baseadress.get(), selectedAdapter);
        addressModel.get(ADDRESS).add("admin-objects", entity.getName());

        ModelNode operation = adminAdapter.fromEntity(entity);
        operation.get(OP).set(ADD);
        operation.get(ADDRESS).set(addressModel.get(ADDRESS).asObject());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                if(response.isFailure())
                    Console.error(Console.MESSAGES.addingFailed("Admin Object"), response.getFailureDescription());
                else
                    Console.info(Console.MESSAGES.added("Admin Object"));
                loadAdapter(true);
            }
        });
    }

    public void onRemoveAdmin(AdminObject entity) {
        ModelNode operation = raMetaData.getAddress().asResource(
                Baseadress.get(), selectedAdapter);
        operation.get(ADDRESS).add("admin-objects", entity.getName());
        operation.get(OP).set(REMOVE);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
               ModelNode response = result.get();
                if(response.isFailure())
                    Console.error(Console.MESSAGES.deletionFailed("Admin Object"), response.getFailureDescription());
                else
                    Console.info(Console.MESSAGES.deleted("Admin Object"));
                loadAdapter(true);
            }
        });
    }

    public void onDoFlush(ConnectionDefinition entity) {

        ModelNode operation = connectionMetaData.getAddress().asResource(
                Baseadress.get(), selectedAdapter, entity.getJndiName());

        operation.get(OP).set("flush-all-connection-in-pool");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode response  = result.get();
                if(response.isFailure())
                    Console.error(Console.MESSAGES.failed("Flush Pool"), response.getFailureDescription());
                else
                    Console.info(Console.MESSAGES.successful("Flush Pool"));
            }
        });
    }

    // https://issues.jboss.org/browse/AS7-3259
    public void enOrDisbaleConnection(ResourceAdapter ra, ConnectionDefinition selection) {
        ModelNode operation = connectionMetaData.getAddress().asResource(
                Baseadress.get(), selectedAdapter, selection.getJndiName());


        operation.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
        operation.get(NAME).set("enabled");
        operation.get(VALUE).set(selection.isEnabled());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if(response.isFailure())
                    Console.error(Console.MESSAGES.modificationFailed("Connection Definition"), response.getFailureDescription());
                else
                    Console.info(Console.MESSAGES.modified("Connection Definition"));
                loadAdapter(true);
            }
        });
    }

    public void enOrDisbaleAdminObject(ResourceAdapter ra, AdminObject selection) {
        ModelNode operation = adminMetaData.getAddress().asResource(
                Baseadress.get(), selectedAdapter, selection.getJndiName());

        operation.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
        operation.get(NAME).set("enabled");
        operation.get(VALUE).set(selection.isEnabled());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                if(response.isFailure())
                    Console.error(Console.MESSAGES.modificationFailed("Admin Object"), response.getFailureDescription());
                else
                    Console.info(Console.MESSAGES.modified("Admin Object"));
                loadAdapter(true);
            }
        });
    }
}
