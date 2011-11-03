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
import org.jboss.as.console.client.shared.runtime.Metric;
import org.jboss.as.console.client.shared.runtime.tx.TXMetricManagement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/3/11
 */
public class TXMetricPresenter extends Presenter<TXMetricPresenter.MyView, TXMetricPresenter.MyProxy>
    implements TXMetricManagement {

    private final PlaceManager placeManager;

    @ProxyCodeSplit
    @NameToken(NameTokens.TXMetrics)
    public interface MyProxy extends Proxy<TXMetricPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(TXMetricManagement presenter);
        void setTxMetric(Metric txMetric);
        void setRollbackMetric(Metric rollbackMetric);
        void setServerNames(List<String> serverNames);

        void recycleCharts();
    }

    @Inject
    public TXMetricPresenter(EventBus eventBus, MyView view, MyProxy proxy,
                             PlaceManager placeManager) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
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
    }

    @Override
    protected void revealInParent() {
       RevealContentEvent.fire(getEventBus(), StandaloneRuntimePresenter.TYPE_MainContent, this);
    }


    @Override
    public void refresh() {

        getView().setTxMetric(new Metric(55, 12, 33, 5));
        getView().setRollbackMetric(new Metric(77, 12));

        List<String> names = new ArrayList<String>();
        names.add("Server One (Production)");
        names.add("Server Three");
        names.add("Server Staging");
        names.add("Load Test Environment");

        getView().setServerNames(names);
    }
}
