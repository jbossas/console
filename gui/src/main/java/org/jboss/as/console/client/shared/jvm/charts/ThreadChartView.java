package org.jboss.as.console.client.shared.jvm.charts;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;
import com.google.gwt.visualization.client.visualizations.corechart.LineChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import org.jboss.as.console.client.shared.jvm.model.ThreadMetric;


/**
 * @author Heiko Braun
 * @date 9/29/11
 */
public class ThreadChartView extends AbstractChartView {

    private DataTable data;
    private LineChart chart;

    private HTML live;
    private HTML daemon;
    private HTML peak;

    public ThreadChartView(String title) {
        super(title);
    }

    public ThreadChartView(int width, int height, String title) {
        super(width, height, title);
    }

    @Override
    public Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();

        // chart
        chart = new LineChart(createTable(), createOptions()) ;
        layout.add(chart);

        // labels

        live = new HTML();
        daemon = new HTML();
        peak = new HTML();
        live.getElement().setAttribute("style", "padding-right:5px");
        daemon.getElement().setAttribute("style", "padding-right:5px");

        HorizontalPanel labels = new HorizontalPanel();
        labels.add(live);
        labels.add(daemon);
        labels.add(peak);

        layout.add(labels);
        labels.getElement().getParentElement().setAttribute("align", "center");
        return layout;

    }

    private DataTable createTable() {
        data = DataTable.create();
        data.addColumn(AbstractDataTable.ColumnType.NUMBER, "Live");
        data.addColumn(AbstractDataTable.ColumnType.NUMBER, "Daemon");
        return data;
    }

    private Options createOptions() {
        Options options = Options.create();
        options.setWidth(width);
        options.setHeight(height);
        options.setTitle(title);
        options.setType(CoreChart.Type.LINE);
        return options;
    }

    public void addSample(ThreadMetric metric) {


        live.setHTML("Live: "+metric.getCount());
        daemon.setHTML("Daemon: "+metric.getDaemonCount());
        peak.setHTML("Peak: "+metric.getPeakCount());

        data.addRow();
        int nextRow = data.getNumberOfRows()-1;

        data.setValue(nextRow, 0, metric.getCount());
        data.setValue(nextRow, 1, metric.getDaemonCount());

        Options options = createOptions();
        AxisOptions vaxis = AxisOptions.create();
        vaxis.setMaxValue(metric.getPeakCount()+10);
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

