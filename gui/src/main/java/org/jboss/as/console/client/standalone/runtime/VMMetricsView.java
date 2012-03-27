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
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.jvm.model.OSMetric;
import org.jboss.as.console.client.shared.jvm.model.RuntimeMetric;
import org.jboss.as.console.client.shared.runtime.Metric;
import org.jboss.as.console.client.shared.runtime.vm.HeapChartView;
import org.jboss.as.console.client.shared.runtime.vm.ThreadChartView;
import org.jboss.as.console.client.shared.runtime.vm.VMMetricsManagement;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.tabs.FakeTabPanel;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

/**
 * @author Heiko Braun
 * @date 9/28/11
 */
public class VMMetricsView extends SuspendableViewImpl implements VMMetricsPresenter.MyView {

    private VMMetricsManagement presenter;

    private VerticalPanel osPanel;

    private HeapChartView heapChart;
    private HeapChartView nonHeapChart;
    private ThreadChartView threadChart;

    private ContentHeaderLabel vmName;

    private HTML osName;
    private HTML processors;
    private ToolButton pauseBtn;

    protected boolean hasServerPicker = false;


    @Override
    public void setPresenter(VMMetricsManagement presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new LayoutPanel();

        FakeTabPanel titleBar = new FakeTabPanel("Virtual Machine Status");
        layout.add(titleBar);

        ToolStrip topLevelTools = new ToolStrip();

        /*pauseBtn = new ToolButton("Stop Monitor");

        //TODO - change all hardcoded text into localized properties
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
        topLevelTools.addToolButton(pauseBtn);      */
        topLevelTools.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_refresh(),
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        presenter.refresh();
                    }
                }));


        // -------

        layout.add(topLevelTools);

        // ----

        VerticalPanel vpanel = new VerticalPanel();
        vpanel.setStyleName("rhs-content-panel");

        ScrollPanel scroll = new ScrollPanel(vpanel);
        layout.add(scroll);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 40, Style.Unit.PX);
        layout.setWidgetTopHeight(topLevelTools, 40, Style.Unit.PX, 30, Style.Unit.PX);
        layout.setWidgetTopHeight(scroll, 70, Style.Unit.PX, 100, Style.Unit.PCT);

        // ------------------------

        HorizontalPanel header = new HorizontalPanel();
        header.setStyleName("fill-layout-width");
        vmName = new ContentHeaderLabel("");
        header.add(vmName);

        // -------------------------

        osName = new HTML();
        processors = new HTML();

        osPanel = new VerticalPanel();
        osPanel.add(osName);
        osPanel.add(processors);

        // cross references
        /* HTML jvmConfigLink = new HTML("<a href='javascript:void(0)'>Configure Virtual Machine &rarr;</a>");
  jvmConfigLink.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
          Console.getPlaceManager().revealPlace(
                  new PlaceRequest(NameTokens.HostJVMPresenter)
          );
      }
  });
  if(hasServerPicker)
      osPanel.add(jvmConfigLink);  */

        header.add(osPanel);

        // 50/50
        osPanel.getElement().getParentElement().setAttribute("style", "width:50%; vertical-align:top;padding-right:15px;");
        osPanel.getElement().getParentElement().setAttribute("align", "right");
        vmName.getElement().getParentElement().setAttribute("style", "width:50%; vertical-align:top");

        vpanel.add(header);


        // --

        heapChart = new HeapChartView("Heap Usage (mb)") ;
        nonHeapChart = new HeapChartView("Non Heap Usage (mb)", false) ;

        vpanel.add(heapChart.asWidget());
        vpanel.add(nonHeapChart.asWidget());

        // --

        threadChart = new ThreadChartView("Thread Usage");
        vpanel.add(threadChart.asWidget());
        //threadPanel.add(osPanel);

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
            processors.setHTML("<b style='color:#A7ABB4'>Processors:</b>   "+osMetric.getNumProcessors());
        }

    }

    @Override
    public void clearSamples() {
        heapChart.clearSamples();
        nonHeapChart.clearSamples();
        threadChart.clearSamples();
    }

}
