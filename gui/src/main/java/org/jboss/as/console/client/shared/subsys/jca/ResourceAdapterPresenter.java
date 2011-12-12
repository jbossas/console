package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
import org.jboss.as.console.client.shared.subsys.jca.model.ConnectionDefinition;
import org.jboss.as.console.client.shared.subsys.jca.model.PoolConfig;
import org.jboss.as.console.client.shared.subsys.jca.model.ResourceAdapter;
import org.jboss.as.console.client.shared.subsys.jca.wizard.NewAdapterWizard;
import org.jboss.as.console.client.shared.subsys.jca.wizard.NewConnectionWizard;
import org.jboss.as.console.client.widgets.forms.AddressBinding;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.ModelNodeUtil;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
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

    private List<ResourceAdapter> resourceAdapters;
    private ApplicationMetaData metaData;

    private BeanMetaData raMetaData;
    private BeanMetaData connectionMetaData;
    private String selectedAdapter;

    private EntityAdapter<ConnectionDefinition> connectionAdapter;
    private EntityAdapter<ResourceAdapter> adapter;

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

        adapter  = new EntityAdapter<ResourceAdapter>(ResourceAdapter.class, metaData);
        connectionAdapter = new EntityAdapter<ConnectionDefinition>(ConnectionDefinition.class, metaData);
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {
        this.selectedAdapter = request.getParameter("name", null);
        if(selectedAdapter !=null)
            getView().setSelectedAdapter(selectedAdapter);
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
                ModelNode result = ModelNode.fromBase64(response.getResponseText());

                List<Property> children = result.get(RESULT).asPropertyList();
                List<ResourceAdapter> resourceAdapters = new ArrayList<ResourceAdapter>(children.size());

                for(Property child : children)
                {
                    ModelNode raModel = child.getValue();
                    //System.out.println(raModel);
                    ResourceAdapter resourceAdapter = adapter.fromDMR(raModel);
                    resourceAdapter.setConnectionDefinitions(new ArrayList<ConnectionDefinition>());

                    // connection definition
                    if(raModel.hasDefined("connection-definitions"))
                    {
                        List<Property> connections = raModel.get("connection-definitions").asPropertyList();
                        for(Property con : connections )
                        {
                            ConnectionDefinition connectionDefinition = connectionAdapter.fromDMR(con.getValue());
                            resourceAdapter.getConnectionDefinitions().add(connectionDefinition);

                        }
                    }

                    resourceAdapters.add(resourceAdapter);
                }

                ResourceAdapterPresenter.this.resourceAdapters = resourceAdapters;
                getView().setAdapters(resourceAdapters);

                if(refreshDetail && selectedAdapter!=null)
                    getView().setSelectedAdapter(selectedAdapter);
            }
        });
    }

    @Override
    protected void onReset() {
        super.onReset();
        loadAdapter(false);
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
                ModelNode result = ModelNode.fromBase64(dmrResponse.getResponseText());
                if(ModelNodeUtil.indicatesSuccess(result))
                    Console.info(Console.MESSAGES.deleted("resource adapter "+ra.getArchive()));
                else
                    Console.error(Console.MESSAGES.deletionFailed("resource adapter "+ra.getArchive()), result.toString());

                loadAdapter(false);
            }
        });

    }

    public void onSave(final ResourceAdapter ra, Map<String, Object> changedValues) {

        /*AddressBinding address = raMetaData.getAddress();
        ModelNode addressModel = address.asResource(Baseadress.get(), ra.getName());
        addressModel.get(OP).set(WRITE_ATTRIBUTE_OPERATION);


        EntityAdapter<ResourceAdapter> adapter = new EntityAdapter<ResourceAdapter>(
                ResourceAdapter.class, metaData
        );

        //HACK ALERT: separately write connection definition attributes (key = archive+jndi name)
        List<ModelNode> extraSteps = new ArrayList<ModelNode>();

        if(changedValues.containsKey("enabled")) {
            ModelNode enabled = createWriteAttributeOp(ra, addressModel, "enabled", (Boolean)changedValues.remove("enabled"));
            extraSteps.add(enabled);
        }


        ModelNode operation = adapter.fromChangeset(
                changedValues,
                addressModel,
                extraSteps.toArray(new ModelNode[] {})
        );

        System.out.println(operation);

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                Console.error("Error: Failed to update resource adapter", caught.getMessage());
                loadAdapter();
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                boolean success = response.get(OUTCOME).asString().equals(SUCCESS);

                if(success)
                    Console.info(Console.MESSAGES.saved("resource adapter " + ra.getName()));
                else
                    Console.error(Console.MESSAGES.saveFailed("resource adapter " + ra.getName()), response.toString());

                loadAdapter();
            }
        });
        */
    }

    public void launchNewAdapterWizard() {
        window = new DefaultWindow(Console.MESSAGES.createTitle("resource adapter"));
        window.setWidth(480);
        window.setHeight(360);

        window.setWidget(
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
                ModelNode result = ModelNode.fromBase64(dmrResponse.getResponseText());
                if(ModelNodeUtil.indicatesSuccess(result))
                    Console.info(Console.MESSAGES.added("resource adapter " + ra.getArchive()));
                else
                    Console.error(Console.MESSAGES.addingFailed("resource adapter " + ra.getArchive()), result.toString());

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
                ModelNode result = ModelNode.fromBase64(dmrResponse.getResponseText());
                if(ModelNodeUtil.indicatesSuccess(result))
                    Console.info(Console.MESSAGES.added("property " + prop.getKey()));
                else
                    Console.error(Console.MESSAGES.addingFailed("property " + prop.getKey()), result.toString());

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
                ModelNode result = ModelNode.fromBase64(dmrResponse.getResponseText());
                if(ModelNodeUtil.indicatesSuccess(result))
                    Console.info(Console.MESSAGES.deleted("property " + prop.getKey()));
                else
                    Console.error(Console.MESSAGES.deletionFailed("property " + prop.getKey()), result.toString());

                loadAdapter(false);
            }
        });
    }

    public void launchNewPropertyDialoge(final ResourceAdapter ra) {
        propertyWindow = new DefaultWindow(Console.MESSAGES.createTitle("Configuration Property"));
        propertyWindow.setWidth(320);
        propertyWindow.setHeight(240);
        propertyWindow.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {

            }
        });

        propertyWindow.setWidget(
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


    public void loadPoolConfig(final ResourceAdapter ra) {


        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "resource-adapters");
        operation.get(ADDRESS).add("resource-adapter", ra.getArchive());
        //operation.get(ADDRESS).add("connection-definitions", ra.getJndiName());

        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(INCLUDE_RUNTIME).set(Boolean.TRUE);

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                Console.error("Failed to load RA pool config", caught.getMessage());
            }

            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode response = ModelNode.fromBase64(result.getResponseText());

                ModelNode payload = response.get(RESULT).asObject();

                PoolConfig poolConfig = factory.poolConfig().as();

                if(payload.hasDefined("max-pool-size"))
                    poolConfig.setMaxPoolSize(payload.get("max-pool-size").asInt());
                else
                    poolConfig.setMaxPoolSize(-1);

                if(payload.hasDefined("min-pool-size"))
                    poolConfig.setMinPoolSize(payload.get("min-pool-size").asInt());
                else
                    poolConfig.setMinPoolSize(-1);

                if(payload.hasDefined("pool-prefill"))
                    poolConfig.setPoolPrefill(payload.get("pool-prefill").asBoolean());
                else
                    poolConfig.setPoolPrefill(false);

                if(payload.hasDefined("pool-use-strict-min"))
                    poolConfig.setPoolStrictMin(payload.get("pool-use-strict-min").asBoolean());
                else
                    poolConfig.setPoolStrictMin(false);

                //getView().setPoolConfig(ra.getArchive(), poolConfig);
            }
        });
    }

    public void onSavePoolConfig(final ResourceAdapter ra, Map<String, Object> changeset) {

        ModelNode proto = new ModelNode();
        proto.get(ADDRESS).set(Baseadress.get());
        proto.get(ADDRESS).add("subsystem", "resource-adapters");
        proto.get(ADDRESS).add("resource-adapter", ra.getArchive());
        //proto.get(ADDRESS).add("connection-definitions", ra.getJndiName());

        EntityAdapter<PoolConfig> adapter = new EntityAdapter<PoolConfig>(
                PoolConfig.class, metaData
        );

        ModelNode operation = adapter.fromChangeset(changeset, proto);

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                Console.error("Failed to update RA pool config", caught.getMessage());
            }

            @Override
            public void onSuccess(DMRResponse result) {

                ResponseWrapper<Boolean> response = ModelAdapter.wrapBooleanResponse(result);
                if(response.getUnderlying())
                    Console.info(Console.MESSAGES.saved("pool settings"));
                else
                    Console.error(Console.MESSAGES.saveFailed("pool settings "+ra.getArchive()), response.getResponse().toString());

                loadPoolConfig(ra);
            }
        });
    }

    public void onDeletePoolConfig(final ResourceAdapter ra, PoolConfig entity) {
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
        window = new DefaultWindow(Console.MESSAGES.createTitle("connection definition"));
        window.setWidth(480);
        window.setHeight(360);

        window.setWidget(
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
                Console.info("Success: Removed connection definition");
                loadAdapter(true);
            }
        });
    }

    public void onCreateConnection(ConnectionDefinition connectionDefinition) {
        closeDialoge();

        ModelNode operation = connectionAdapter.fromEntity(connectionDefinition);
        operation.get(OP).set(ADD);
        ModelNode addressModel = connectionMetaData.getAddress().asResource(
                Baseadress.get(),
                selectedAdapter,
                connectionDefinition.getJndiName());

        operation.get(ADDRESS).set(addressModel.get(ADDRESS));

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                Console.info("Success: Add connection definition");
                loadAdapter(true);

            }
        });

    }

}
