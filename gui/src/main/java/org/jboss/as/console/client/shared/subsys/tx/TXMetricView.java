package org.jboss.as.console.client.shared.subsys.tx;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.subsys.tx.charts.TXChartView;
import org.jboss.as.console.client.shared.subsys.tx.charts.TXPlainView;
import org.jboss.as.console.client.shared.subsys.tx.model.TXMetric;

/**
 * @author Heiko Braun
 * @date 10/25/11
 */
public class TXMetricView implements TXMetricSampler {

    private TransactionPresenter presenter;
    private TXMetricSampler sampler = null;

    public TXMetricView(TransactionPresenter presenter) {
        this.presenter = presenter;
    }

    public Widget asWidget() {
        return displayStrategy();
    }

    private Widget displayStrategy() {

        if(Console.visAPILoaded()) {
            sampler = new TXChartView("");
        }
        else
        {
            sampler = new TXPlainView();
        }

        return sampler.asWidget();
    }

    @Override
    public void addSample(TXMetric metric) {
        sampler.addSample(metric);
    }

    @Override
    public void clearSamples() {
        sampler.clearSamples();
    }

    @Override
    public long numSamples() {
        return sampler.numSamples();
    }
}
