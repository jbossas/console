package org.jboss.as.console.client.shared.jvm;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;
import com.google.gwt.visualization.client.visualizations.corechart.LineChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.jvm.charts.HeapChartView;
import org.jboss.as.console.client.shared.jvm.model.HeapMetric;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.tabs.FakeTabPanel;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

/**
 * @author Heiko Braun
 * @date 9/28/11
 */
public class VMMetricsView extends SuspendableViewImpl implements VMMetricsPresenter.MyView {

    private VMMetricsPresenter presenter;
    private Form<HeapMetric> heapForm;
    private Form<HeapMetric> nonHeapForm;

    private VerticalPanel vpanel;

    private HeapChartView heapChart;
    private Widget heapChartWidget;

    @Override
    public void setPresenter(VMMetricsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new LayoutPanel();

        FakeTabPanel titleBar = new FakeTabPanel("Virtual Machine");
        layout.add(titleBar);

        ToolStrip topLevelTools = new ToolStrip();
        topLevelTools.addToolButtonRight(
                new ToolButton(Console.CONSTANTS.common_label_refresh(),
                        new ClickHandler() {

                            @Override
                            public void onClick(ClickEvent event) {
                                presenter.loadVMStatus();
                            }
                        }));

        layout.add(topLevelTools);

        // ----

        vpanel = new VerticalPanel();
        vpanel.setStyleName("rhs-content-panel");

        ScrollPanel scroll = new ScrollPanel(vpanel);
        layout.add(scroll);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 28, Style.Unit.PX);
        layout.setWidgetTopHeight(topLevelTools, 28, Style.Unit.PX, 30, Style.Unit.PX);
        layout.setWidgetTopHeight(scroll, 58, Style.Unit.PX, 100, Style.Unit.PCT);

        // ------------------------

        heapForm = createHeapForm();
        nonHeapForm = createHeapForm();

        vpanel.add(new ContentGroupLabel("Heap"));
        vpanel.add(heapForm.asWidget());

        vpanel.add(new ContentGroupLabel("Non Heap"));
        vpanel.add(nonHeapForm.asWidget());

        return layout;
    }

    @Override
    public void attachCharts() {
        heapChart = new HeapChartView() ;
        heapChartWidget = heapChart.asWidget();
        vpanel.add(heapChartWidget);
    }

    @Override
    public void detachCharts() {
        if(heapChartWidget!=null)
            vpanel.remove(heapChartWidget);
    }


    private Form<HeapMetric> createHeapForm() {
        Form<HeapMetric> heapForm = new Form<HeapMetric>(HeapMetric.class);

        NumberBoxItem initItem = new NumberBoxItem("init", "Init");
        NumberBoxItem usedItem = new NumberBoxItem("used", "Used");
        NumberBoxItem committedItem = new NumberBoxItem("committed", "Committed");
        NumberBoxItem maxItem = new NumberBoxItem("max", "Max");

        heapForm.setFields(initItem, usedItem, committedItem, maxItem);

        heapForm.setEnabled(false);
        heapForm.setNumColumns(2);
        return heapForm;
    }

    @Override
    public void setHeap(HeapMetric heap) {

        if(heapChart!=null)
            heapChart.addSample(heap);
        heapForm.edit(heap);
    }

    @Override
    public void setNonHeap(HeapMetric nonHeap) {
        nonHeapForm.edit(nonHeap);
    }
}
