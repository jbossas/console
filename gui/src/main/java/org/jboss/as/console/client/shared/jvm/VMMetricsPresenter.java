package org.jboss.as.console.client.shared.jvm;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.LineChart;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.jvm.model.HeapMetric;
import org.jboss.as.console.client.shared.model.ModelAdapter;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.standalone.ServerMgmtApplicationPresenter;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.dmr.client.ModelNode;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 9/28/11
 */
public class VMMetricsPresenter extends Presenter<VMMetricsPresenter.MyView, VMMetricsPresenter.MyProxy> {

    private static final int POLL_INTERVAL = 5000;
    private DispatchAsync dispatcher;
    private BeanFactory factory;
    private PropertyMetaData metaData;
    private RevealStrategy revealStrategy;
    private EntityAdapter<HeapMetric> heapMetricAdapter;

    @ProxyCodeSplit
    @NameToken(NameTokens.VirtualMachine)
    public interface MyProxy extends Proxy<VMMetricsPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(VMMetricsPresenter presenter);
        void setHeap(HeapMetric heap);
        void setNonHeap(HeapMetric nonHeap);

        void attachCharts();
        void detachCharts();
    }

    @Inject
    public VMMetricsPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            RevealStrategy revealStrategy,
            DispatchAsync dispatcher, BeanFactory factory, PropertyMetaData propertyMetaData) {
        super(eventBus, view, proxy);

        this.revealStrategy = revealStrategy;
        this.metaData = propertyMetaData;
        this.factory = factory;
        this.dispatcher = dispatcher;

        this.heapMetricAdapter =   new EntityAdapter<HeapMetric>(HeapMetric.class, metaData);
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    protected void onHide() {
        super.onHide();

        getView().detachCharts();
    }


    @Override
    protected void onReset() {
        super.onReset();

        if(Console.visAPILoaded())
            getView().attachCharts();
        else
            Console.error("Failed load visualization API", "Charts will not be available.");

        loadVMStatus();

        Scheduler.get().scheduleFixedDelay(
                new Scheduler.RepeatingCommand() {
                    @Override
                    public boolean execute() {
                        loadVMStatus();
                        return isVisible();
                    }
                }, POLL_INTERVAL);

    }



    public void loadVMStatus() {

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).add("core-service", "platform-mbean");
        operation.get(ADDRESS).add("type", "memory");
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(INCLUDE_RUNTIME).set(true);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                Console.error("Error loading VM metrics", caught.getMessage());
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());

                if(ModelAdapter.wasSuccess(response))
                {
                    ModelNode payload = response.get(RESULT);

                    HeapMetric heap = heapMetricAdapter.fromDMR(payload.get("heap-memory-usage"));
                    HeapMetric nonHeap = heapMetricAdapter.fromDMR(payload.get("non-heap-memory-usage"));

                    getView().setHeap(heap);
                    getView().setNonHeap(nonHeap);
                }
                else
                {
                    Console.error("Failed to load VM metrics", response.toString());
                }
            }
        });
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), ServerMgmtApplicationPresenter.TYPE_MainContent, this);
    }
}
