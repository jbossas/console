package org.jboss.as.console.client.shared.jvm.charts;

import com.google.gwt.user.client.ui.*;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;
import com.google.gwt.visualization.client.visualizations.corechart.LineChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import org.jboss.as.console.client.shared.jvm.model.HeapMetric;


/**
 * @author Heiko Braun
 * @date 9/29/11
 */
public class HeapChartView extends AbstractChartView {

    private DataTable data;
    private LineChart chart;

    private HTML usedLabel;
    private HTML maxLabel;

    public HeapChartView(String title) {
        super(title);
    }

    public HeapChartView(int width, int height, String title) {
        super(width, height, title);
    }

    public Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();

        // chart
        chart = new LineChart(createTable(), createOptions()) ;
        layout.add(chart);

        // labels

        maxLabel = new HTML();
        usedLabel = new HTML();
        usedLabel.getElement().setAttribute("style", "padding-right:5px");

        HorizontalPanel labels = new HorizontalPanel();
        labels.add(usedLabel);
        labels.add(maxLabel);

        layout.add(labels);
        labels.getElement().getParentElement().setAttribute("align", "center");
        return layout;

    }

    private DataTable createTable() {
        data = DataTable.create();
        data.addColumn(AbstractDataTable.ColumnType.NUMBER, "Used");
        return data;
    }

    private Options createOptions() {
        Options options = Options.create();
        options.setWidth(width);
        options.setHeight(height);
        options.setTitle(title +" (mb)");
        options.setType(CoreChart.Type.LINE);
        return options;
    }

    public void addSample(HeapMetric heap) {


        long usedMb = (heap.getUsed()/1024)/1024;
        long maxMb = (heap.getMax()/1024)/1024;

        maxLabel.setHTML("Max: " + maxMb + " mb");
        usedLabel.setHTML("Used: "+usedMb+" mb");

        data.addRow();
        int nextRow = data.getNumberOfRows()-1;

        data.setValue(nextRow, 0, usedMb);

        Options options = createOptions();
        AxisOptions vaxis = AxisOptions.create();
        vaxis.setMaxValue(maxMb);
        options.setVAxisOptions(vaxis);

        chart.draw(data, options);
    }

    public void clearSamples()
    {
        data = createTable();
        chart.draw(data);
    }

    public long numSamples() {
        return data.getNumberOfRows();
    }


}
