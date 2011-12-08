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
import org.jboss.as.console.client.domain.hosts.ServerPicker;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.shared.runtime.Metric;
import org.jboss.as.console.client.widgets.ContentDescription;
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
    protected boolean supportServers = false;
    private ServerPicker serverPicker;

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

        toolStrip.addToolButton(new ToolButton(Console.CONSTANTS.common_label_refresh(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.refresh();
            }
        }));

        serverPicker = new ServerPicker(new ServerPicker.SelectionHandler() {
            @Override
            public void onSelection(ServerInstance server) {
                // TODO: handle selection
            }
        });

        if(supportServers)
            toolStrip.addToolWidgetRight(serverPicker.asWidget());

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

        panel.add(new ContentHeaderLabel("Transaction subsystem metrics"));
        panel.add(new ContentDescription("These metrics reflect the current state of the transaction subsystem."));

        this.executionMetric = new TXExecutionView();
        panel.add(executionMetric.asWidget());

        this.rollbackMetric = new TXRollbackView();
        panel.add(rollbackMetric.asWidget());


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
    public void setServer(List<ServerInstance> server) {
        serverPicker.setServers(server);
    }

    @Override
    public void recycle() {
        executionMetric.recycle();
        rollbackMetric.recycle();
    }

    @Override
    public void reset() {
        executionMetric.clearSamples();
        rollbackMetric.clearSamples();
    }
}
