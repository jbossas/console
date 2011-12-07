package org.jboss.as.console.client.shared.runtime.tx;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.runtime.Metric;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

/**
 * @author Heiko Braun
 * @date 11/3/11
 */
public class TXMetricViewImpl extends SuspendableViewImpl implements TXMetricPresenter.MyView {

    private TXMetricManagement presenter;
    private TXExecutionView executionMetric;
    private TXRollbackView rollbackMetric;

    @Override
    public void setPresenter(TXMetricManagement presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {


        final ToolStrip toolStrip = new ToolStrip();
        ToolButton refreshBtn = new ToolButton(Console.CONSTANTS.common_label_refresh(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.refresh();
            }
        });
        refreshBtn.ensureDebugId(Console.CONSTANTS.debug_label_refresh_tXMetricViewImp());
        toolStrip.addToolButton(refreshBtn);

        this.executionMetric = new TXExecutionView();
        this.rollbackMetric = new TXRollbackView();

        SimpleLayout layout = new SimpleLayout()
                .setTitle("Transactions")
                .setHeadline("Transaction Metrics")
                .setDescription("These metrics reflect the current state of the transaction subsystem. If no metrics are shown, you might need to enable statistics for the transaction subsystem.")
                .setTopLevelTools(toolStrip.asWidget())
                .addContent("Executions", executionMetric.asWidget())
                .addContent("Rollbacks", rollbackMetric.asWidget());


        return layout.build();
    }

    @Override
    public void clearSamples() {
        this.executionMetric.clearSamples();
        this.rollbackMetric.clearSamples();
    }

    @Override
    public void setTxMetric(Metric txMetric) {
        this.executionMetric.addSample(txMetric);
    }

    @Override
    public void setRollbackMetric(Metric rollbackMetric) {
        this.rollbackMetric.addSample(rollbackMetric);
    }

}
