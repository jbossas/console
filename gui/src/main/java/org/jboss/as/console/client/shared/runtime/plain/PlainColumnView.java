package org.jboss.as.console.client.shared.runtime.plain;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.runtime.Metric;
import org.jboss.as.console.client.shared.runtime.Sampler;
import org.jboss.as.console.client.shared.runtime.charts.Column;
import org.jboss.as.console.client.shared.runtime.charts.StackedBar;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 10/25/11
 */
public class PlainColumnView implements Sampler {

    private Column[] columns = null;
    private FlexTable grid;
    private String title;
    private int ROW_OFFSET = 2;

    List<StackedBar> stacks = new LinkedList<StackedBar>();

    public PlainColumnView(String title) {
        this.title = title;
    }

    public PlainColumnView setColumns(Column... columns) {
        this.columns = columns;
        return this;
    }

    @Override
    public Widget asWidget() {

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        grid = new FlexTable();
        grid.getElement().setAttribute("style", "width:400px;");

        grid.setHTML(0, 0, "<h3>"+title+"</h3>");
        grid.setHTML(1, 0, "Metric");
        grid.setHTML(1, 1, "Actual");

        // stacked bars: TODO these are optional
        grid.setHTML(1, 2, "");

        int row = ROW_OFFSET;
        for(Column c : columns)
        {
            grid.setHTML(row, 0, "<b style='color:#A7ABB4'>"+c.getLabel() + ":</b>");
            grid.setHTML(row, 1, "");

            stacks.add(new StackedBar());
            grid.setWidget(row, 2, stacks.get(row-ROW_OFFSET).asWidget());

            grid.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_RIGHT);

            if(!c.isVisible())
                grid.getRowFormatter().setVisible(row, false);

            row++;
        }

        grid.getFlexCellFormatter().setColSpan(0, 0, 2);
        grid.getRowFormatter().setStyleName(0, "metric-table-title");

        grid.getCellFormatter().setStyleName(1,0,"metric-table-header");
        grid.getCellFormatter().setStyleName(1,1,"metric-table-header");
        grid.getCellFormatter().setStyleName(1,2,"metric-table-header");

        layout.add(grid);
        return layout;
    }

    @Override
    public void addSample(Metric metric) {
        int row=ROW_OFFSET;

        for(Column c : columns)
        {
            Long baseline = Long.valueOf(metric.get(0));
            int dataIndex = row - ROW_OFFSET;

            grid.setText(row, 1, metric.get(dataIndex) +" / "+ baseline);

            if(c.getComparisonColumn()!=null)
            {
                Long actualValue = Long.valueOf(metric.get(dataIndex));
                stacks.get(dataIndex).setRatio(baseline,actualValue);
            }
            row++;
        }

    }

    @Override
    public void clearSamples() {
        int row=ROW_OFFSET;

        for(Column c : columns)
        {
            grid.setText(row, 1, "");
            row++;
        }
    }

    @Override
    public long numSamples() {
        return 1;
    }

    @Override
    public void recycle() {

    }
}
