package org.jboss.as.console.client.shared.subsys.tx.charts;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.BarChart;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;
import com.google.gwt.visualization.client.visualizations.corechart.LineChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import org.jboss.as.console.client.shared.jvm.charts.AbstractChartView;
import org.jboss.as.console.client.shared.subsys.tx.TXRollbackSampler;
import org.jboss.as.console.client.shared.subsys.tx.model.RollbackMetric;

import java.util.Date;

/**
 * @author Heiko Braun
 * @date 10/25/11
 */
public class RollbackChartView extends AbstractChartView implements TXRollbackSampler {

    private DataTable data;
    private BarChart chart;

    private HTML appLabel;
    private HTML resourceLabel;
    private VerticalPanel layout;

    public RollbackChartView(String title) {
        super(title);
    }

    public RollbackChartView(int width, int height, String title) {
        super(width, height, title);
    }

    public Widget asWidget() {
        layout = new VerticalPanel();

        // chart
        chart = new BarChart(createTable(), createOptions()) ;
        layout.add(chart);

        // labels

        appLabel = new HTML();
        resourceLabel = new HTML();

        HorizontalPanel labels = new HorizontalPanel();
        labels.add(appLabel);
        labels.add(resourceLabel);

        layout.add(labels);
        labels.getElement().getParentElement().setAttribute("align", "center");
        return layout;

    }

    private DataTable createTable() {
        data = DataTable.create();
        data.addColumn(AbstractDataTable.ColumnType.DATETIME, "Time");
        data.addColumn(AbstractDataTable.ColumnType.NUMBER, "Applications");
        data.addColumn(AbstractDataTable.ColumnType.NUMBER, "Resources");
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

    public void addSample(RollbackMetric metric) {

        appLabel.setHTML("Applications: " + metric.getAppRollback());
        resourceLabel.setHTML("Resources: "+metric.getResourceRollback());

        if(data.getNumberOfRows()==0)
            data.addRow();
        int nextRow = data.getNumberOfRows()-1;

        data.setValue(nextRow, 0, new Date(System.currentTimeMillis()));
        data.setValue(nextRow, 1, metric.getAppRollback());
        data.setValue(nextRow, 2, metric.getResourceRollback());

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

    @Override
    public void recycle() {
        if(chart!=null)
        {
            layout.remove(chart);
            chart = new BarChart(createTable(), createOptions()) ;
            layout.add(chart);

        }
    }
}