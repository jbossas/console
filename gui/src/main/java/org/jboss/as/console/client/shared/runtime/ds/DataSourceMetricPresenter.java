package org.jboss.as.console.client.shared.runtime.ds;

import com.google.gwt.core.client.Scheduler;
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
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.runtime.Metric;
import org.jboss.as.console.client.shared.runtime.RuntimeBaseAddress;
import org.jboss.as.console.client.shared.state.CurrentServerSelection;
import org.jboss.as.console.client.shared.state.ServerSelectionEvent;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.dmr.client.ModelNode;

import java.util.Collections;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 12/19/11
 */
public class DataSourceMetricPresenter extends Presenter<DataSourceMetricPresenter.MyView,
        DataSourceMetricPresenter.MyProxy>
        implements ServerSelectionEvent.ServerSelectionListener{

    private final PlaceManager placeManager;
    private DispatchAsync dispatcher;
    private RevealStrategy revealStrategy;
    private CurrentServerSelection serverSelection;
    private DataSource selectedDS;
    private BeanFactory factory;
    private EntityAdapter<DataSource> dataSourceAdapter;

    private LoadDataSourceCmd loadDSCmd;
    private DataSource selectedXA;

    @ProxyCodeSplit
    @NameToken(NameTokens.DataSourceMetricPresenter)
    public interface MyProxy extends Proxy<DataSourceMetricPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(DataSourceMetricPresenter presenter);
        void clearSamples();
        void setDatasources(List<DataSource> datasources, boolean isXA);
        void setDSPoolMetric(Metric poolMetric, boolean isXA);
        void setDSCacheMetric(Metric metric, boolean isXA);
    }

    @Inject
    public DataSourceMetricPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,  DispatchAsync dispatcher,
            ApplicationMetaData metaData, RevealStrategy revealStrategy,
            CurrentServerSelection serverSelection, BeanFactory factory) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;

        this.dispatcher = dispatcher;
        this.revealStrategy = revealStrategy;
        this.serverSelection = serverSelection;
        this.factory = factory;

        this.loadDSCmd = new LoadDataSourceCmd(dispatcher, metaData);

    }

    @Override
    public void onServerSelection(String hostName, final ServerInstance server) {

        getView().clearSamples();

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                if(isVisible()) refreshDatasources();
            }
        });
    }

    public void refreshDatasources() {

        if(!serverSelection.isActive()) {
            Console.warning(Console.CONSTANTS.common_err_server_not_active());
            getView().setDatasources(Collections.EMPTY_LIST, true);
            getView().setDatasources(Collections.EMPTY_LIST, false);
            getView().clearSamples();
            return;
        }

        // Regular Datasources
        loadDSCmd.execute(new SimpleCallback<List<DataSource>>() {
            @Override
            public void onSuccess(List<DataSource> result) {
                getView().setDatasources(result, false);
            }
        }, false);

        // XA Data Sources
        loadDSCmd.execute(new SimpleCallback<List<DataSource>>() {
            @Override
            public void onSuccess(List<DataSource> result) {
                getView().setDatasources(result, true);
            }
        }, true);
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
        refreshDatasources();
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInRuntimeParent(this);
    }

    public void setSelectedDS(DataSource currentSelection, boolean xa) {

        if(!currentSelection.isEnabled())
        {
            Console.error(Console.MESSAGES.subsys_jca_err_ds_notEnabled(currentSelection.getName()));
            getView().clearSamples();
            return;
        }

        if(xa) {
            this.selectedXA = currentSelection;
            if(selectedXA!=null)
                loadMetrics(true);
        }
        else {
            this.selectedDS = currentSelection;
            if(selectedDS!=null)
                loadMetrics(false);
        }
    }

    private void loadMetrics(boolean isXA) {
        loadDSPoolMetrics(isXA);
        loadDSCacheMetrics(isXA);
    }

    private void loadDSPoolMetrics(final boolean isXA) {

        DataSource target = isXA ? selectedXA : selectedDS;
        if(null==target)
            throw new RuntimeException("DataSource selection is null!");

        getView().clearSamples();

        String subresource = isXA ? "xa-data-source": "data-source";
        String name = target.getName();

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(RuntimeBaseAddress.get());
        operation.get(ADDRESS).add("subsystem", "datasources");
        operation.get(ADDRESS).add(subresource, name);
        operation.get(ADDRESS).add("statistics", "pool");

        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(INCLUDE_RUNTIME).set(true);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode response = dmrResponse.get();

                if(response.isFailure())
                {
                    Console.error(Console.MESSAGES.failed("Datasource Metrics"), response.getFailureDescription());
                }
                else
                {
                    ModelNode result = response.get(RESULT).asObject();

                    long avail = result.get("AvailableCount").asLong();
                    long active = result.get("ActiveCount").asLong();
                    long max = result.get("MaxUsedCount").asLong();

                    Metric poolMetric = new Metric(
                            avail,active,max
                    );

                    getView().setDSPoolMetric(poolMetric, isXA);
                }
            }
        });
    }

    private void loadDSCacheMetrics(final boolean isXA) {

        DataSource target = isXA ? selectedXA : selectedDS;
        if(null==target)
            throw new RuntimeException("DataSource selection is null!");

        getView().clearSamples();

        String subresource = isXA ? "xa-data-source": "data-source";
        String name = target.getName();

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(RuntimeBaseAddress.get());
        operation.get(ADDRESS).add("subsystem", "datasources");
        operation.get(ADDRESS).add(subresource, name);
        operation.get(ADDRESS).add("statistics", "jdbc");

        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(INCLUDE_RUNTIME).set(true);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode response = dmrResponse.get();

                if(response.isFailure())
                {
                    Console.error(Console.MESSAGES.failed("Datasource Metrics"), response.getFailureDescription());
                }
                else
                {
                    ModelNode result = response.get(RESULT).asObject();

                    long size = result.get("PreparedStatementCacheCurrentSize").asLong();
                    long hit = result.get("PreparedStatementCacheHitCount").asLong();
                    long miss = result.get("PreparedStatementCacheMissCount").asLong();

                    Metric metric = new Metric(
                            size,hit,miss
                    );

                    getView().setDSCacheMetric(metric, isXA);
                }
            }
        });
    }
}
