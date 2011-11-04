package org.jboss.as.console.client.shared.runtime.tx;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.events.HostSelectionEvent;
import org.jboss.as.console.client.domain.hosts.CurrentHostSelection;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.domain.model.impl.HostInfoStoreImpl;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.runtime.Metric;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
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

    private DispatchAsync dispatcher;
    private ApplicationMetaData metaData;
    private AddressBinding addressBinding;
    private EntityAdapter<TransactionManager> entityAdapter;
    private RevealStrategy revealStrategy;
    private BootstrapContext bootstrapContext;
    private HostInformationStore hostInfoStore;
    private CurrentHostSelection hostSelection;

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
        void setSupportServers(boolean b);
    }

    @Inject
    public TXMetricPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, DispatchAsync dispatcher,
            ApplicationMetaData metaData, RevealStrategy revealStrategy,
            BootstrapContext bootstrapContext, HostInformationStore hostInfoStore,
            CurrentHostSelection hostSelection) {
        super(eventBus, view, proxy);

        this.dispatcher = dispatcher;
        this.metaData = metaData;
        this.revealStrategy = revealStrategy;
        this.hostSelection = hostSelection;

        this.addressBinding = metaData.getBeanMetaData(TransactionManager.class).getAddress();
        this.entityAdapter = new EntityAdapter<TransactionManager>(TransactionManager.class, metaData);
        this.bootstrapContext = bootstrapContext;
        this.hostInfoStore = hostInfoStore;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
        getView().setSupportServers(!bootstrapContext.isStandalone());
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

        hostInfoStore.getServerConfigurations(hostSelection.getName(), new SimpleCallback<List<Server>>() {
            @Override
            public void onSuccess(List<Server> servers) {
                List<String> names = new ArrayList<String>();
                for(Server s : servers)
                    names.add(s.getName());

                getView().setServerNames(names);
            }
        });

    }

    @Override
    protected void revealInParent() {
       revealStrategy.revealInRuntimeParent(this);
    }


    @Override
    public void refresh() {

        if(!bootstrapContext.isStandalone())
        {
            System.out.println("Domain mode not yet supported");
            return;
        }

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
