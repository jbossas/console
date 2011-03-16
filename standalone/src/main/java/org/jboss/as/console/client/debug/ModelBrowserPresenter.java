package org.jboss.as.console.client.debug;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.*;
import com.google.gwt.requestfactory.rebind.model.RequestMethod;
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
import org.jboss.dmr.client.ModelNode;

/**
 * @author Heiko Braun
 * @date 3/16/11
 */
public class ModelBrowserPresenter extends Presenter<ModelBrowserPresenter.MyView, ModelBrowserPresenter.MyProxy> {

    private final PlaceManager placeManager;

    @ProxyCodeSplit
    @NameToken(NameTokens.ModelBrowserPresenter)
    public interface MyProxy extends Proxy<ModelBrowserPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(ModelBrowserPresenter presenter);

        void setRoot(ModelNode modelNode);
    }

    @Inject
    public ModelBrowserPresenter(EventBus eventBus, MyView view, MyProxy proxy,
                                 PlaceManager placeManager) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    protected void onReset() {
        super.onReset();
    }

    public void requestRootModel()
    {

        //GWT.getHostPageBaseURL() + "app/proxy/domain-api?recursive=true";
        String url = "http://localhost:9990/domain-api?recursive=true";

        RequestBuilder rb = new RequestBuilder(
                RequestBuilder.GET,
                url
        );

        rb.setHeader("Accept", "application/dmr-encoded");

        try {
            rb.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {

                    if(200==response.getStatusCode())
                    {
                        System.out.println("> "+response.getText());

                        ModelNode modelNode = ModelNode.fromBase64(response.getText());
                        //getView().setRoot(modelNode);
                    }
                }

                @Override
                public void onError(Request request, Throwable e) {
                    Log.error("request failed", e);
                }
            });
        } catch (RequestException e) {
            Log.error("Failed to request root model", e);
        }

    }
    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), DebugToolsPresenter.TYPE_MainContent, this);
    }
}
