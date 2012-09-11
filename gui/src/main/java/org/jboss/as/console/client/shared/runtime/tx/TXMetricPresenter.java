package org.jboss.as.console.client.shared.runtime.tx;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.Proxy;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.plugins.RuntimeGroup;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.runtime.Metric;
import org.jboss.as.console.client.shared.runtime.RuntimeBaseAddress;
import org.jboss.as.console.client.shared.state.CurrentServerSelection;
import org.jboss.as.console.client.shared.state.ServerSelectionEvent;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.tx.model.TransactionManager;
import org.jboss.as.console.client.widgets.forms.AddressBinding;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.as.console.spi.RuntimeExtension;
import org.jboss.dmr.client.ModelNode;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 11/3/11
 */
public class TXMetricPresenter extends Presenter<TXMetricPresenter.MyView, TXMetricPresenter.MyProxy>
        implements TXMetricManagement , ServerSelectionEvent.ServerSelectionListener{

    private DispatchAsync dispatcher;
    private AddressBinding addressBinding;
    private EntityAdapter<TransactionManager> entityAdapter;
    private RevealStrategy revealStrategy;
    private CurrentServerSelection serverSelection;

    @ProxyCodeSplit
    @NameToken(NameTokens.TXMetrics)
    @RuntimeExtension(name="Transactions", group=RuntimeGroup.METRICS, key="transactions")
    public interface MyProxy extends Proxy<TXMetricPresenter>, Place {
    }

    public interface MyView extends TXMetricView  {
        void setPresenter(TXMetricManagement presenter);
        void setTxMetric(Metric txMetric);
        void setRollbackMetric(Metric rollbackMetric);
        void clearSamples();
    }

    @Inject
    public TXMetricPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            DispatchAsync dispatcher,
            ApplicationMetaData metaData, RevealStrategy revealStrategy,
            CurrentServerSelection serverSelection) {
        super(eventBus, view, proxy);

        this.dispatcher = dispatcher;
        this.revealStrategy = revealStrategy;
        this.serverSelection = serverSelection;

        this.addressBinding = metaData.getBeanMetaData(TransactionManager.class).getAddress();
        this.entityAdapter = new EntityAdapter<TransactionManager>(TransactionManager.class, metaData);
    }

    @Override
    public void onServerSelection(String hostName, ServerInstance server, ServerSelectionEvent.Source source) {

         Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                getView().clearSamples();
                if(isVisible()) refresh();
            }
         });
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
        getEventBus().addHandler(ServerSelectionEvent.TYPE, this);
    }

    @Override
    protected void onReset() {
        super.onReset();
        refresh();
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInRuntimeParent(this);
    }

    @Override
    public void refresh() {

        if(!serverSelection.isActive()) {
            Console.warning(Console.CONSTANTS.common_err_server_not_active());
            getView().clearSamples();
            return;
        }

        ModelNode operation = addressBinding.asResource(RuntimeBaseAddress.get());
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(INCLUDE_RUNTIME).set(true);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode result = dmrResponse.get();

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


                //provideRandomMetrics();

            }
        });
    }

}
