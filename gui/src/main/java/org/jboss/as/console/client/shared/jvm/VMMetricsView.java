package org.jboss.as.console.client.shared.jvm;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.jvm.charts.HeapChartView;
import org.jboss.as.console.client.shared.jvm.charts.ThreadChartView;
import org.jboss.as.console.client.shared.jvm.model.HeapMetric;
import org.jboss.as.console.client.shared.jvm.model.OSMetric;
import org.jboss.as.console.client.shared.jvm.model.RuntimeMetric;
import org.jboss.as.console.client.shared.jvm.model.ThreadMetric;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.forms.ComboBox;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.tabs.FakeTabPanel;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 9/28/11
 */
public class VMMetricsView extends SuspendableViewImpl implements VMMetricsPresenter.MyView {

    private VMMetricsManagement presenter;

    private HorizontalPanel heapPanel;
    private HorizontalPanel threadPanel;
    private VerticalPanel osPanel;

    private HeapChartView heapChart;
    private HeapChartView nonHeapChart;
    private ThreadChartView threadChart;

    private ContentHeaderLabel vmName;

    private HTML osName;
    private HTML processors;
    private ToolButton pauseBtn;

    private ComboBox vmSelection;

    @Override
    public void setPresenter(VMMetricsManagement presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new LayoutPanel();

        FakeTabPanel titleBar = new FakeTabPanel("Virtual Machine");
        layout.add(titleBar);

        ToolStrip topLevelTools = new ToolStrip();

        pauseBtn = new ToolButton("Stop");
        ClickHandler clickHandler = new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {

                boolean b = pauseBtn.getText().equals("Start");
                presenter.keepPolling(b);

                if(pauseBtn.getText().equals("Stop"))
                    pauseBtn.setText("Start");
                else
                    pauseBtn.setText("Stop");

            }
        };

        pauseBtn.addClickHandler(clickHandler);
        topLevelTools.addToolButtonRight(pauseBtn);

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

        HorizontalPanel header = new HorizontalPanel();
        header.setStyleName("fill-layout-width");

        vmName = new ContentHeaderLabel("");
        header.add(vmName);

        vmSelection = new ComboBox();
        vmSelection.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                presenter.onVMSelection(event.getValue());
            }
        });

        header.add(vmSelection.asWidget());

        vpanel.add(header);

        // -------------------------


        vpanel.add(new ContentGroupLabel("Threads"));

        threadPanel = new HorizontalPanel();
        threadPanel.setStyleName("fill-layout-width");

        osName = new HTML();
        processors = new HTML();

        osPanel = new VerticalPanel();
        osPanel.add(osName);
        osPanel.add(processors);
        osName.getElement().setAttribute("style", "padding-top:30px");

        vpanel.add(threadPanel);

        vpanel.add(new ContentGroupLabel("Heap"));

        heapPanel = new HorizontalPanel();
        heapPanel.setStyleName("fill-layout-width");
        vpanel.add(heapPanel);

        // --

        return layout;
    }

    @Override
    public void attachCharts() {
        heapChart = new HeapChartView(320, 200, "Heap Usage") ;
        nonHeapChart = new HeapChartView(320, 200, "Non Heap Usage") ;

        heapPanel.add(heapChart.asWidget());
        heapPanel.add(nonHeapChart.asWidget());

        // --

        threadChart = new ThreadChartView(320, 200, "Thread Usage");
        threadPanel.add(threadChart.asWidget());
        threadPanel.add(osPanel);
    }

    @Override
    public void detachCharts() {
        if(heapChart!=null) {
            heapPanel.clear();
            threadPanel.clear();
        }

        this.heapChart=null;
        this.nonHeapChart=null;
        this.threadChart=null;
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

    @Override
    public void setThreads(ThreadMetric metric) {

        if(threadChart!=null)
            threadChart.addSample(metric);
    }

    @Override
    public void setRuntimeMetric(RuntimeMetric runtime) {
        vmName.setText(runtime.getVmName());
    }

    @Override
    public void setOSMetric(OSMetric osMetric) {

        if(threadChart!=null)
        {
            osName.setHTML("<b style='color:#A7ABB4'>Operating System:</b>   "+osMetric.getName()+" "+osMetric.getVersion());
            processors.setHTML("<b style='color:#A7ABB4'>Number of processors:</b>   "+osMetric.getNumProcessors());
        }

    }

    @Override
    public void reset() {
        pauseBtn.setText("Stop");

        if(heapChart!=null)
        {
            heapChart.clearSamples();
            nonHeapChart.clearSamples();
            threadChart.clearSamples();
        }
    }

    @Override
    public void setVMKeys(List<String> vmkeys) {
        vmSelection.setValues(vmkeys);
        vmSelection.setItemSelected(0, true);
    }
}
