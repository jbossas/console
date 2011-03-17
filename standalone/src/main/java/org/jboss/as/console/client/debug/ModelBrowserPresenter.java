package org.jboss.as.console.client.debug;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.*;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.TreeItem;
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

import java.util.Set;

import static org.jboss.dmr.client.ModelDescriptionConstants.OP;
import static org.jboss.dmr.client.ModelDescriptionConstants.OP_ADDR;

/**
 * @author Heiko Braun
 * @date 3/16/11
 */
public class ModelBrowserPresenter extends Presenter<ModelBrowserPresenter.MyView, ModelBrowserPresenter.MyProxy> {

    private static final String DOMAIN_API_ENDPOINT = "http://localhost:9990/domain-api";
    private final PlaceManager placeManager;
    private String selectedOperation = ModelDescriptionConstants.READ_RESOURCE_OPERATION;

    @ProxyCodeSplit
    @NameToken(NameTokens.ModelBrowserPresenter)
    public interface MyProxy extends Proxy<ModelBrowserPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(ModelBrowserPresenter presenter);
        void updateItem(String itemName, String json);
        void updateRequest(String itemName, String json);
        void updateResponse(String itemName, String json);

        void addItem(TreeItem item);
        void clearTree();
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

    public void reloadRootModel()
    {
        getView().clearTree();
        //GWT.getHostPageBaseURL() + "app/proxy/domain-api?recursive=true";
        request(DOMAIN_API_ENDPOINT, new SimpleCallback() {
            @Override
            public void onResponseText(String response) {
                JSONObject root = JSONParser.parse(response).isObject();

                Set<String> properties = root.keySet();
                for (final String title : properties) {
                    final TreeItem item = new AddressableTreeItem(title, title);
                    getView().addItem(item);

                    ModelNode operation = new ModelNode();
                    operation.get(OP).set(ModelDescriptionConstants.READ_CHILDREN_NAMES_OPERATION);
                    operation.get("child-type").set(title);

                    post(DOMAIN_API_ENDPOINT, operation.toJSONString(false), new SimpleCallback() {
                        @Override
                        public void onResponseText(String response) {

                            getView().updateItem(title, response);
                        }
                    });
                }
            }
        });
    };

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), DebugToolsPresenter.TYPE_MainContent, this);
    }

    public void onTreeItemSelection(final AddressableTreeItem item) {
        Log.debug("Request " + item.addressString());

        ModelNode operation = null;

        operation = new ModelNode();
        ModelNode addr = operation.get(OP_ADDR).setEmptyList();
        if(item.isTuple())
        {
            for(int i=0; i<item.getAddress().size(); i+=2)
            {
                addr.add(item.getAddress().get(i), item.getAddress().get(i+1));
            }
        }
        else
        {
            for(int i=0; i<item.getAddress().size(); i++)
            {
                addr.add(item.getAddress().get(i));
            }
        }

        // address
        /*for(String addr : item.getAddress())
            operation.get(OP_ADDR).add(addr);*/

        // operation
        operation.get(OP).set(selectedOperation);

        getView().updateRequest(item.title, operation.toString());

        post(DOMAIN_API_ENDPOINT, operation.toJSONString(true), new SimpleCallback() {
            @Override
            public void onResponseText(String response) {
                getView().updateResponse(item.title, response);
            }
        });

    }

    public void setOperation(String opValue) {
        this.selectedOperation = opValue;
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
