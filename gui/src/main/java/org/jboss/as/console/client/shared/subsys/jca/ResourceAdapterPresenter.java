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
import org.jboss.as.console.client.shared.subsys.jca.model.PoolConfig;
import org.jboss.as.console.client.shared.subsys.jca.model.ResourceAdapter;
import org.jboss.as.console.client.shared.subsys.jca.wizard.NewAdapterWizard;
import org.jboss.as.console.client.widgets.forms.AddressBinding;
import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
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

    public BeanFactory getFactory() {
        return factory;
    }

    public void closePropertyDialoge() {
        propertyWindow.hide();
    }


    @ProxyCodeSplit
    @NameToken(NameTokens.ResourceAdapterPresenter)
    public interface MyProxy extends Proxy<ResourceAdapterPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(ResourceAdapterPresenter presenter);
        void setAdapters(List<ResourceAdapter> adapters);
        void setEnabled(boolean b);
        void setPoolConfig(String name, PoolConfig poolConfig);
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
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    private void loadAdapter() {

        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "resource-adapters");
        operation.get(ADDRESS).add("resource-adapter", "*");
        operation.get(RECURSIVE).set(true);


        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse response) {
                ModelNode result = ModelNode.fromBase64(response.getResponseText());

                List<ModelNode> children = result.get(RESULT).asList();
                List<ResourceAdapter> resourceAdapters = new ArrayList<ResourceAdapter>(children.size());

                for(ModelNode child : children)
                {
                    ModelNode raConfig = child.get(RESULT);

                    List<Property> conDefs = raConfig.get("connection-definitions").asPropertyList();
                    for(Property conDef : conDefs)
                    {
                        // for each connection definition create an RA representation (archive+jndi=key)
                        ResourceAdapter ra = factory.resourceAdapter().as();
                        ra.setArchive(raConfig.get("archive").asString());
                        ra.setTransactionSupport(raConfig.get("transaction-support").asString());
                        ra.setName(ra.getArchive());

                        ModelNode connection = conDef.getValue();
                        ra.setEnabled(connection.get("enabled").asBoolean());

                        ra.setJndiName(connection.get("jndi-name").asString());
                        ra.setEnabled(connection.get("enabled").asBoolean());

                        ra.setConnectionClass(connection.get("class-name").asString());
                        ra.setPoolName(connection.get("pool-name").asString());

                        List<PropertyRecord> props = new ArrayList<PropertyRecord>();
                        if(connection.hasDefined("config-properties"))
                        {
                            List<Property> properties = connection.get("config-properties").asPropertyList();
                            for(Property prop : properties)
                            {
                                ModelNode propWrapper = prop.getValue();
                                String value = propWrapper.get("value").asString();

                                PropertyRecord propertyRepresentation = factory.property().as();
                                propertyRepresentation.setKey(prop.getName());
                                propertyRepresentation.setValue(value);
                                props.add(propertyRepresentation);
                            }

                        }
                        ra.setProperties(props);

                        resourceAdapters.add(ra);
                    }
                }

                ResourceAdapterPresenter.this.resourceAdapters = resourceAdapters;
                getView().setAdapters(resourceAdapters);

            }
        });
    }

    @Override
    protected void onReset() {
        super.onReset();
        loadAdapter();
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
                loadAdapter();
            }

            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode result = ModelNode.fromBase64(dmrResponse.getResponseText());
                if(ModelNodeUtil.indicatesSuccess(result))
                    Console.info(Console.MESSAGES.deleted("resource adapter "+ra.getName()));
                else
                    Console.error(Console.MESSAGES.deletionFailed("resource adapter "+ra.getName()), result.toString());

                loadAdapter();
            }
        });

    }

    public void onSave(final ResourceAdapter ra, Map<String, Object> changedValues) {

        AddressBinding address = raMetaData.getAddress();
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
    }

    private ModelNode createWriteAttributeOp(
            ResourceAdapter ra,
            ModelNode addressModel,
            String attributeName, boolean value) {

        ModelNode op = createBaseOp(ra, addressModel, attributeName);
        op.get(VALUE).set(value);
        return op;
    }

    private ModelNode createWriteAttributeOp(
            ResourceAdapter ra,
            ModelNode addressModel,
            String attributeName, String value) {

        ModelNode op = createBaseOp(ra, addressModel, attributeName);
        op.get(VALUE).set(value);
        return op;
    }

    private ModelNode createWriteAttributeOp(
            ResourceAdapter ra,
            ModelNode addressModel,
            String attributeName, long value) {

        ModelNode op = createBaseOp(ra, addressModel, attributeName);
        op.get(VALUE).set(value);
        return op;
    }

    private ModelNode createBaseOp(ResourceAdapter ra, ModelNode addressModel, String attributeName) {
        ModelNode op = new ModelNode();
        op.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
        op.get(ADDRESS).set(addressModel.get(ADDRESS));
        op.get(ADDRESS).add("connection-definitions", ra.getJndiName());
        op.get(NAME).set(attributeName);
        return op;
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

        ra.setEnabled(false);

        ModelNode operation = new ModelNode();
        operation.get(OP).set(COMPOSITE);
        operation.get(ADDRESS).setEmptyList();

        List<ModelNode> steps = new ArrayList<ModelNode>();

        if(!adapterExists(ra)) {
            // the top level ra element. this step may fail (if exists already)
            ModelNode createParent = new ModelNode();
            createParent.get(OP).set(ADD);
            createParent.get(ADDRESS).set(Baseadress.get());
            createParent.get(ADDRESS).add("subsystem","resource-adapters");
            createParent.get(ADDRESS).add("resource-adapter", ra.getArchive());
            createParent.get("archive").set(ra.getArchive());
            createParent.get("transaction-support").set(ra.getArchive());

            steps.add(createParent);
        }

        // the specific connection definition
        ModelNode createConnection = new ModelNode();
        createConnection.get(OP).set(ADD);
        createConnection.get(ADDRESS).set(Baseadress.get());
        createConnection.get(ADDRESS).add("subsystem","resource-adapters");
        createConnection.get(ADDRESS).add("resource-adapter", ra.getArchive());
        createConnection.get(ADDRESS).add("connection-definitions", ra.getJndiName());
        createConnection.get("jndi-name").set(ra.getJndiName());
        createConnection.get("class-name").set(ra.getConnectionClass());

        steps.add(createConnection);

        // connection properties

        for(PropertyRecord prop : ra.getProperties())
        {
            ModelNode createProp = new ModelNode();
            createProp.get(OP).set(ADD);
            createProp.get(ADDRESS).set(Baseadress.get());
            createProp.get(ADDRESS).add("subsystem","resource-adapters");
            createProp.get(ADDRESS).add("resource-adapter", ra.getArchive());
            createProp.get(ADDRESS).add("connection-definitions", ra.getJndiName());
            createProp.get(ADDRESS).add("config-properties", prop.getKey());
            createProp.get("value").set(prop.getValue());

            steps.add(createProp);

        }
        operation.get(STEPS).set(steps);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                loadAdapter();
            }

            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode result = ModelNode.fromBase64(dmrResponse.getResponseText());
                if(ModelNodeUtil.indicatesSuccess(result))
                    Console.info(Console.MESSAGES.added("resource adapter " + ra.getArchive()));
                else
                    Console.error(Console.MESSAGES.addingFailed("resource adapter " + ra.getArchive()), result.toString());

                loadAdapter();
            }
        });

    }

    private boolean adapterExists(ResourceAdapter ra) {
        boolean match = false;
        for(ResourceAdapter candidate : resourceAdapters)
        {
            if(candidate.getArchive().equals(ra.getArchive()))
            {
                match = true;
                break;
            }
        }
        return match;
    }

    public void createProperty(final ResourceAdapter ra, final PropertyRecord prop) {
        closePropertyDialoge();

        ModelNode createProp = new ModelNode();
        createProp.get(OP).set(ADD);
        createProp.get(ADDRESS).set(Baseadress.get());
        createProp.get(ADDRESS).add("subsystem","resource-adapters");
        createProp.get(ADDRESS).add("resource-adapter", ra.getArchive());
        createProp.get(ADDRESS).add("connection-definitions", ra.getJndiName());
        createProp.get(ADDRESS).add("config-properties", prop.getKey());
        createProp.get("value").set(prop.getValue());

        dispatcher.execute(new DMRAction(createProp), new SimpleCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                loadAdapter();
            }

            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode result = ModelNode.fromBase64(dmrResponse.getResponseText());
                if(ModelNodeUtil.indicatesSuccess(result))
                    Console.info(Console.MESSAGES.added("property " + prop.getKey()));
                else
                    Console.error(Console.MESSAGES.addingFailed("property " + prop.getKey()), result.toString());

                loadAdapter();
            }
        });

    }

    public void onDeleteProperty(ResourceAdapter ra, final PropertyRecord prop) {

        ModelNode operation = new ModelNode();
        operation.get(OP).set(REMOVE);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem","resource-adapters");
        operation.get(ADDRESS).add("resource-adapter", ra.getArchive());
        operation.get(ADDRESS).add("connection-definitions", ra.getJndiName());
        operation.get(ADDRESS).add("config-properties", prop.getKey());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                loadAdapter();
            }

            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode result = ModelNode.fromBase64(dmrResponse.getResponseText());
                if(ModelNodeUtil.indicatesSuccess(result))
                    Console.info(Console.MESSAGES.deleted("property " + prop.getKey()));
                else
                    Console.error(Console.MESSAGES.deletionFailed("property " + prop.getKey()), result.toString());

                loadAdapter();
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
        operation.get(ADDRESS).add("connection-definitions", ra.getJndiName());

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

                getView().setPoolConfig(ra.getArchive(), poolConfig);
            }
        });
    }

    public void onSavePoolConfig(final ResourceAdapter ra, Map<String, Object> changeset) {

        ModelNode proto = new ModelNode();
        proto.get(ADDRESS).set(Baseadress.get());
        proto.get(ADDRESS).add("subsystem", "resource-adapters");
        proto.get(ADDRESS).add("resource-adapter", ra.getArchive());
        proto.get(ADDRESS).add("connection-definitions", ra.getJndiName());

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


}
