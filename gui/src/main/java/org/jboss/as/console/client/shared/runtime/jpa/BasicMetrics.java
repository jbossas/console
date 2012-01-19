package org.jboss.as.console.client.shared.runtime.jpa;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.help.HelpSystem;
import org.jboss.as.console.client.shared.runtime.RuntimeBaseAddress;
import org.jboss.as.console.client.shared.runtime.charts.Column;
import org.jboss.as.console.client.shared.runtime.charts.NumberColumn;
import org.jboss.as.console.client.shared.runtime.jpa.model.JPADeployment;
import org.jboss.as.console.client.shared.runtime.plain.PlainColumnView;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.dmr.client.ModelDescriptionConstants;
import org.jboss.dmr.client.ModelNode;

/**
 * @author Heiko Braun
 * @date 1/19/12
 */
public class BasicMetrics {

    private PlainColumnView sampler;
    private JPAMetricPresenter presenter;
    private JPADeployment currentUnit;

    public BasicMetrics(JPAMetricPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {

        final ToolStrip toolStrip = new ToolStrip();
        toolStrip.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_refresh(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // TODO
            }
        }));


        NumberColumn requestCount = new NumberColumn("requestCount","Request Count");

        Column[] cols = new Column[] {
                requestCount.setBaseline(true),
                new NumberColumn("errorCount","Error Count").setComparisonColumn(requestCount),
                new NumberColumn("processingTime","Processing Time"),
                new NumberColumn("maxTime", "Max Time")
        };

        String title = "Overall Usage";

        final HelpSystem.AddressCallback addressCallback = new HelpSystem.AddressCallback() {
            @Override
            public ModelNode getAddress() {
                ModelNode address = new ModelNode();
                address.get(ModelDescriptionConstants.ADDRESS).set(RuntimeBaseAddress.get());
                address.get(ModelDescriptionConstants.ADDRESS).add("subsystem", "jpa");
                address.get(ModelDescriptionConstants.ADDRESS).add("hibernate-persistence-unit", "*");
                return address;
            }
        };

        sampler = new PlainColumnView(title, addressCallback)
                .setColumns(cols)
                .setWidth(100, Style.Unit.PCT);


        // ----

        SimpleLayout layout = new SimpleLayout()
                .setPlain(true)
                .setTopLevelTools(toolStrip.asWidget())
                .setHeadline("Persistence Unit Metrics")
                .setDescription("Metrics for a persistence unit.")
                .addContent("Basic Metrics", sampler.asWidget());

        return layout.build();
    }

    public void setUnit(JPADeployment unit) {
        this.currentUnit = unit;
    }
}
