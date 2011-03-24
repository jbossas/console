package org.jboss.as.console.client.debug;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.dmr.client.ModelDescriptionConstants;
import org.jboss.dmr.client.ModelNode;

import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 3/16/11
 */
public class ModelBrowserPresenter extends Presenter<ModelBrowserPresenter.MyView, ModelBrowserPresenter.MyProxy> {

    private final PlaceManager placeManager;
    private String selectedOperation = ModelDescriptionConstants.READ_RESOURCE_OPERATION;
    private DispatchAsync dispatcher;

    @ProxyCodeSplit
    @NameToken(NameTokens.ModelBrowserPresenter)
    public interface MyProxy extends Proxy<ModelBrowserPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(ModelBrowserPresenter presenter);

        void updateRequest(String itemName, String json);
        void updateResponse(String itemName, String json);

        void clearTree();

        void addItem(TreeItem item);
        void updateItem(String itemName, String json);
    }

    @Inject
    public ModelBrowserPresenter(
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
    protected void onReveal() {
        super.onReveal();
        reloadRootModel();

    }

    @Override
    protected void onReset() {
        super.onReset();
    }

    public void reloadRootModel()
    {
        getView().clearTree();

        ModelNode rootOp = new ModelNode();
        rootOp.get(OP).set(ModelDescriptionConstants.READ_CHILDREN_TYPES_OPERATION);
        rootOp.get(ADDRESS).setEmptyList();

        dispatcher.execute(new DMRAction(rootOp), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Failed to root resource ", caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                loadChildren(result.getResponseText());
            }
        });
    }

    private void loadChildren(String base64) {
        ModelNode response = ModelNode.fromBase64(base64);
        List<ModelNode> result = response.get("result").asList();

        for (final ModelNode node : result) {

            final String title = node.asString();
            final TreeItem item = new AddressableTreeItem(title, title);
            getView().addItem(item);

            ModelNode operation = new ModelNode();
            operation.get(OP).set(ModelDescriptionConstants.READ_CHILDREN_NAMES_OPERATION);
            operation.get("child-type").set(title);

            dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
                @Override
                public void onFailure(Throwable caught) {
                    //Log.error("Failed to retrieve resource ", caught);
                }

                @Override
                public void onSuccess(DMRResponse result) {
                    getView().updateItem(title, result.getResponseText());
                }
            });

        }
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), DebugToolsPresenter.TYPE_MainContent, this);
    }

    public void onTreeItemSelection(final AddressableTreeItem item) {
        Log.debug("Request " + item.addressString());

        ModelNode operation = new ModelNode();
        //operation.get(ModelDescriptionConstants.RECURSIVE).set(true);
        ModelNode addr = operation.get(OP_ADDR).setEmptyList();

        // address
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

        // operation
        operation.get(OP).set(selectedOperation);

        if(selectedOperation.equals(ModelDescriptionConstants.READ_CHILDREN_NAMES_OPERATION))
        {
            operation.get(OP_ADDR).setEmptyList();
            operation.get(ModelDescriptionConstants.CHILD_TYPE).set(item.title);
        }

        getView().updateRequest(item.title, operation.toString());

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Failed to execute operation", caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                getView().updateResponse(item.title, response.toJSONString(false));
            }
        });

    }

    public void setOperation(String opValue) {
        this.selectedOperation = opValue;
    }

}
