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
public class HeapChartView {

    private DataTable heapData;
    private LineChart heapChart;

    private HTML usedLabel;
    private HTML maxLabel;

    private int width = 400;
    private int height = 240;

    private String title;

    public HeapChartView(String title) {
        this.title = title;
    }

    public HeapChartView(String title, int width, int height) {
        this.width = width;
        this.height = height;
        this.title = title;
    }

    public Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();

        // chart
        heapChart = new LineChart(createTable(), createOptions()) ;
        layout.add(heapChart);

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
        heapData = DataTable.create();
        heapData.addColumn(AbstractDataTable.ColumnType.NUMBER, "Used");
        return heapData;
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

        maxLabel.setHTML("<b>Max</b>: "+maxMb+" mb");
        usedLabel.setHTML("<b>Used</b>: "+usedMb+" mb");

        heapData.addRow();
        int nextRow = heapData.getNumberOfRows()-1;

        heapData.setValue(nextRow, 0, usedMb);

        Options options = createOptions();
        AxisOptions vaxis = AxisOptions.create();
        vaxis.setMaxValue(maxMb);
        options.setVAxisOptions(vaxis);

        heapChart.draw(heapData, options);
    }

    public void clearSamples()
    {
        heapData = createTable();
        heapChart.draw(heapData);
    }

    public long numSamples() {
        return heapData.getNumberOfRows();
    }


}
