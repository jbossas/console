package org.jboss.as.console.client.shared.subsys.tx;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.subsys.tx.charts.RollbackChartView;
import org.jboss.as.console.client.shared.subsys.tx.charts.RollbackPlainView;
import org.jboss.as.console.client.shared.subsys.tx.model.RollbackMetric;

/**
 * @author Heiko Braun
 * @date 10/25/11
 */
public class TXRollbackView implements TXRollbackSampler {

    private TransactionPresenter presenter;
    private TXRollbackSampler sampler = null;

    public TXRollbackView(TransactionPresenter presenter) {
        this.presenter = presenter;
    }

    public Widget asWidget() {
        return displayStrategy();
    }

    private Widget displayStrategy() {

        if(Console.visAPILoaded()) {
            sampler = new RollbackChartView("");
        }
        else
        {
            sampler = new RollbackPlainView();
        }

        return sampler.asWidget();
    }

    @Override
    public void addSample(RollbackMetric metric) {
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
