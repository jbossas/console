package org.jboss.as.console.client.debug;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.*;
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
import org.jboss.dmr.client.ModelDescriptionConstants;
import org.jboss.dmr.client.ModelNode;

import static org.jboss.dmr.client.ModelDescriptionConstants.OP;
import static org.jboss.dmr.client.ModelDescriptionConstants.OP_ADDR;

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
        void setRootJson(String json);
        void updateItem(String itemName, String json);

        void updateResource(String itemName, String json);
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
        String url = "http://localhost:9990/domain-api";
        request(url, new SimpleCallback()
        {
            @Override
            public void onResponseText(String response) {
                getView().setRootJson(response);
            }
        });

    }
    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), DebugToolsPresenter.TYPE_MainContent, this);
    }

    public void onTreeItemSelection(final ModelBrowserView.AddressableTreeItem item) {
        Log.debug("Request " + item.addressString());


        ModelNode operation = null;

        if(item.isTuple())
        {
            operation = new ModelNode();
            for(String addr : item.getAddress())
                operation.get(OP_ADDR).add(addr);

            operation.get(OP).set(ModelDescriptionConstants.READ_RESOURCE_OPERATION);

        }
        else
        {
            operation = new ModelNode();
            operation.get(OP).set(ModelDescriptionConstants.READ_CHILDREN_NAMES_OPERATION);
            operation.get("child-type").set(item.title);
        }

        System.out.println(operation.asString());

        String url = "http://localhost:9990/domain-api";
        post(url, operation.toJSONString(false), new SimpleCallback() {
            @Override
            public void onResponseText(String response) {
                System.out.println("> "+response);

                if(!item.isTuple())
                    getView().updateItem(item.title, response);
                else
                    getView().updateResource(item.title, response);
            }
        });

    }

    private void request(final String url, final SimpleCallback callback)
    {

        RequestBuilder rb = new RequestBuilder(
                RequestBuilder.GET,
                url
        );

        //rb.setHeader("Accept", "application/dmr-encoded");

        try {
            rb.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {

                    if(200==response.getStatusCode())
                    {
                        callback.onResponseText(response.getText());
                    }
                    else
                    {
                        Log.warn(response.getStatusCode() + " on " + url);
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


    private void post(final String url, String data, final SimpleCallback callback)
    {

        RequestBuilder rb = new RequestBuilder(
                RequestBuilder.POST,
                url
        );

        //rb.setHeader("Accept", "application/dmr-encoded");

        try {
            rb.sendRequest(data, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {

                    if(200==response.getStatusCode())
                    {
                        callback.onResponseText(response.getText());
                    }
                    else
                    {
                        Log.warn(response.getStatusCode() + " on "+ url);
                        Log.warn(response.getStatusText() );
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

    interface SimpleCallback {
        void onResponseText(String response);
    }
}
