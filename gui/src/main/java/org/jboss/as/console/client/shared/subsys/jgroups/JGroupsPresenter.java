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
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
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
                        System.out.println(model);

                        JGroupsStack stack = factory.jGroupsStack().as();
                        stack.setName(prop.getName());

                        List<JGroupsProtocol> protocols = new ArrayList<JGroupsProtocol>();
                        if(model.hasDefined("protocol"))
                        {
                            // parse protocols
                            List<Property> items = model.get("protocol").asPropertyList();

                            for(Property item : items)
                            {
                                JGroupsProtocol jGroupsProtocol = protocolAdapter.fromDMR(item.getValue());
                                protocols.add(jGroupsProtocol);
                            }



                        }
                        stack.setProtocols(protocols);

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
        
    }

    public void onDeleteStack(JGroupsStack entity) {
        
    }

    public void launchNewProtocolWizard() {
        
    }

    public void onDeleteProtocol(JGroupsProtocol editedEntity) {
        
    }

    public void onSaveProtocol(JGroupsProtocol editedEntity, Map<String, Object> changeset) {
        
    }

    @Override
    public void onCreateProperty(String reference, PropertyRecord prop) {
        
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
        
    }
}
