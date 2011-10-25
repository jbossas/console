package org.jboss.as.console.client.shared.subsys.tx.charts;

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
import org.jboss.as.console.client.shared.jvm.charts.AbstractChartView;
import org.jboss.as.console.client.shared.subsys.tx.TXMetricSampler;
import org.jboss.as.console.client.shared.subsys.tx.model.TXMetric;

import java.util.Date;

/**
 * @author Heiko Braun
 * @date 10/25/11
 */
public class TXChartView extends AbstractChartView implements TXMetricSampler {

    private DataTable data;
    private LineChart chart;

    private HTML totalLabel;
    private HTML commitedLabel;
    private HTML abortedLabel;
    private HTML timedOutLabel;

    public TXChartView(String title) {
        super(title);
    }

    public TXChartView(int width, int height, String title) {
        super(width, height, title);
    }

    public Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();

        // chart
        chart = new LineChart(createTable(), createOptions()) ;
        layout.add(chart);

        // labels

        totalLabel  = new HTML();
        commitedLabel = new HTML();
        abortedLabel = new HTML();
        timedOutLabel= new HTML();


        HorizontalPanel labels = new HorizontalPanel();
        labels.add(totalLabel);
        labels.add(commitedLabel);
        labels.add(abortedLabel);
        labels.add(timedOutLabel);

        layout.add(labels);
        labels.getElement().getParentElement().setAttribute("align", "center");
        return layout;

    }

    private DataTable createTable() {
        data = DataTable.create();
        data.addColumn(AbstractDataTable.ColumnType.DATETIME, "Time");
        data.addColumn(AbstractDataTable.ColumnType.NUMBER, "Total");
        data.addColumn(AbstractDataTable.ColumnType.NUMBER, "Committed");
        data.addColumn(AbstractDataTable.ColumnType.NUMBER, "Aborted");
        data.addColumn(AbstractDataTable.ColumnType.NUMBER, "Timed Out");
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

    public void addSample(TXMetric metric) {

        totalLabel.setHTML("Total: " + metric.getTotal());
        commitedLabel.setHTML("Committed: "+metric.getCommitted());

        data.addRow();
        int nextRow = data.getNumberOfRows()-1;

        data.setValue(nextRow, 0, new Date(System.currentTimeMillis()));
        data.setValue(nextRow, 1, metric.getTotal());
        data.setValue(nextRow, 2, metric.getCommitted());
        data.setValue(nextRow, 3, metric.getAborted());
        data.setValue(nextRow, 4, metric.getTimedOut());

        Options options = createOptions();

        AxisOptions haxis = AxisOptions.create();
        haxis.set("showTextEvery", "10.00");
        haxis.set("maxAlternation", "1");
        options.setHAxisOptions(haxis);

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