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
public class TXExecutionView implements TXExecutionSampler {

    private TransactionPresenter presenter;
    private TXExecutionSampler sampler = null;

    @Deprecated
    public TXExecutionView(TransactionPresenter presenter) {
        this.presenter = presenter;
    }

    public TXExecutionView() {
        this.presenter = presenter;
    }

    public Widget asWidget() {
        return displayStrategy();
    }

    private Widget displayStrategy() {

        if(Console.visAPILoaded()) {
            sampler = new TXChartView(320,200, "Transaction Execution");
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

    @Override
    public void recycle() {
        sampler.recycle();
    }
}
