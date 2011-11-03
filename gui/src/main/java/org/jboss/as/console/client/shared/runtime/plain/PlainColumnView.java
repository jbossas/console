package org.jboss.as.console.client.shared.runtime.plain;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.runtime.Metric;
import org.jboss.as.console.client.shared.runtime.Sampler;
import org.jboss.as.console.client.shared.runtime.charts.Column;

/**
 * @author Heiko Braun
 * @date 10/25/11
 */
public class PlainColumnView implements Sampler {

    private Column[] columns = null;
    private Grid grid;
    private String title;

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

        layout.add(new HTML("<b>"+title+"</b>"));

        grid = new Grid(columns.length, 2);

        int row = 0;
        for(Column c : columns)
        {
            grid.setText(row, 0, c.getLabel() + ":");
            grid.setText(row, 1, "");
            row++;
        }

        layout.add(grid);
        return layout;
    }

    @Override
    public void addSample(Metric metric) {
        int row=0;

        for(Column c : columns)
        {
            grid.setText(row, 1, metric.get(row));
            row++;
        }

    }

    @Override
    public void clearSamples() {
        int row=0;

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
