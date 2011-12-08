package org.jboss.as.console.client.shared.runtime.tx;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.events.HostSelectionEvent;
import org.jboss.as.console.client.domain.hosts.CurrentHostSelection;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.domain.model.SimpleCallback;
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

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 11/3/11
 */
public class TXMetricPresenter extends Presenter<TXMetricPresenter.MyView, TXMetricPresenter.MyProxy>
        implements TXMetricManagement , HostSelectionEvent.HostSelectionListener {

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
        void recycle();
        void setSupportServers(boolean b);

        void reset();
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
    public void onHostSelection(String hostName) {
        if(isVisible())
            loadServerConfigurations();
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
        getView().setSupportServers(!bootstrapContext.isStandalone());
        getEventBus().addHandler(HostSelectionEvent.TYPE, this);
    }

    @Override
    protected void onReset() {
        super.onReset();

        getView().recycle();
        loadServerConfigurations();
        refresh();
    }

    private void loadServerConfigurations() {

        if(!bootstrapContext.isStandalone())
        {

            if(!hostSelection.isSet())
                throw new RuntimeException("Host selection not set!");

            hostInfoStore.getServerInstances(hostSelection.getName(), new SimpleCallback<List<ServerInstance>>() {
                @Override
                public void onSuccess(List<ServerInstance> servers) {


                    List<ServerInstance> active = new LinkedList<ServerInstance>();
                    for(ServerInstance server : servers)
                        if(server.isRunning())
                            active.add(server);


                    // apply active servers
                    getView().setServer(active);

                    if(active.isEmpty())
                        getView().reset();

                }
            });
        }

    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInRuntimeParent(this);
    }


    @Override
    public void refresh() {

        if(!bootstrapContext.isStandalone())
        {
            Console.warning("Domain mode not yet supported");

            getView().setTxMetric(new Metric(0l,0l,0l,0l));

            getView().setRollbackMetric(new Metric(0l,0l));

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

                /*getView().setTxMetric(new Metric(
                        metrics.getNumTransactions(),
                        metrics.getNumCommittedTransactions(),
                        metrics.getNumAbortedTransactions(),
                        metrics.getNumTimeoutTransactions()
                        ));

                getView().setRollbackMetric(new Metric(
                        metrics.getNumApplicationRollback(),
                        metrics.getNumResourceRollback()
                ));

                        */

                provideRandomMetrics();



            }
        });
    }

    private void provideRandomMetrics() {

        final Random random = new Random(System.currentTimeMillis());

        int total = 175;
        int committed = random.nextInt(50);

        getView().setTxMetric(new Metric(
                total,
                committed,
                total-committed,
                0L));

        getView().setRollbackMetric(new Metric(
                        (long)random.nextInt(50),
                        (long)random.nextInt(75)
                ));
    }
}
