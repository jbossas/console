package org.jboss.as.console.client.shared.subsys.tx;

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
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.tx.model.TransactionManager;
import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.dmr.client.ModelNode;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;


/**
 * @author Heiko Braun
 * @date 10/25/11
 */
public class TransactionPresenter extends Presenter<TransactionPresenter.MyView, TransactionPresenter.MyProxy> {


    private final PlaceManager placeManager;
    private DispatchAsync dispatcher;
    private DefaultWindow window = null;
    private RevealStrategy revealStrategy;
    private PropertyMetaData metaData;
    private BeanMetaData beanMetaData ;

    @ProxyCodeSplit
    @NameToken(NameTokens.TransactionPresenter)
    public interface MyProxy extends Proxy<TransactionPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(TransactionPresenter presenter);
    }

    @Inject
    public TransactionPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,DispatchAsync dispatcher,
            RevealStrategy revealStrategy,
            PropertyMetaData metaData)
    {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.dispatcher = dispatcher;
        this.revealStrategy = revealStrategy;
        this.metaData = metaData;

        this.beanMetaData = metaData.getBeanMetaData(TransactionManager.class);
    }


    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }


    @Override
    protected void onReset() {
        super.onReset();
        loadModel();
    }

    private void loadModel() {

        ModelNode operation = beanMetaData.getAddress().asResource(Baseadress.get());
        operation.get(OP).set(READ_RESOURCE_OPERATION);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {

            }
        });

    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }
}
