package org.jboss.as.console.client.shared.runtime.charts;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;
import com.google.gwt.visualization.client.visualizations.corechart.LineChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import org.jboss.as.console.client.shared.runtime.Metric;
import org.jboss.as.console.client.shared.runtime.Sampler;

import java.util.Date;

/**
 * Needs to be {@link #recycle()}ed after is was hidden.
 * Typically this happens in Presenter.onReset()
 *
 * @author Heiko Braun
 * @date 10/25/11
 */
public class LineChartView extends AbstractChartView implements Sampler {

    protected DataTable data;
    protected LineChart chart;

    protected VerticalPanel layout;
    protected  Column[] columns = null;

    private boolean timelineSeries = true;

    public LineChartView(int width, int height, String title) {
        super(width, height, title);
    }

    public LineChartView setColumns(Column... columns) {
        this.columns = columns;
        return this;
    }

    public LineChartView setTimelineSeries(boolean timelineSeries) {
        this.timelineSeries = timelineSeries;
        return this;
    }

    public Widget asWidget() {
        layout = new VerticalPanel();

        // chart
        chart = new LineChart(createTable(), createOptions()) ;
        layout.add(chart);

        return layout;

    }

    protected DataTable createTable() {

        if(null==columns)
            throw new RuntimeException("Columns not specified");

        data = DataTable.create();

        // default
        data.addColumn(AbstractDataTable.ColumnType.DATE, "Time");

        for(Column c : columns)
        {
            data.addColumn(c.getType(), c.getLabel());
        }

        return data;
    }

    protected Options createOptions() {
        Options options = Options.create();
        options.setWidth(width);
        options.setHeight(height);
        options.setTitle(title);
        options.setType(CoreChart.Type.LINE);

        return options;
    }

    public void addSample(Metric metric) {

        if(chart==null)
            throw new RuntimeException("chart is null. did you forget to #recycle() ?");

        if(data.getNumberOfRows()==0 || timelineSeries)
            data.addRow();

        int nextRow = data.getNumberOfRows()-1;

        // default
        data.setValue(nextRow, 0, new Date(System.currentTimeMillis()));


        DataTableAdapter adapter = new DataTableAdapter(data);

        for(int i=0; i<metric.getValues().size(); i++)
        {
            adapter.setValue(nextRow, i+1, columns[i].cast(metric.get(i)));
        }

        Options options = createOptions();

        AxisOptions haxis = AxisOptions.create();
        haxis.set("showTextEvery", "25.00");
        haxis.set("maxAlternation", "1");
        options.setHAxisOptions(haxis);

        chart.draw(data, options);
    }

    public void clearSamples()
    {
        if(chart!=null)
        {
            data = createTable();
            chart.draw(data, createOptions());
        }
    }

    public long numSamples() {
        return data.getNumberOfRows();
    }

    @Override
    public void recycle() {
        if(chart!=null)
        {
            layout.clear();
            chart=null;

            chart = new LineChart(createTable(), createOptions()) ;
            chart.setTitle(title);
            layout.add(chart);
        }
    }
}