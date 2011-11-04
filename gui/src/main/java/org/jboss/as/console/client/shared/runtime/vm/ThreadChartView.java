package org.jboss.as.console.client.shared.runtime.vm;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.runtime.Metric;
import org.jboss.as.console.client.shared.runtime.Sampler;
import org.jboss.as.console.client.shared.runtime.charts.Column;
import org.jboss.as.console.client.shared.runtime.charts.LineChartView;
import org.jboss.as.console.client.shared.runtime.charts.NumberColumn;
import org.jboss.as.console.client.shared.runtime.plain.PlainColumnView;

/**
 * @author Heiko Braun
 * @date 9/29/11
 */
public class ThreadChartView implements Sampler {

    private Sampler sampler;
    private String title;

    public ThreadChartView(String title) {
        this.title = title;
    }

    public Widget asWidget() {
        return displayStrategy();
    }

    private Widget displayStrategy() {

        Column[] threadCols = new Column[] {
                new NumberColumn("Live"),
                new NumberColumn("Daemon")
        };

        if(Console.visAPILoaded()) {
            sampler = new LineChartView(320,200, title)
                    .setColumns(threadCols);
        }
        else
        {
            sampler = new PlainColumnView(title)
                    .setColumns(threadCols);
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
