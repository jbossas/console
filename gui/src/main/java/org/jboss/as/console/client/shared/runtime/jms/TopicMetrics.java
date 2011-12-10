package org.jboss.as.console.client.shared.runtime.jms;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.help.HelpSystem;
import org.jboss.as.console.client.shared.runtime.RuntimeBaseAddress;
import org.jboss.as.console.client.shared.runtime.Sampler;
import org.jboss.as.console.client.shared.runtime.charts.Column;
import org.jboss.as.console.client.shared.runtime.charts.NumberColumn;
import org.jboss.as.console.client.shared.runtime.plain.PlainColumnView;
import org.jboss.as.console.client.shared.subsys.messaging.model.JMSEndpoint;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.dmr.client.ModelDescriptionConstants;
import org.jboss.dmr.client.ModelNode;

/**
 * @author Heiko Braun
 * @date 12/10/11
 */
public class TopicMetrics {

    
    private JMSMetricPresenter presenter;
    private CellTable<JMSEndpoint> topicTable;
    private Sampler sampler;

    public TopicMetrics(JMSMetricPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
        final ToolStrip toolStrip = new ToolStrip();
        toolStrip.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_refresh(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                //presenter.setSelectedConnector(getCurrentSelection());
            }
        }));

        // ----

        topicTable = new DefaultCellTable<JMSEndpoint>(10);
        topicTable.setSelectionModel(new SingleSelectionModel<JMSEndpoint>());

        com.google.gwt.user.cellview.client.Column<JMSEndpoint, String> nameColumn = new com.google.gwt.user.cellview.client.Column<JMSEndpoint, String>(new TextCell()) {
            @Override
            public String getValue(JMSEndpoint object) {
                return object.getName();
            }
        };


        com.google.gwt.user.cellview.client.Column<JMSEndpoint, String> protocolColumn = new com.google.gwt.user.cellview.client.Column<JMSEndpoint, String>(new TextCell()) {
            @Override
            public String getValue(JMSEndpoint object) {
                return object.getJndiName();
            }
        };

        topicTable.addColumn(nameColumn, "Name");
        topicTable.addColumn(protocolColumn, "JNDI");

        topicTable.getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler(){
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                JMSEndpoint topic = getCurrentSelection();
                presenter.setSelectedTopic(topic);

            }
        });
        topicTable.getElement().setAttribute("style", "margin-top:15px;margin-bottom:15px;");

        // ----


        NumberColumn messageCount = new NumberColumn("message-count","Message Count");

        Column[] cols = new Column[] {
                messageCount,
                new NumberColumn("messages-added","Messages Added"),
                new NumberColumn("delivering-count","Delivered Messages"),
                new NumberColumn("subscription-count", "Subscription Count")
        };

        // TODO: Add remaining fields

        String title = "Request per Connector";

        final HelpSystem.AddressCallback addressCallback = new HelpSystem.AddressCallback() {
            @Override
            public ModelNode getAddress() {
                ModelNode address = new ModelNode();
                address.get(ModelDescriptionConstants.ADDRESS).set(RuntimeBaseAddress.get());
                address.get(ModelDescriptionConstants.ADDRESS).add("subsystem", "messaging");
                address.get(ModelDescriptionConstants.ADDRESS).add("hornetq-server", "default");
                address.get(ModelDescriptionConstants.ADDRESS).add("jms-topic", "*");
                return address;
            }
        };

        sampler = new PlainColumnView(title, addressCallback)
                .setColumns(cols)
                .setWidth(100, Style.Unit.PCT);


        // ----

        SimpleLayout layout = new SimpleLayout()
                .setTitle("Topics")
                .setPlain(true)
                .setTopLevelTools(toolStrip.asWidget())
                .setHeadline("JMS Topic Metrics")
                .setDescription("Metrics for JMS topics.")
                .addContent("Topic Selection", topicTable)
                .addContent("Topic Metrics", sampler.asWidget());

        return layout.build();
    }

    private JMSEndpoint getCurrentSelection() {
        return ((SingleSelectionModel<JMSEndpoint>) topicTable.getSelectionModel()).getSelectedObject();
    }

    public void clearSamples() {
        sampler.clearSamples();
    }
}
