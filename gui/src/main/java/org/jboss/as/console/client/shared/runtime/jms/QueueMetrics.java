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
import org.jboss.as.console.client.shared.subsys.messaging.model.Queue;
import org.jboss.as.console.client.shared.viewframework.builder.OneToOneLayout;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.dmr.client.ModelDescriptionConstants;
import org.jboss.dmr.client.ModelNode;

import java.util.Iterator;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 12/10/11
 */
public class QueueMetrics {


    private JMSMetricPresenter presenter;
    private CellTable<Queue> queueTable;
    private ListDataProvider<Queue> dataProvider;
    private Sampler sampler;
    private Sampler messageSampler;
    private Sampler consumerSampler;

    public QueueMetrics(JMSMetricPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
        final ToolStrip toolStrip = new ToolStrip();
        toolStrip.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_refresh(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.setSelectedQueue(getCurrentSelection());
            }
        }));

        // ----

        queueTable = new DefaultCellTable<Queue>(5);
        queueTable.setSelectionModel(new SingleSelectionModel<Queue>());

        dataProvider = new ListDataProvider<Queue>();
        dataProvider.addDataDisplay(queueTable);

        com.google.gwt.user.cellview.client.Column<Queue, String> nameColumn = new com.google.gwt.user.cellview.client.Column<Queue, String>(new TextCell()) {
            @Override
            public String getValue(Queue object) {
                return object.getName();
            }
        };


        com.google.gwt.user.cellview.client.Column<Queue, String> protocolColumn = new com.google.gwt.user.cellview.client.Column<Queue, String>(new TextCell()) {
            @Override
            public String getValue(Queue object) {
                List<String> names = object.getEntries();
                StringBuilder builder = new StringBuilder();
                if (!names.isEmpty())
                {
                    Iterator<String> iterator = names.iterator();
                    builder.append("[").append(iterator.next());
                    if (iterator.hasNext())
                    {
                        builder.append(", ...");
                    }
                    builder.append("]");
                }
                return builder.toString();
            }
        };

        queueTable.addColumn(nameColumn, "Name");
        queueTable.addColumn(protocolColumn, "JNDI");

        queueTable.getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler(){
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                Queue queue = getCurrentSelection();
                presenter.setSelectedQueue(queue);

            }
        });

        // ----

        NumberColumn inQueue = new NumberColumn("message-count", "Messages in Queue");
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
                address.get(ModelDescriptionConstants.ADDRESS).add("jms-queue", "*");
                return address;
            }
        };

        sampler = new PlainColumnView(title, addressCallback)
                .setColumns(cols)
                .setWidth(100, Style.Unit.PCT);


        // ----

        Column[] cols2 = new Column[] {
                new NumberColumn("messages-added", "Messages Added"),
                new NumberColumn("scheduled-count","Messages Scheduled")
        };

        String title2 = "Messages Processed";

        messageSampler = new PlainColumnView(title2, addressCallback)
                .setColumns(cols2)
                .setWidth(100, Style.Unit.PCT);

        // ----


        NumberColumn consumerCol = new NumberColumn("consumer-count", "Number of Consumer");
        Column[] cols3 = new Column[] {
                consumerCol
        };

        String title3 = "Consumer";

        consumerSampler = new PlainColumnView(title3, addressCallback)
                .setColumns(cols3)
                .setWidth(100, Style.Unit.PCT);

        // ----


        DefaultPager pager = new DefaultPager();
        pager.setDisplay(queueTable);

        ToolStrip queueTools = new ToolStrip();
        queueTools.addToolButtonRight(new ToolButton("Flush", new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                SingleSelectionModel<Queue> selectionModel =
                        (SingleSelectionModel<Queue>)queueTable.getSelectionModel();

                final Queue queue = selectionModel.getSelectedObject();
                Feedback.confirm("Flush Queue", "Do you really want to flush queue "+queue.getName(),
                        new Feedback.ConfirmationHandler(){
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                presenter.onFlushQueue(queue);
                            }
                        });
            }
        }));

        VerticalPanel tablePanel = new VerticalPanel();
        tablePanel.setStyleName("fill-layout-width");
        tablePanel.add(queueTools.asWidget());
        tablePanel.add(queueTable);
        tablePanel.add(pager);

        VerticalPanel messagePanel = new VerticalPanel();
        messagePanel.setStyleName("fill-layout-width");
        messagePanel.add(sampler.asWidget());
        messagePanel.add(messageSampler.asWidget());

        OneToOneLayout layout = new OneToOneLayout()
                .setTitle("Queues")
                .setPlain(true)
                .setTopLevelTools(toolStrip.asWidget())
                .setHeadline("JMS Queue Metrics")
                .setDescription(Console.CONSTANTS.subsys_messaging_queue_metric_desc())
                .setMaster("Queue Selection", tablePanel)
                .addDetail("Messages", messagePanel)
                .addDetail("Consumer", consumerSampler.asWidget());

        return layout.build();
    }

    private Queue getCurrentSelection() {
        return ((SingleSelectionModel<Queue>) queueTable.getSelectionModel()).getSelectedObject();
    }

    public void clearSamples() {
        sampler.clearSamples();
        messageSampler.clearSamples();

    }

    public void setQueues(List<Queue> queues) {
        dataProvider.setList(queues);

        if(!queues.isEmpty())
            queueTable.getSelectionModel().setSelected(queues.get(0), true);
    }

    public void setInflight(Metric queueInflight) {
        sampler.addSample(queueInflight);
    }

    public void setProcessed(Metric queueProcessed) {
        messageSampler.addSample(queueProcessed);
    }

    public void setConsumer(Metric queueConsumer) {
        consumerSampler.addSample(queueConsumer);
    }
}
