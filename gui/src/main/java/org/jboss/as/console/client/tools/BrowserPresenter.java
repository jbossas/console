package org.jboss.as.console.client.tools;

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
import org.jboss.as.console.client.core.MainLayoutPresenter;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 6/15/12
 */
public class BrowserPresenter extends Presenter<BrowserPresenter.MyView, BrowserPresenter.MyProxy> {

    private final PlaceManager placeManager;
    private DispatchAsync dispatcher;

    @ProxyCodeSplit
    @NameToken(NameTokens.DMRBrowser)
    public interface MyProxy extends Proxy<BrowserPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(BrowserPresenter presenter);

        void setModel(List<Property> properties);
    }

    @Inject
    public BrowserPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, DispatchAsync dispatcher) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.dispatcher = dispatcher;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }


    @Override
    protected void onReset() {
        super.onReset();

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).setEmptyList();
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(RECURSIVE).set(true);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                final ModelNode response = dmrResponse.get();
                getView().setModel(response.get(RESULT).asPropertyList());
            }
        });
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), ToolsPresenter.TYPE_MainContent, this);
    }
}
