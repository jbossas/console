package org.jboss.as.console.client.standalone.runtime;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.jvm.model.OSMetric;
import org.jboss.as.console.client.shared.jvm.model.RuntimeMetric;
import org.jboss.as.console.client.shared.runtime.Metric;
import org.jboss.as.console.client.shared.runtime.vm.HeapChartView;
import org.jboss.as.console.client.shared.runtime.vm.ThreadChartView;
import org.jboss.as.console.client.shared.runtime.vm.VMMetricsManagement;
import org.jboss.as.console.client.widgets.tables.TablePicker;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
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

    private TablePicker<String> vmSelection;
    private CellTable<String> vmTable;

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

        vmTable = new DefaultCellTable<String>(5);
        Column<String, String> nameCol = new Column<String, String>(new TextCell()) {
            @Override
            public String getValue(String object) {
                return object;
            }
        };
        vmTable.addColumn(nameCol, "Server Instance");

        vmSelection = new TablePicker(vmTable, new TablePicker.ValueRenderer<String>() {
            @Override
            public String render(String selection) {
                return selection;
            }
        });
        vmSelection.setPopupWidth(400);
        vmSelection.setDescription("Please select a server instance:");

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

        Widget vmWidget = vmSelection.asWidget();
        vmWidget.getElement().setAttribute("style", "width:200px;padding-right:5px;");
        topLevelTools.addToolWidgetRight(vmWidget);


        // -------

        vmTable.getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler(){
            @Override
            public void onSelectionChange(SelectionChangeEvent selectionChangeEvent) {
                String vm = ((SingleSelectionModel<String>) vmTable.getSelectionModel()).getSelectedObject();
                presenter.onVMSelection(vm);
            }
        });

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

        /*HorizontalPanel header = new HorizontalPanel();
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

        header.add(vmSelection.asWidget());*/

        vmName = new ContentHeaderLabel("");
        vpanel.add(vmName);

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
    public void setVMKeys(List<String> vmkeys) {
        vmTable.setRowCount(vmkeys.size(), true);
        vmTable.setRowData(vmkeys);

        if(!vmkeys.isEmpty())
            vmTable.getSelectionModel().setSelected(vmkeys.get(0), true);
    }
}
