package org.jboss.as.console.client.shared.jvm;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
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

    private HorizontalPanel heapPanel;

    private HeapChartView heapChart;
    private Widget heapChartWidget;

    private HeapChartView nonHeapChart;
    private Widget nonHeapChartWidget;

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

        VerticalPanel vpanel = new VerticalPanel();
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

        heapPanel = new HorizontalPanel();
        heapPanel.setStyleName("fill-layout-width");

        vpanel.add(heapPanel);

        return layout;
    }

    @Override
    public void attachCharts() {
        heapChart = new HeapChartView("Heap Usage", 320, 240) ;
        heapChartWidget = heapChart.asWidget();

        nonHeapChart = new HeapChartView("Non Heap Usage", 320, 240) ;
        nonHeapChartWidget = nonHeapChart.asWidget();

        heapPanel.add(heapChartWidget);
        heapPanel.add(nonHeapChartWidget);
    }

    @Override
    public void detachCharts() {
        if(heapChartWidget!=null) {
            heapPanel.remove(heapChartWidget);
            heapPanel.remove(nonHeapChartWidget);
        }


        this.heapChartWidget=null;
        this.heapChart=null;

        this.nonHeapChart=null;
        this.nonHeapChartWidget=null;
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
        {
            heapChart.addSample(heap);
        }
        //heapForm.edit(heap);
    }

    @Override
    public void setNonHeap(HeapMetric nonHeap) {

        if(nonHeapChart!=null)
        {
            nonHeapChart.addSample(nonHeap);
        }
        //nonHeapForm.edit(nonHeap);
    }
}
