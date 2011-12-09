package org.jboss.as.console.client.shared.runtime.tx;

import com.google.gwt.dom.client.Style;
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


        NumberColumn total = new NumberColumn("number-of-transactions","Total");

        Column[] cols = new Column[] {
                total.setBaseline(true),
                new NumberColumn("number-of-committed-transactions","Commited").setComparisonColumn(total),
                new NumberColumn("number-of-aborted-transactions","Aborted").setComparisonColumn(total),
                new NumberColumn("number-of-timed-out-transactions", "Timed Out").setComparisonColumn(total)
        };

        String title = "Transaction Execution";
        if(Console.visAPILoaded()) {
            sampler = new ColumnChartView(320,200, title)
                    .setColumns(cols)
                    .setTimelineSeries(false);
        }
        else
        {
            sampler = new PlainColumnView(title, "/subsystem=transactions")
                    .setColumns(cols)
                    .setWidth(100, Style.Unit.PCT);
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
