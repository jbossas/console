package org.jboss.as.console.client.shared.runtime;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.subsys.tx.TXExecutionView;
import org.jboss.as.console.client.shared.subsys.tx.TXRollbackView;
import org.jboss.as.console.client.shared.subsys.tx.model.RollbackMetric;
import org.jboss.as.console.client.shared.subsys.tx.model.TXMetric;
import org.jboss.as.console.client.standalone.runtime.TXMetricPresenter;
import org.jboss.ballroom.client.widgets.tabs.FakeTabPanel;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

/**
 * @author Heiko Braun
 * @date 11/3/11
 */
public class TXMetricView extends SuspendableViewImpl implements TXMetricPresenter.MyView{

    private TXMetricPresenter presenter;
    private TXExecutionView executionMetric;
    private TXRollbackView rollbackMetric;


    @Override
    public void setPresenter(TXMetricPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new LayoutPanel();

        FakeTabPanel titleBar = new FakeTabPanel("Transaction Metrics");
        layout.add(titleBar);

        // ----

        final ToolStrip toolStrip = new ToolStrip();

        toolStrip.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_refresh(), new ClickHandler(){
            @Override
            public void onClick(ClickEvent event) {

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

        this.executionMetric = new TXExecutionView();
        panel.add(executionMetric.asWidget());

        this.rollbackMetric = new TXRollbackView();
        panel.add(rollbackMetric.asWidget());


        // sample data
        executionMetric.addSample(new TXMetric(55, 12, 33, 5));
        rollbackMetric.addSample(new RollbackMetric(77, 12));

        return layout;
    }
}
