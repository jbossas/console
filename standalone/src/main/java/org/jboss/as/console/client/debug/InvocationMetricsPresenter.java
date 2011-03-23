package org.jboss.as.console.client.debug;

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
import org.jboss.as.console.client.shared.dispatch.InvocationMetrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Heiko Braun
 * @date 3/22/11
 */
public class InvocationMetricsPresenter extends Presenter<InvocationMetricsPresenter.MyView, InvocationMetricsPresenter.MyProxy> {

    private final PlaceManager placeManager;
    private InvocationMetrics metrics;

    @ProxyCodeSplit
    @NameToken(NameTokens.MetricsPresenter)
    public interface MyProxy extends Proxy<InvocationMetricsPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(InvocationMetricsPresenter presenterInvocation);

        void updateFrom(List<SimpleMetric> metrics);
    }

    @Inject
    public InvocationMetricsPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, InvocationMetrics metrics) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.metrics = metrics;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    protected void onReset() {
        super.onReset();
        refreshMetrics();
    }

    public void refreshMetrics() {
        Map<String,Double> invocations = metrics.getNumInvocations();
        Set<String> keys = invocations.keySet();
        List<SimpleMetric> values = new ArrayList<SimpleMetric>();
        for(String key : keys)
        {
            values.add(new SimpleMetric(key, invocations.get(key)));
        }

        Collections.sort(values);
        getView().updateFrom(values);
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), DebugToolsPresenter.TYPE_MainContent, this);
    }
}
