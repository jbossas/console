package org.jboss.as.console.client.shared.subsys.tx;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.subsys.tx.charts.TXChartView;
import org.jboss.as.console.client.shared.subsys.tx.charts.TXPlainView;

/**
 * @author Heiko Braun
 * @date 10/25/11
 */
public class TXMetricView {

    private TransactionPresenter presenter;
    private TXMetricSampler sampler = null;

    public TXMetricView(TransactionPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
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
}
