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
                            // parse protocols
                            List<Property> items = model.get("protocol").asPropertyList();

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
        // not used atm
    }

    public void onDeleteStack(JGroupsStack entity) {
        // not used atm
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


    // TODO: https://issues.jboss.org/browse/AS7-3791
    public void onDeleteProtocol(JGroupsProtocol editedEntity) {
        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "jgroups");
        operation.get(ADDRESS).add("stack", selectedStack);
        operation.get(ADDRESS).add("protocol", editedEntity.getType());
        operation.get(OP).set(REMOVE);

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

    public void onSaveProtocol(JGroupsProtocol editedEntity, Map<String, Object> changeset) {

    }

    // TODO: https://issues.jboss.org/browse/AS7-3791
    public void onCreateProtocol(JGroupsProtocol entity) {

        closeDialoge();

        ModelNode operation = protocolAdapter.fromEntity(entity);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "jgroups");
        operation.get(ADDRESS).add("stack", selectedStack);
        operation.get(ADDRESS).add("protocol", entity.getType());
        operation.get(OP).set(ADD);

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
}
