package org.jboss.as.console.client.shared.subsys.ws;

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
import org.jboss.as.console.client.shared.subsys.messaging.model.MessagingProvider;
import org.jboss.as.console.client.shared.subsys.ws.model.WebServiceEndpoint;
import org.jboss.as.console.client.widgets.DefaultWindow;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 6/10/11
 */
public class WebServicePresenter extends Presenter<WebServicePresenter.MyView, WebServicePresenter.MyProxy> {

    private final PlaceManager placeManager;
    private DispatchAsync dispatcher;
    private BeanFactory factory;
    private MessagingProvider providerEntity;
    private DefaultWindow window = null;
    private RevealStrategy revealStrategy;
    private PropertyMetaData propertyMetaData;

    @ProxyCodeSplit
    @NameToken(NameTokens.WebServicePresenter)
    public interface MyProxy extends Proxy<WebServicePresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(WebServicePresenter presenter);
        void updateEndpoints(List<WebServiceEndpoint> endpoints);
    }

    @Inject
    public WebServicePresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,DispatchAsync dispatcher,
            BeanFactory factory, RevealStrategy revealStrategy,
            PropertyMetaData propertyMetaData) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.dispatcher = dispatcher;
        this.factory = factory;
        this.revealStrategy = revealStrategy;
        this.propertyMetaData = propertyMetaData;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }


    @Override
    protected void onReset() {
        super.onReset();

        loadEndpoints();
    }

    private void loadEndpoints() {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "webservices");
        operation.get(CHILD_TYPE).set("endpoint");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());

                List<WebServiceEndpoint> endpoints = new ArrayList<WebServiceEndpoint>();
                if(response.hasDefined(RESULT))
                {
                    List<Property> props = response.get(RESULT).asPropertyList();
                    for(Property prop : props)
                    {
                        ModelNode value = prop.getValue();
                        WebServiceEndpoint endpoint = factory.webServiceEndpoint().as();
                        endpoint.setName(value.get("name").asString());
                        endpoint.setClassName(value.get("class").asString());
                        endpoint.setContext(value.get("context").asString());
                        endpoint.setType(value.get("type").asString());
                        endpoint.setWsdl(value.get("wsdl-url").asString());

                        endpoints.add(endpoint);
                    }
                }

                getView().updateEndpoints(endpoints);
            }
        });
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }
}
