package org.jboss.as.console.client.shared.runtime.jms;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.help.HelpSystem;
import org.jboss.as.console.client.shared.runtime.Metric;
import org.jboss.as.console.client.shared.runtime.RuntimeBaseAddress;
import org.jboss.as.console.client.shared.runtime.Sampler;
import org.jboss.as.console.client.shared.runtime.charts.Column;
import org.jboss.as.console.client.shared.runtime.charts.NumberColumn;
import org.jboss.as.console.client.shared.runtime.plain.PlainColumnView;
import org.jboss.as.console.client.shared.subsys.messaging.model.JMSEndpoint;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.dmr.client.ModelDescriptionConstants;
import org.jboss.dmr.client.ModelNode;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 12/10/11
 */
public class TopicMetrics {

    
    private JMSMetricPresenter presenter;
    private CellTable<JMSEndpoint> topicTable;
    private ListDataProvider<JMSEndpoint> dataProvider;
    private Sampler inflightSampler;
    private Sampler processedSampler;
    private Sampler subscriptionSampler;

    public TopicMetrics(JMSMetricPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
        final ToolStrip toolStrip = new ToolStrip();
        toolStrip.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_refresh(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.setSelectedTopic(getCurrentSelection());
            }
        }));

        // ----

        topicTable = new DefaultCellTable<JMSEndpoint>(5);
        topicTable.setSelectionModel(new SingleSelectionModel<JMSEndpoint>());

        dataProvider = new ListDataProvider<JMSEndpoint>();
        dataProvider.addDataDisplay(topicTable);

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
        topicTable.getElement().setAttribute("style", "margin-top:15px;margin-bottom:0px;");

        // ----

        NumberColumn inQueue = new NumberColumn("message-count", "Messages in Topic");
        Column[] cols = new Column[] {
                inQueue.setBaseline(true),
                new NumberColumn("delivering-count","In Delivery").setComparisonColumn(inQueue),
        };

        String title = "In-Flight Messages";

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

        inflightSampler = new PlainColumnView(title, addressCallback)
                .setColumns(cols)
                .setWidth(100, Style.Unit.PCT);


        // ----


        NumberColumn processedCol = new NumberColumn("messages-added", "Messages Added");
        Column[] cols2 = new Column[] {
                processedCol.setBaseline(true),
                new NumberColumn("durable-message-count","Number Durable Messages").setComparisonColumn(processedCol),
                new NumberColumn("non-durable-message-count","Number Non-Durable Messages").setComparisonColumn(processedCol)
        };

        String title2 = "Messages Processed";

        processedSampler = new PlainColumnView(title2, addressCallback)
                .setColumns(cols2)
                .setWidth(100, Style.Unit.PCT);


        // ----

        NumberColumn subscriptionsCols = new NumberColumn("subscription-count", "Number of Subscriptions");
        Column[] cols3 = new Column[] {
                subscriptionsCols.setBaseline(true),
                new NumberColumn("durable-subscription-count","Durable Subscribers").setComparisonColumn(subscriptionsCols),
                new NumberColumn("non-durable-subscription-count","Nun-Durable Subscribers").setComparisonColumn(subscriptionsCols)
        };

        String title3 = "Subscriptions";

        subscriptionSampler = new PlainColumnView(title3, addressCallback)
                .setColumns(cols3)
                .setWidth(100, Style.Unit.PCT);


        // ----

        DefaultPager pager = new DefaultPager();
        pager.setDisplay(topicTable);

        VerticalPanel tablePanel = new VerticalPanel();
        tablePanel.setStyleName("fill-layout-width");
        tablePanel.add(topicTable);
        tablePanel.add(pager);


        SimpleLayout layout = new SimpleLayout()
                .setTitle("Topics")
                .setPlain(true)
                .setTopLevelTools(toolStrip.asWidget())
                .setHeadline("JMS Topic Metrics")
                .setDescription("Metrics for JMS topics.")
                .addContent("Topic Selection", tablePanel)
                .addContent("In flight messages", inflightSampler.asWidget())
                .addContent("Message Ratio", processedSampler.asWidget())
                .addContent("Subscription Ratio", subscriptionSampler.asWidget());

        return layout.build();
    }

    private JMSEndpoint getCurrentSelection() {
        return ((SingleSelectionModel<JMSEndpoint>) topicTable.getSelectionModel()).getSelectedObject();
    }

    public void clearSamples() {
        inflightSampler.clearSamples();
        processedSampler.clearSamples();

    }

    public void setTopics(List<JMSEndpoint> topics) {
        dataProvider.setList(topics);

        if(!topics.isEmpty())
            topicTable.getSelectionModel().setSelected(topics.get(0), true);
    }

    public void setInflight(Metric topicInflight) {
        inflightSampler.addSample(topicInflight);
    }

    public void setProcessed(Metric topicProcessed) {
        processedSampler.addSample(topicProcessed);
    }

    public void setSubscriptions(Metric topicSubscriptions) {
        subscriptionSampler.addSample(topicSubscriptions);
    }
}
