package org.jboss.as.console.client.standalone.runtime;

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
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.runtime.Metric;
import org.jboss.as.console.client.shared.runtime.tx.TXMetricManagement;
import org.jboss.as.console.client.shared.runtime.tx.TXMetricView;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.tx.model.TransactionManager;
import org.jboss.as.console.client.widgets.forms.AddressBinding;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.dmr.client.ModelNode;

import java.util.ArrayList;
import java.util.List;
import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 11/3/11
 */
public class TXMetricPresenter extends Presenter<TXMetricPresenter.MyView, TXMetricPresenter.MyProxy>
    implements TXMetricManagement {

    private final PlaceManager placeManager;
    private DispatchAsync dispatcher;
    private ApplicationMetaData metaData;
    private AddressBinding addressBinding;
    private EntityAdapter<TransactionManager> entityAdapter;

    @ProxyCodeSplit
    @NameToken(NameTokens.TXMetrics)
    public interface MyProxy extends Proxy<TXMetricPresenter>, Place {
    }

    public interface MyView extends TXMetricView  {
        void setPresenter(TXMetricManagement presenter);
        void setTxMetric(Metric txMetric);
        void setRollbackMetric(Metric rollbackMetric);
        void setServerNames(List<String> serverNames);
        void recycleCharts();
    }

    @Inject
    public TXMetricPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, DispatchAsync dispatcher,
            ApplicationMetaData metaData) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.dispatcher = dispatcher;
        this.metaData = metaData;

        this.addressBinding = metaData.getBeanMetaData(TransactionManager.class).getAddress();
        this.entityAdapter = new EntityAdapter<TransactionManager>(TransactionManager.class, metaData);
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }


    @Override
    protected void onHide() {
        super.onHide();
        getView().recycleCharts();
    }

    @Override
    protected void onReset() {
        super.onReset();
        refresh();
        loadServerConfigurations();
    }

    private void loadServerConfigurations() {
        List<String> names = new ArrayList<String>();
        names.add("Server One (Production)");
        names.add("Server Three");
        names.add("Server Staging");
        names.add("Load Test Environment");

        getView().setServerNames(names);
    }

    @Override
    protected void revealInParent() {
       RevealContentEvent.fire(getEventBus(), StandaloneRuntimePresenter.TYPE_MainContent, this);
    }


    @Override
    public void refresh() {

        ModelNode operation = addressBinding.asResource(Baseadress.get());
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(INCLUDE_RUNTIME).set(true);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode result = ModelNode.fromBase64(dmrResponse.getResponseText());
                TransactionManager metrics = entityAdapter.fromDMR(result.get(RESULT));

                getView().setTxMetric(new Metric(
                        metrics.getNumTransactions(),
                        metrics.getNumCommittedTransactions(),
                        metrics.getNumAbortedTransactions(),
                        metrics.getNumTimeoutTransactions()
                        ));

                getView().setRollbackMetric(new Metric(
                        metrics.getNumApplicationRollback(),
                        metrics.getNumResourceRollback()
                ));

            }
        });
    }
}
