/*
 * JBoss, Home of Professional Open Source
 * Copyright <YEAR> Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jboss.as.console.client.debug;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.widgets.ContentGroupLabel;
import org.jboss.as.console.client.widgets.RHSContentPanel;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/22/11
 */
public class InvocationMetricsView extends SuspendableViewImpl implements InvocationMetricsPresenter.MyView{
    private InvocationMetricsPresenter presenter;

    private CellTable<SimpleMetric> invocationTable;
    private double upperHalf;

    @Override
    public void setPresenter(InvocationMetricsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new RHSContentPanel("Invocation Metrics");

        invocationTable = new DefaultCellTable<SimpleMetric>(100);
        TextColumn<SimpleMetric> type = new TextColumn<SimpleMetric>() {
            @Override
            public String getValue(SimpleMetric metric) {
                return metric.getKey();
            }
        };

        TextColumn<SimpleMetric> count = new TextColumn<SimpleMetric>() {

            @Override
            public void render(Cell.Context context, SimpleMetric object, SafeHtmlBuilder sb) {


                int value = object.getValue().intValue();
                String color = value>upperHalf ? "red" : "black";

                // fallback when num records < 3
                if(upperHalf==0) color ="black";

                sb.appendHtmlConstant("<div style='text-align:right; color:"+color+"'>")
                        .append(value)
                        .appendHtmlConstant("</div>");
            }

            @Override
            public String getValue(SimpleMetric metric) {
                return String.valueOf(metric.getValue().intValue());
            }
        };

        invocationTable.addColumn(type, "Type");
        invocationTable.addColumn(count, "Count");

        layout.add(new ContentGroupLabel("Invocation Count"));
        layout.add(invocationTable);

        return layout;
    }

    @Override
    public void updateFrom(List<SimpleMetric> values) {

        List<Double> l = new ArrayList<Double>(values.size());
        for(SimpleMetric metric : values)
        {
            l.add(metric.getValue());
        }

        double[] quartiles = Stats.Quartiles(l);
        upperHalf = quartiles[1];

        invocationTable.setRowCount(values.size());
        invocationTable.setRowData(values);
    }
}
