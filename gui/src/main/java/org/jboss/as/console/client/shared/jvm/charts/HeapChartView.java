package org.jboss.as.console.client.shared.jvm.charts;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.LineChart;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.runtime.Metric;
import org.jboss.as.console.client.shared.runtime.Sampler;
import org.jboss.as.console.client.shared.runtime.charts.Column;
import org.jboss.as.console.client.shared.runtime.charts.LineChartView;
import org.jboss.as.console.client.shared.runtime.charts.NumberColumn;
import org.jboss.as.console.client.shared.runtime.plain.PlainColumnView;
import com.google.gwt.visualization.client.visualizations.corechart.Options;

import java.util.Date;

/**
 * @author Heiko Braun
 * @date 9/29/11
 */
public class HeapChartView implements Sampler {

    private Sampler sampler;
    private String title;

    public HeapChartView(String title) {
        this.title = title;
    }

    public Widget asWidget() {
        return displayStrategy();
    }

    private Widget displayStrategy() {

        Column[] heapCols = new Column[] {
                new NumberColumn("Used")
        };

        if(Console.visAPILoaded()) {
            sampler = new NormalizedLineChartView(320,200, title)
                    .setColumns(heapCols);
        }
        else
        {
            sampler = new PlainColumnView(title)
                    .setColumns(heapCols);
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


    class NormalizedLineChartView extends LineChartView {
        NormalizedLineChartView(int width, int height, String title) {
            super(width, height, title);
        }

        @Override
        public void addSample(Metric metric) {
            long used = Long.valueOf(metric.get(0));
            long max = Long.valueOf(metric.get(1));

            long usedMb = ( used/1024)/1024;
            long maxMb = (max/1024)/1024;

            if(chart==null)
            {
                chart = new LineChart(createTable(), createOptions()) ;
                chart.setTitle(title);
                layout.add(chart);
            }

            data.addRow();
            int nextRow = data.getNumberOfRows()-1;

            // default
            data.setValue(nextRow, 0, new Date(System.currentTimeMillis()));

            data.setValue(nextRow, 1, usedMb);

            Options options = createOptions();
            AxisOptions vaxis = AxisOptions.create();
            vaxis.setMaxValue(maxMb);
            options.setVAxisOptions(vaxis);

            AxisOptions haxis = AxisOptions.create();
            haxis.set("showTextEvery", "25.00");
            haxis.set("maxAlternation", "1");
            options.setHAxisOptions(haxis);

            chart.draw(data, options);
        }
    }

   /*

    public void addSample(HeapMetric heap) {


        long usedMb = (heap.getUsed()/1024)/1024;
        long maxMb = (heap.getMax()/1024)/1024;

        maxLabel.setHTML("Max: " + maxMb + " mb");
        usedLabel.setHTML("Used: "+usedMb+" mb");

        data.addRow();
        int nextRow = data.getNumberOfRows()-1;

        data.setValue(nextRow, 0, new Date(System.currentTimeMillis()));
        data.setValue(nextRow, 1, usedMb);

        Options options = createOptions();
        AxisOptions vaxis = AxisOptions.create();
        vaxis.setMaxValue(maxMb);
        options.setVAxisOptions(vaxis);

        AxisOptions haxis = AxisOptions.create();
        haxis.set("showTextEvery", "10.00");
        haxis.set("maxAlternation", "1");
        options.setHAxisOptions(haxis);

        chart.draw(data, options);
    }

     */


}
