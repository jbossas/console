package org.jboss.as.console.client.standalone.runtime;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.domain.hosts.ServerPicker;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.shared.jvm.model.OSMetric;
import org.jboss.as.console.client.shared.jvm.model.RuntimeMetric;
import org.jboss.as.console.client.shared.runtime.Metric;
import org.jboss.as.console.client.shared.runtime.vm.HeapChartView;
import org.jboss.as.console.client.shared.runtime.vm.ThreadChartView;
import org.jboss.as.console.client.shared.runtime.vm.VMMetricsManagement;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
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
    private ServerPicker serverPicker;

    protected boolean hasServerPicker = false;

    @Override
    public void setPresenter(VMMetricsManagement presenter) {
        this.presenter = presenter;
    }


    @Override
    public void recycle() {
        heapChart.recycle();
        nonHeapChart.recycle();
        threadChart.recycle();
    }

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new LayoutPanel();

        FakeTabPanel titleBar = new FakeTabPanel("Virtual Machine Status");
        layout.add(titleBar);

        ToolStrip topLevelTools = new ToolStrip();

        serverPicker = new ServerPicker(new ServerPicker.SelectionHandler() {
            @Override
            public void onSelection(ServerInstance server) {
                presenter.onServerSelection(server.getName());
            }
        });

        pauseBtn = new ToolButton("Stop Monitor");
        ClickHandler clickHandler = new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {

                boolean b = pauseBtn.getText().equals("Start Monitor");
                presenter.keepPolling(b);

                if(pauseBtn.getText().equals("Stop Monitor"))
                    pauseBtn.setText("Start Monitor");
                else
                    pauseBtn.setText("Stop Monitor");

            }
        };

        pauseBtn.addClickHandler(clickHandler);
        topLevelTools.addToolButton(pauseBtn);

        Widget serverPickerWidget = serverPicker.asWidget();
        serverPickerWidget.getElement().setAttribute("style", "width:200px;padding-right:5px;");

        if(hasServerPicker)
            topLevelTools.addToolWidgetRight(serverPickerWidget);


        // -------

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

        vmName = new ContentHeaderLabel("");
        vpanel.add(vmName);

        // -------------------------

        threadPanel = new HorizontalPanel();
        threadPanel.setStyleName("fill-layout-width");

        osName = new HTML();
        processors = new HTML();

        osPanel = new VerticalPanel();
        osPanel.add(osName);
        osPanel.add(processors);
        osName.getElement().setAttribute("style", "padding-top:30px");

        vpanel.add(threadPanel);

        heapPanel = new HorizontalPanel();
        heapPanel.setStyleName("fill-layout-width");
        vpanel.add(heapPanel);

        // --

        heapChart = new HeapChartView("Heap Usage") ;
        nonHeapChart = new HeapChartView("Non Heap Usage") ;

        heapPanel.add(heapChart.asWidget());
        heapPanel.add(nonHeapChart.asWidget());

        // --

        threadChart = new ThreadChartView("Thread Usage");
        threadPanel.add(threadChart.asWidget());
        threadPanel.add(osPanel);

        return layout;
    }

    @Override
    public void setHeap(Metric heap) {

        if(heapChart!=null)
        {
            heapChart.addSample(heap);
        }
        //heapForm.edit(heap);
    }

    @Override
    public void setNonHeap(Metric nonHeap) {

        if(nonHeapChart!=null)
        {
            nonHeapChart.addSample(nonHeap);
        }
        //nonHeapForm.edit(nonHeap);
    }

    @Override
    public void setThreads(Metric metric) {

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
        pauseBtn.setText("Stop Monitor");

        if(heapChart!=null)
        {
            heapChart.clearSamples();
            nonHeapChart.clearSamples();
            threadChart.clearSamples();
        }
    }

    @Override
    public void setServer(List<ServerInstance> servers) {

        serverPicker.setServers(servers);
    }
}
