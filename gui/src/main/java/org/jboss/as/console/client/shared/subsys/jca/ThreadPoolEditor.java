package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.as.console.client.shared.properties.PropertyEditor;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaWorkmanager;
import org.jboss.as.console.client.shared.subsys.threads.model.BoundedQueueThreadPool;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

/**
 * @author Heiko Braun
 * @date 11/30/11
 */
public class ThreadPoolEditor {

    ListDataProvider<JcaWorkmanager> dataProvider;
    Form<BoundedQueueThreadPool> sizingForm;
    Form<BoundedQueueThreadPool> attributesForm;
    PropertyEditor propertyEditor;

    private WorkmanagerPresenter presenter;
    private String contextName;

    public ThreadPoolEditor(WorkmanagerPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
        DefaultCellTable<JcaWorkmanager> table = new DefaultCellTable<JcaWorkmanager>(10);
        dataProvider = new ListDataProvider<JcaWorkmanager>();
        dataProvider.addDataDisplay(table);

        TextColumn<JcaWorkmanager> name = new TextColumn<JcaWorkmanager>() {
            @Override
            public String getValue(JcaWorkmanager record) {
                return record.getName();
            }
        };

        table.addColumn(name, "Thread Pool");
        table.addColumn(name, "Max Threads");

        ToolStrip topLevelTools = new ToolStrip();
        topLevelTools.addToolButtonRight(new ToolButton("Add", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // TODO
            }
        }));

        topLevelTools.addToolButtonRight(new ToolButton("Remove", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // TODO
            }
        }));

        // ---

        attributesForm = new Form<BoundedQueueThreadPool>(BoundedQueueThreadPool.class);
        attributesForm.setNumColumns(2);
        attributesForm.setEnabled(false);
        TextItem nameItem = new TextItem("name", "Name");

        NumberBoxItem keepAliveTimeout = new NumberBoxItem("KeepaliveTimeout", "Keep Alive Timeout (ms)");
        CheckBoxItem blocking = new CheckBoxItem ("blocking", "Is Blocking?");
        CheckBoxItem allowCore = new CheckBoxItem ("AllowCoreTimeout", "Allow Core Timeout?");
        TextBoxItem threadFactory = new TextBoxItem("threadFactory", "Thread Factory");
        TextBoxItem handoff = new TextBoxItem("handoffExecutor", "Handoff Executor");

        attributesForm.setFields(nameItem, keepAliveTimeout, blocking, allowCore, threadFactory, handoff);

        // ---


        sizingForm = new Form<BoundedQueueThreadPool>(BoundedQueueThreadPool.class);
        sizingForm.setNumColumns(2);
        sizingForm.setEnabled(false);

        NumberBoxItem maxThreads = new NumberBoxItem("maxThreadsCount", "Max Threads");
        NumberBoxItem maxThreadsPerCPU = new NumberBoxItem("maxThreadsPerCPU", "Max Threads/CPU");
        NumberBoxItem queueLength = new NumberBoxItem("queueLengthCount", "Queue Length");
        NumberBoxItem queueLengthPerCPU = new NumberBoxItem("queueLengthPerCPU", "Queue Length/CPU");

        sizingForm.setFields(nameItem, maxThreads, maxThreadsPerCPU, queueLength, queueLengthPerCPU);

        // ---

        propertyEditor = new PropertyEditor(presenter);

        // ---
        Widget panel = new MultipleToOneLayout()
                .setPlain(true)
                .setTitle("Thread Pool")
                .setDescription("A thread pool executor with a bounded queue used by a JCA workmanager.")
                .setMaster("Configured Thread Pools", table)
                .setTopLevelTools(topLevelTools.asWidget())
                .addDetail("Attributes", attributesForm.asWidget())
                .addDetail("Sizing", sizingForm.asWidget())
                .addDetail("Properties", propertyEditor.asWidget())
                .build();

        return panel;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }
}
