package org.jboss.as.console.client.shared.subsys.jms;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.domain.profiles.ProfileMgmtPresenter;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.subsys.jms.model.JMSEndpoint;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/29/11
 */
public class JMSPresenter extends Presenter<JMSPresenter.MyView, JMSPresenter.MyProxy> {

    private final PlaceManager placeManager;
    private DispatchAsync dispatcher;
    private BeanFactory factory;

    @ProxyCodeSplit
    @NameToken(NameTokens.JMSPresenter)
    public interface MyProxy extends Proxy<JMSPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(JMSPresenter presenter);

        void updateEndpoints(List<JMSEndpoint> endpoints);
    }

    @Inject
    public JMSPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, DispatchAsync dispatcher,
            BeanFactory factory) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.dispatcher = dispatcher;
        this.factory = factory;
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

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), ProfileMgmtPresenter.TYPE_MainContent, this);
    }

    void loadEndpoints() {

        final List<JMSEndpoint> aggregated = new ArrayList<JMSEndpoint>();

        ModelNode operation = new ReadEndpointOperation("default", "queue");
        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = ModelNode.fromBase64(result.getResponseText());
                List<JMSEndpoint> queues= parseResponse(response);
                aggregated.addAll(queues);
            }
        });

        ModelNode topics = new ReadEndpointOperation("default", "topic");
        dispatcher.execute(new DMRAction(topics), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = ModelNode.fromBase64(result.getResponseText());
                List<JMSEndpoint> topics= parseResponse(response);
                aggregated.addAll(topics);

                getView().updateEndpoints(aggregated);
            }
        });


    }

    private List<JMSEndpoint> parseResponse(ModelNode response) {
        List<ModelNode> payload = response.get("result").asList();

        List<JMSEndpoint> endpoints = new ArrayList<JMSEndpoint>(payload.size());
        for(ModelNode item : payload)
        {
            // returned as type property (key=ds name)
            Property property = item.asProperty();
            ModelNode ep = property.getValue().asObject();
            String name = property.getName();

            try {
                JMSEndpoint model = factory.jmsEndpoint().as();
                model.setName(name);
                model.setJndiName(ep.get("entries").asList().get(0).asString());// TODO: fragile crap

                endpoints.add(model);

            } catch (IllegalArgumentException e) {
                Log.error("Failed to parse data source representation", e);
            }
        }
        return endpoints;
    }
}
