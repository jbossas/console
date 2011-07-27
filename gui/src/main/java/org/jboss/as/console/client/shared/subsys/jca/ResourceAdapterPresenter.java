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
import org.jboss.as.console.client.shared.properties.NewPropertyWizard;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.jca.model.ResourceAdapter;
import org.jboss.as.console.client.shared.subsys.jca.wizard.NewAdapterWizard;
import org.jboss.as.console.client.widgets.forms.PropertyBinding;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.ModelNodeUtil;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 7/19/11
 */
public class ResourceAdapterPresenter
        extends Presenter<ResourceAdapterPresenter.MyView, ResourceAdapterPresenter.MyProxy>
        implements PropertyManagement {

    private final PlaceManager placeManager;
    private RevealStrategy revealStrategy;
    private DispatchAsync dispatcher;
    private BeanFactory factory;
    private DefaultWindow window;
    private DefaultWindow propertyWindow;

    private List<ResourceAdapter> resourceAdapters;
    private PropertyMetaData propertyMetaData;

    public BeanFactory getFactory() {
        return factory;
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.ResourceAdapterPresenter)
    public interface MyProxy extends Proxy<ResourceAdapterPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(ResourceAdapterPresenter presenter);
        void setAdapters(List<ResourceAdapter> adapters);

        void setEnabled(boolean b);
    }

    @Inject
    public ResourceAdapterPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, RevealStrategy revealStrategy,
            DispatchAsync dispatcher, BeanFactory factory, PropertyMetaData propertyMetaData) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.revealStrategy = revealStrategy;
        this.dispatcher = dispatcher;
        this.factory = factory;
        this.propertyMetaData = propertyMetaData;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    private void loadResourceAdapter() {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "resource-adapters");
        operation.get(CHILD_TYPE).set("resource-adapter");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse response) {
                ModelNode result = ModelNode.fromBase64(response.getResponseText());

                List<Property> props = result.get(RESULT).asPropertyList();
                List<ResourceAdapter> adapters = new ArrayList<ResourceAdapter>(props.size());

                for(Property prop : props) {
                    String name = prop.getName();
                    ModelNode model = prop.getValue();

                    ModelNode connDef = model.get("connection-definitions").asList().get(0);
                    ResourceAdapter ra = factory.resourceAdapter().as();
                    ra.setName(name);

                    ra.setJndiName(connDef.get("jndi-name").asString());
                    ra.setConnectionClass(connDef.get("class-name").asString());
                    ra.setPoolName(connDef.get("pool-name").asString());

                    ra.setTransactionSupport(model.get("transaction-support").asString());
                    ra.setArchive(model.get("archive").asString());

                    if(model.hasDefined("config-properties"))
                    {
                        List<Property> config = model.get("config-properties").asPropertyList();
                        List<PropertyRecord> propertyList = new ArrayList<PropertyRecord>(config.size());
                        for(Property cfg : config) {
                            PropertyRecord propModel = factory.property().as();
                            propModel.setKey(cfg.getName());
                            propModel.setValue(cfg.getValue().asString());

                            propertyList.add(propModel);
                        }

                        ra.setProperties(propertyList);
                    }

                    adapters.add(ra);

                }

                resourceAdapters = adapters;
                getView().setAdapters(adapters);

            }
        });
    }

    @Override
    protected void onReset() {
        super.onReset();
        loadResourceAdapter();
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }

    public void onDelete(final ResourceAdapter ra) {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(REMOVE);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "resource-adapters");
        operation.get(ADDRESS).add("resource-adapter", ra.getArchive());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                loadResourceAdapter();
            }

            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode result = ModelNode.fromBase64(dmrResponse.getResponseText());
                if(ModelNodeUtil.indicatesSuccess(result))
                    Console.info("Success: Removed RA "+ra.getName());
                else
                    Console.error("Error: Failed to remove RA "+ra.getName(), result.toString());

                loadResourceAdapter();
            }
        });

    }

    public void onSave(String name, Map<String, Object> changedValues) {

        getView().setEnabled(false);

        if(changedValues.isEmpty())
        {
            Console.warning("No changes saved!");
            return;
        }

        ModelNode proto = new ModelNode();
        proto.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
        proto.get(ADDRESS).set(Baseadress.get());
        proto.get(ADDRESS).add("subsystem", "resource-adapters");
        proto.get(ADDRESS).add("resource-adapter", name);


        List<PropertyBinding> bindings = propertyMetaData.getBindingsForType(ResourceAdapter.class);
        ModelNode operation  = ModelAdapter.detypedFromChangeset(proto, changedValues, bindings);

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                Console.error("Error: Failed to update resource adapter", caught.getMessage());
                loadResourceAdapter();
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                boolean success = response.get(OUTCOME).asString().equals(SUCCESS);

                if(success)
                    Console.info("Success: Update resource adapter ");
                else
                    Console.error("Failed: Update resource adapter", response.toString());

                loadResourceAdapter();
            }
        });
    }

    public void onEdit(ResourceAdapter editedEntity) {
        getView().setEnabled(true);
    }

    public void launchNewAdapterWizard() {
        window = new DefaultWindow("Create Resource Adapter");
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

        ModelNode operation = new ModelNode();
        operation.get(OP).set(ADD);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "resource-adapters");
        operation.get(ADDRESS).add("resource-adapter", ra.getArchive());

        operation.get("archive").set(ra.getArchive());
        operation.get("transaction-support").set(ra.getTransactionSupport());

        // submodel
        ModelNode conDef = new ModelNode();
        conDef.get("class-name").set(ra.getConnectionClass());
        conDef.get("jndi-name").set(ra.getJndiName());
        conDef.get("pool-name").set(ra.getPoolName());

        List<ModelNode> list = new ArrayList<ModelNode>();
        list.add(conDef);
        operation.get("connection-definitions").set(list);

        // poperties
        operation.get("config-properties").setEmptyList();
        for(PropertyRecord prop : ra.getProperties())
        {
            operation.get("config-properties").add(prop.getKey(), prop.getValue());
        }

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                loadResourceAdapter();
            }

            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode result = ModelNode.fromBase64(dmrResponse.getResponseText());
                if(ModelNodeUtil.indicatesSuccess(result))
                    Console.info("Success: Created RA "+ra.getName());
                else
                    Console.error("Error: Failed to create RA " + ra.getName(), result.toString());

                loadResourceAdapter();
            }
        });

    }

    // TODO: https://issues.jboss.org/browse/AS7-1379
    @Override
    public void onCreateProperty(final String ref, final PropertyRecord prop) {
        closePropertyDialoge();
        System.out.println("Create "+prop.getKey());

        ModelNode operation = new ModelNode();
        operation.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "resource-adapters");
        operation.get(ADDRESS).add("resource-adapter", ref);


        ResourceAdapter ra = resolveAdapter(ref);


        // poperties
        ModelNode cfg = new ModelNode();
        cfg.setEmptyList();
        for(PropertyRecord cfgProp : ra.getProperties())
        {
            cfg.add(cfgProp.getKey(), cfgProp.getValue());
        }

        operation.get(NAME).set("config-properties");
        operation.get(VALUE).set(cfg);

        System.out.println(operation);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                loadResourceAdapter();
            }

            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode result = ModelNode.fromBase64(dmrResponse.getResponseText());
                if(ModelNodeUtil.indicatesSuccess(result))
                    Console.info("Success: Created added property "+prop.getKey());
                else
                    Console.error("Error: Failed to add property " + prop.getKey(), result.toString());

                loadResourceAdapter();
            }
        });

    }

    private ResourceAdapter resolveAdapter(String reference)
    {
        ResourceAdapter match = null;
        for(ResourceAdapter ra : resourceAdapters)
        {
            if(ra.getArchive().equals(reference))
            {
                match = ra;
                break;
            }
        }

        return match;
    }

    @Override
    public void onDeleteProperty(String ref, PropertyRecord prop) {
        Console.error("Not implemented");

        // TODO: https://issues.jboss.org/browse/AS7-1381
    }

    @Override
    public void launchNewPropertyDialoge(String reference) {
        propertyWindow = new DefaultWindow("New Configuration Property");
        propertyWindow.setWidth(320);
        propertyWindow.setHeight(240);
        propertyWindow.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {

            }
        });

        propertyWindow.setWidget(
                new NewPropertyWizard(this, reference).asWidget()
        );

        propertyWindow.setGlassEnabled(true);
        propertyWindow.center();
    }

    @Override
    public void closePropertyDialoge() {
        propertyWindow.hide();
    }
}
