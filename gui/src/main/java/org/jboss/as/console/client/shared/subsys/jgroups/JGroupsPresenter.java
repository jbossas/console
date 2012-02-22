package org.jboss.as.console.client.shared.subsys.jgroups;

import com.google.gwt.event.shared.EventBus;
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
import org.jboss.as.console.client.shared.properties.NewPropertyWizard;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 2/16/12
 */
public class JGroupsPresenter extends Presenter<JGroupsPresenter.MyView, JGroupsPresenter.MyProxy>
        implements PropertyManagement {

    private final PlaceManager placeManager;

    private RevealStrategy revealStrategy;
    private ApplicationMetaData metaData;
    private DispatchAsync dispatcher;
    private EntityAdapter<JGroupsProtocol> protocolAdapter;
    private EntityAdapter<JGroupsTransport> transportAdapter;
    private BeanMetaData beanMetaData;
    private BeanFactory factory;
    private String selectedStack;
    private DefaultWindow propertyWindow;
    private DefaultWindow window;

    public PlaceManager getPlaceManager() {
        return placeManager;
    }


    @ProxyCodeSplit
    @NameToken(NameTokens.JGroupsPresenter)
    public interface MyProxy extends Proxy<JGroupsPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(JGroupsPresenter presenter);

        void updateStacks(List<JGroupsStack> stacks);

        void setSelectedStack(String selectedStack);
    }

    @Inject
    public JGroupsPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,
            DispatchAsync dispatcher,
            RevealStrategy revealStrategy,
            ApplicationMetaData metaData, BeanFactory beanFactory) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.revealStrategy = revealStrategy;
        this.metaData = metaData;
        this.dispatcher = dispatcher;

        this.protocolAdapter = new EntityAdapter<JGroupsProtocol>(JGroupsProtocol.class, metaData);
        this.transportAdapter= new EntityAdapter<JGroupsTransport>(JGroupsTransport.class, metaData);

        this.factory = beanFactory;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }


    @Override
    protected void onReset() {
        super.onReset();
        loadStacks(true);
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {
        this.selectedStack = request.getParameter("name", null);
    }

    private void loadStacks(final boolean refreshDetails) {

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "jgroups");
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(CHILD_TYPE).set("stack");
        operation.get(RECURSIVE).set(true);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if (response.isFailure()) {
                    Console.error(Console.MESSAGES.failed("JGroups Stack"), response.getFailureDescription());
                } else {

                    List<JGroupsStack> stacks = new ArrayList<JGroupsStack>();

                    List<Property> subresources = response.get(RESULT).asPropertyList();
                    for(Property prop : subresources)
                    {
                        ModelNode model = prop.getValue();

                        JGroupsStack stack = factory.jGroupsStack().as();
                        stack.setName(prop.getName());

                        List<JGroupsProtocol> protocols = new ArrayList<JGroupsProtocol>();
                        if(model.hasDefined("protocol"))
                        {
                            if(!model.hasDefined("protocols"))
                                throw new RuntimeException("Protocol sort order not given!");

                            List<ModelNode> sortOrder = model.get("protocols").asList();
                            final List<String> keys = new LinkedList<String>();
                            for(ModelNode key : sortOrder)
                                keys.add(key.asString());

                            // parse protocols
                            List<Property> items = model.get("protocol").asPropertyList();

                            // todo: https://issues.jboss.org/browse/AS7-3863
                            Collections.sort(items, new Comparator<Property>() {
                                @Override
                                public int compare(Property property, Property property1) {
                                    int firstIdx = keys.indexOf(property.getName());
                                    int secondIdx = keys.indexOf(property1.getName());

                                    if(firstIdx<secondIdx) return -1;
                                    if(firstIdx>secondIdx) return 1;
                                    return 0;
                                }
                            });

                            for(Property item : items)
                            {
                                ModelNode protocolModel = item.getValue();
                                JGroupsProtocol jGroupsProtocol = protocolAdapter.fromDMR(protocolModel);
                                jGroupsProtocol.setProperties(new ArrayList<PropertyRecord>());
                                // protocol properties
                                if(protocolModel.hasDefined("property"))
                                {
                                    List<Property> propItems = protocolModel.get("property").asPropertyList();
                                    for(Property p : propItems)
                                    {
                                        String name = p.getName();
                                        String value = p.getValue().asObject().get("value").asString();
                                        PropertyRecord propertyRecord = factory.property().as();
                                        propertyRecord.setKey(name);
                                        propertyRecord.setValue(value);

                                        jGroupsProtocol.getProperties().add(propertyRecord);
                                    }

                                }

                                protocols.add(jGroupsProtocol);
                            }

                        }
                        stack.setProtocols(protocols);

                        // TODO: parse transport

                        if(model.hasDefined("transport"))
                        {
                            List<Property> transportList = model.get("transport").asPropertyList();
                            if(transportList.isEmpty())
                            {
                                JGroupsTransport transport = factory.jGroupsTransport().as();
                                stack.setTransport(transport);
                            }
                            else
                            {
                                ModelNode transportDef = transportList.get(0).getValue();
                                JGroupsTransport transport = transportAdapter.fromDMR(transportDef);
                                stack.setTransport(transport);
                            }
                        }

                        stacks.add(stack);
                    }

                    getView().updateStacks(stacks);

                    if(refreshDetails)
                        getView().setSelectedStack(selectedStack);
                }
            }
        });
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }

    public void launchNewStackWizard() {
        window = new DefaultWindow(Console.MESSAGES.createTitle("Protocol Stack"));
        window.setWidth(640);
        window.setHeight(520);

        window.setWidget(
                new NewStackWizard(this).asWidget()
        );

        window.setGlassEnabled(true);
        window.center();
    }

    public void onCreateStack(JGroupsStack entity) {

        closeDialoge();

        // keep them in sync
        entity.setName(entity.getType());

        ModelNode composite = new ModelNode();
        composite.get(ADDRESS).setEmptyList();
        composite.get(OP).set(COMPOSITE);

        List<ModelNode> steps = new ArrayList<ModelNode>(entity.getProtocols().size()+2);

        // the stack itself
        ModelNode stackOp = new ModelNode();
        stackOp.get(ADDRESS).set(Baseadress.get());
        stackOp.get(ADDRESS).add("subsystem", "jgroups");
        stackOp.get(ADDRESS).add("stack", entity.getType());
        stackOp.get(OP).set("add");
        steps.add(stackOp);

        // transport

        ModelNode transportOp = new ModelNode();
        transportOp.get(ADDRESS).set(Baseadress.get());
        transportOp.get(ADDRESS).add("subsystem", "jgroups");
        transportOp.get(ADDRESS).add("stack", entity.getType());
        transportOp.get(ADDRESS).add("transport", "TRANSPORT");
        transportOp.get("type").set(entity.getTransportType());
        transportOp.get("socket-binding").set(entity.getTransportSocket());
        transportOp.get(OP).set("add");
        steps.add(transportOp);


        // add protocols

        for(JGroupsProtocol protocol : entity.getProtocols())
        {
            ModelNode protocolOp = new ModelNode();
            protocolOp.get(ADDRESS).set(Baseadress.get());
            protocolOp.get(ADDRESS).add("subsystem", "jgroups");
            protocolOp.get(ADDRESS).add("stack", entity.getType());
            protocolOp.get(OP).set("add-protocol");
            protocolOp.get("type").set(protocol.getType());
            if(protocol.getSocketBinding()!=null && !protocol.getSocketBinding().isEmpty())
                protocolOp.get("socket-binding").set(protocol.getSocketBinding());

            steps.add(protocolOp);
        }

        composite.get(STEPS).set(steps);

        dispatcher.execute(new DMRAction(composite), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if (response.isFailure()) {
                    Console.error(Console.MESSAGES.addingFailed("Protocol Stack"), response.getFailureDescription());
                } else {
                    Console.info(Console.MESSAGES.added("Protocol Stack"));

                    loadStacks(false);
                }
            }
        });
    }

    public void onDeleteStack(JGroupsStack entity) {
        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "jgroups");
        operation.get(ADDRESS).add("stack", entity.getName());
        operation.get(OP).set("remove");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if (response.isFailure()) {
                    Console.error(Console.MESSAGES.deletionFailed("Protocol Stack"), response.getFailureDescription());
                } else {
                    Console.info(Console.MESSAGES.deleted("Protocol Stack"));

                    loadStacks(false);
                }
            }
        });
    }

    public void launchNewProtocolWizard() {
        window = new DefaultWindow(Console.MESSAGES.createTitle("Protocol Property"));
        window.setWidth(480);
        window.setHeight(360);

        window.setWidget(
                new NewProtocolWizard(this).asWidget()
        );

        window.setGlassEnabled(true);
        window.center();
    }

    public void onDeleteProtocol(JGroupsProtocol editedEntity) {
        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "jgroups");
        operation.get(ADDRESS).add("stack", selectedStack);
        operation.get(OP).set("remove-protocol");
        operation.get("type").set(editedEntity.getType());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if (response.isFailure()) {
                    Console.error(Console.MESSAGES.deletionFailed("Protocol"), response.getFailureDescription());
                } else {
                    Console.info(Console.MESSAGES.deleted("Protocol"));

                    loadStacks(true);
                }
            }
        });

    }

    public void onSaveProtocol(JGroupsProtocol entity, Map<String, Object> changeset) {

        ModelNode address = new ModelNode();
        address.get(ADDRESS).set(Baseadress.get());
        address.get(ADDRESS).add("subsystem", "jgroups");
        address.get(ADDRESS).add("stack", selectedStack);
        address.get(ADDRESS).add("protocol", entity.getType());

        ModelNode operation = protocolAdapter.fromChangeset(changeset, address);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if (response.isFailure()) {
                    Console.error(Console.MESSAGES.modificationFailed("Protocol"), response.getFailureDescription());
                } else {
                    Console.info(Console.MESSAGES.modified("Protocol"));

                    loadStacks(true);
                }
            }
        });
    }

    public void onCreateProtocol(JGroupsProtocol entity) {

        closeDialoge();

        ModelNode operation = protocolAdapter.fromEntity(entity);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "jgroups");
        operation.get(ADDRESS).add("stack", selectedStack);
        operation.get(OP).set("add-protocol");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if (response.isFailure()) {
                    Console.error(Console.MESSAGES.addingFailed("Protocol"), response.getFailureDescription());
                } else {
                    Console.info(Console.MESSAGES.added("Protocol"));

                    loadStacks(true);
                }
            }
        });
    }

    public void closeDialoge() {
        window.hide();
    }


    @Override
    public void onCreateProperty(String reference, PropertyRecord prop) {
        closePropertyDialoge();

        String[] tokens = reference.split("_#_");

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "jgroups");
        operation.get(ADDRESS).add("stack", tokens[0]);
        operation.get(ADDRESS).add("protocol", tokens[1]);
        operation.get(ADDRESS).add("property", prop.getKey());
        operation.get(OP).set(ADD);
        operation.get(VALUE).set(prop.getValue());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if (response.isFailure()) {
                    Console.error(Console.MESSAGES.addingFailed("Protocol Property"), response.getFailureDescription());
                } else {
                    Console.info(Console.MESSAGES.added("Protocol Property"));

                    loadStacks(true);
                }
            }
        });
    }

    @Override
    public void onDeleteProperty(String reference, PropertyRecord prop) {
        String[] tokens = reference.split("_#_");

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "jgroups");
        operation.get(ADDRESS).add("stack", tokens[0]);
        operation.get(ADDRESS).add("protocol", tokens[1]);
        operation.get(ADDRESS).add("property", prop.getKey());
        operation.get(OP).set(REMOVE);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if (response.isFailure()) {
                    Console.error(Console.MESSAGES.deletionFailed("Protocol Property"), response.getFailureDescription());
                } else {
                    Console.info(Console.MESSAGES.deleted("Protocol Property"));

                    loadStacks(true);
                }
            }
        });
    }

    @Override
    public void onChangeProperty(String reference, PropertyRecord prop) {
        // not provided
    }

    @Override
    public void launchNewPropertyDialoge(String reference) {

        propertyWindow = new DefaultWindow(Console.MESSAGES.createTitle("Protocol Property"));
        propertyWindow.setWidth(320);
        propertyWindow.setHeight(240);

        propertyWindow.setWidget(
                new NewPropertyWizard(this, reference).asWidget()
        );

        propertyWindow.setGlassEnabled(true);
        propertyWindow.center();

    }

    @Override
    public void closePropertyDialoge() {
        if(propertyWindow!=null)
            propertyWindow.hide();
    }

    public void onSaveTransport(JGroupsTransport entity, Map<String, Object> changeset) {
        ModelNode address = new ModelNode();
        address.get(ADDRESS).set(Baseadress.get());
        address.get(ADDRESS).add("subsystem", "jgroups");
        address.get(ADDRESS).add("stack", selectedStack);
        address.get(ADDRESS).add("transport", "TRANSPORT");

        ModelNode operation = transportAdapter.fromChangeset(changeset, address);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if (response.isFailure()) {
                    Console.error(Console.MESSAGES.modificationFailed("Transport"), response.getFailureDescription());
                } else {
                    Console.info(Console.MESSAGES.modified("Transport"));

                    loadStacks(true);
                }
            }
        });
    }
}
