package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.jca.model.ResourceAdapter;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 7/19/11
 */
public class ResourceAdapterPresenter extends Presenter<ResourceAdapterPresenter.MyView, ResourceAdapterPresenter.MyProxy> {

    private final PlaceManager placeManager;
    private RevealStrategy revealStrategy;
    private DispatchAsync dispatcher;
    private BeanFactory factory;

    @ProxyCodeSplit
    @NameToken(NameTokens.ResourceAdapterPresenter)
    public interface MyProxy extends Proxy<ResourceAdapterPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(ResourceAdapterPresenter presenter);
        void setAdapters(List<ResourceAdapter> adapters);
    }

    @Inject
    public ResourceAdapterPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, RevealStrategy revealStrategy,
            DispatchAsync dispatcher, BeanFactory factory) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.revealStrategy = revealStrategy;
        this.dispatcher = dispatcher;
        this.factory = factory;
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

                    // todo: https://issues.jboss.org/browse/AS7-1339

                    adapters.add(ra);

                }

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
}
