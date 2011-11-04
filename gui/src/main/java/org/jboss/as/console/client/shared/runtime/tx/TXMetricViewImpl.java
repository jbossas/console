package org.jboss.as.console.client.shared.runtime.tx;

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
import org.jboss.as.console.client.shared.runtime.Metric;
import org.jboss.as.console.client.widgets.nav.ServerSwitch;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.tabs.FakeTabPanel;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/3/11
 */
public class TXMetricViewImpl extends SuspendableViewImpl implements TXMetricPresenter.MyView {

    private TXMetricManagement presenter;
    private TXExecutionView executionMetric;
    private TXRollbackView rollbackMetric;
    private ServerSwitch serverSwitch;
    protected boolean supportServers = false;

    @Override
    public void setPresenter(TXMetricManagement presenter) {
        this.presenter = presenter;
    }

    public void setSupportServers(boolean supportServers) {
        this.supportServers = supportServers;
    }

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new LayoutPanel();

        FakeTabPanel titleBar = new FakeTabPanel("Transactions");
        layout.add(titleBar);

        // ----

        final ToolStrip toolStrip = new ToolStrip();

        toolStrip.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_refresh(), new ClickHandler(){
            @Override
            public void onClick(ClickEvent event) {
                presenter.refresh();
            }
        }));


        layout.add(toolStrip);

        // ---

        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("rhs-content-panel");

        ScrollPanel scrollPanel = new ScrollPanel(panel);
        layout.add(scrollPanel);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 28, Style.Unit.PX);
        layout.setWidgetTopHeight(toolStrip, 28, Style.Unit.PX, 30, Style.Unit.PX);
        layout.setWidgetTopHeight(scrollPanel, 58, Style.Unit.PX, 100, Style.Unit.PCT);

        // --------------

        if(supportServers)
        {
            serverSwitch = new ServerSwitch();
            panel.add(serverSwitch.asWidget());
        }
        else
        {
            panel.add(new ContentHeaderLabel("Transaction Metrics"));
        }


        // --------------

        HorizontalPanel outcomePanel = new HorizontalPanel();
        outcomePanel .setStyleName("fill-layout-width");

        this.executionMetric = new TXExecutionView();
        outcomePanel .add(executionMetric.asWidget());

        this.rollbackMetric = new TXRollbackView();
        outcomePanel .add(rollbackMetric.asWidget());

        panel.add(new ContentGroupLabel("Transaction Outcome"));
        panel.add(outcomePanel);

        return layout;
    }

    @Override
    public void setTxMetric(Metric txMetric) {
        this.executionMetric.addSample(txMetric);
    }

    @Override
    public void setRollbackMetric(Metric rollbackMetric) {
        this.rollbackMetric.addSample(rollbackMetric);
    }

    @Override
    public void setServerNames(List<String> serverNames) {
        if(supportServers)
            serverSwitch.setServerNames(serverNames);
    }

    @Override
    public void recycleCharts() {
        executionMetric.recycle();
        rollbackMetric.recycle();
    }
}
