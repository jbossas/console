package org.jboss.as.console.client.shared.runtime.web;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
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
import org.jboss.as.console.client.shared.subsys.web.LoadConnectorCmd;
import org.jboss.as.console.client.shared.subsys.web.model.HttpConnector;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.dmr.client.ModelNode;

import java.util.Collections;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 12/9/11
 */
public class WebMetricPresenter extends Presenter<WebMetricPresenter.MyView, WebMetricPresenter.MyProxy>
        implements ServerSelectionEvent.ServerSelectionListener {

    private DispatchAsync dispatcher;
    private RevealStrategy revealStrategy;
    private CurrentServerSelection serverSelection;
    private HttpConnector selectedConnector;
    private BeanFactory factory;

    @ProxyCodeSplit
    @NameToken(NameTokens.WebMetricPresenter)
    public interface MyProxy extends Proxy<WebMetricPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(WebMetricPresenter presenter);
        void clearSamples();
        void setConnectorMetric(Metric metric);
        void setConnectors(List<HttpConnector> list);
    }

    @Inject
    public WebMetricPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            DispatchAsync dispatcher,
            ApplicationMetaData metaData, RevealStrategy revealStrategy,
            CurrentServerSelection serverSelection, BeanFactory factory) {
        super(eventBus, view, proxy);

        this.dispatcher = dispatcher;
        this.revealStrategy = revealStrategy;
        this.serverSelection = serverSelection;
        this.factory = factory;
    }

    public void setSelectedConnector(HttpConnector selection) {
        this.selectedConnector = selection;
        if(selection!=null)
            loadConnectorMetrics();

    }

    @Override
    public void onServerSelection(String hostName, ServerInstance server) {

         Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                getView().clearSamples();
                if(isVisible()) refresh();
            }
         });
    }

    public void refresh() {

        if(!serverSelection.isActive()) {
            Console.warning(Console.CONSTANTS.common_err_server_not_active());
            getView().setConnectors(Collections.EMPTY_LIST);
            getView().clearSamples();
            return;
        }

        LoadConnectorCmd cmd = new LoadConnectorCmd(dispatcher, factory);
        cmd.execute(new SimpleCallback<List<HttpConnector>>() {
            @Override
            public void onSuccess(List<HttpConnector> result) {
                getView().setConnectors(result);
            }
        });

    }

    private void loadConnectorMetrics() {

        if(null==selectedConnector)
            throw new RuntimeException("connector selection is null!");

        getView().clearSamples();

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(RuntimeBaseAddress.get());
        operation.get(ADDRESS).add("subsystem", "web");
        operation.get(ADDRESS).add("connector", selectedConnector.getName());

        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(INCLUDE_RUNTIME).set(true);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode response = dmrResponse.get();

                if(response.isFailure())
                {
                    Console.error(Console.MESSAGES.failed("Web Metrics"), response.getFailureDescription());
                }
                else
                {
                    ModelNode result = response.get(RESULT).asObject();

                    Metric metric = new Metric(
                            result.get("requestCount").asLong(),
                            result.get("errorCount").asLong(),
                            result.get("processingTime").asLong(),
                            result.get("maxTime").asLong()
                    );

                    getView().setConnectorMetric(metric);
                }
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
}
