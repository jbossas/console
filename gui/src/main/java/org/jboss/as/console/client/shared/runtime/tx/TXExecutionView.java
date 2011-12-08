package org.jboss.as.console.client.shared.runtime.tx;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.runtime.Metric;
import org.jboss.as.console.client.shared.runtime.Sampler;
import org.jboss.as.console.client.shared.runtime.charts.Column;
import org.jboss.as.console.client.shared.runtime.charts.ColumnChartView;
import org.jboss.as.console.client.shared.runtime.charts.NumberColumn;
import org.jboss.as.console.client.shared.runtime.plain.PlainColumnView;
import org.jboss.as.console.client.shared.subsys.tx.TransactionPresenter;

/**
 * @author Heiko Braun
 * @date 10/25/11
 */
public class TXExecutionView implements Sampler {

    private TransactionPresenter presenter;
    private Sampler sampler = null;

    public TXExecutionView() {
        this.presenter = presenter;
    }

    public Widget asWidget() {
        return displayStrategy();
    }

    private Widget displayStrategy() {


        NumberColumn total = new NumberColumn("Total");

        Column[] cols = new Column[] {
                total.setVisible(false),
                new NumberColumn("Commited").setComparisonColumn(total),
                new NumberColumn("Aborted").setComparisonColumn(total),
                new NumberColumn("Timed Out").setComparisonColumn(total)
        };

        String title = "Transaction Execution";
        if(Console.visAPILoaded()) {
            sampler = new ColumnChartView(320,200, title)
                    .setColumns(cols)
                    .setTimelineSeries(false);
        }
        else
        {
            sampler = new PlainColumnView(title)
                    .setColumns(cols);
        }

        return sampler.asWidget();
    }

    @Override
    public void addSample(Metric metric) {
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
