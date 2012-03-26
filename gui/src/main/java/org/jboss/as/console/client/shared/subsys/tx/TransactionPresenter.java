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
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.model.ModelAdapter;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.tx.model.TransactionManager;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.as.console.spi.Subsystem;
import org.jboss.dmr.client.ModelNode;

import java.util.Map;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;


/**
 * @author Heiko Braun
 * @date 10/25/11
 */
public class TransactionPresenter extends Presenter<TransactionPresenter.MyView, TransactionPresenter.MyProxy> {

    private final PlaceManager placeManager;
    private DispatchAsync dispatcher;
    private RevealStrategy revealStrategy;
    private ApplicationMetaData metaData;
    private BeanMetaData beanMetaData ;
    private EntityAdapter<TransactionManager> entityAdapter;

    @ProxyCodeSplit
    @NameToken(NameTokens.TransactionPresenter)
    @Subsystem
    public interface MyProxy extends Proxy<TransactionPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(TransactionPresenter presenter);
        void setTransactionManager(TransactionManager transactionManager);
    }

    @Inject
    public TransactionPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,DispatchAsync dispatcher,
            RevealStrategy revealStrategy,
            ApplicationMetaData metaData)
    {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.dispatcher = dispatcher;
        this.revealStrategy = revealStrategy;
        this.metaData = metaData;

        this.beanMetaData = metaData.getBeanMetaData(TransactionManager.class);
        this.entityAdapter = new EntityAdapter<TransactionManager>(TransactionManager.class, metaData);
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
        operation.get(INCLUDE_RUNTIME).set(true);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode response = dmrResponse.get();
                TransactionManager transactionManager = entityAdapter.fromDMR(response.get(RESULT));
                getView().setTransactionManager(transactionManager);
            }
        });

    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }

    public void onSaveConfig(Map<String, Object> changeset) {
        ModelNode operation =
                entityAdapter.fromChangeset(
                        changeset,
                        beanMetaData.getAddress().asResource(Baseadress.get()
                        )
                );

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode response = dmrResponse.get();

                boolean success = ModelAdapter.wasSuccess(response);
                if(success)
                    Console.info(Console.MESSAGES.modified("Transaction Manager"));
                else
                    Console.error(Console.MESSAGES.modificationFailed("Transaction Manager"), response.getFailureDescription());

                loadModel();
            }
        });
    }
}
