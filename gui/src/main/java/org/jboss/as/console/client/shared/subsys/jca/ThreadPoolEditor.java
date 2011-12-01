package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.shared.properties.PropertyEditor;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaWorkmanager;
import org.jboss.as.console.client.shared.subsys.threads.model.BoundedQueueThreadPool;
import org.jboss.as.console.client.shared.viewframework.builder.FormLayout;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;

import java.util.List;
import java.util.Map;


/**
 * @author Heiko Braun
 * @date 11/30/11
 */
public class ThreadPoolEditor {

    private DefaultCellTable<BoundedQueueThreadPool> table;
    private ListDataProvider<BoundedQueueThreadPool> dataProvider;
    private Form<BoundedQueueThreadPool> sizingForm;
    private Form<BoundedQueueThreadPool> attributesForm;
    private PropertyEditor propertyEditor;

    private WorkmanagerPresenter presenter;
    private String contextName;
    private Label headline;

    private boolean shortRunning = false;

    public ThreadPoolEditor(WorkmanagerPresenter presenter, boolean isShortRunning) {
        this.presenter = presenter;
        this.shortRunning = isShortRunning;
    }

    Widget asWidget() {
        table = new DefaultCellTable<BoundedQueueThreadPool>(10);

        dataProvider = new ListDataProvider<BoundedQueueThreadPool>();
        dataProvider.addDataDisplay(table);

        TextColumn<BoundedQueueThreadPool> name = new TextColumn<BoundedQueueThreadPool>() {
            @Override
            public String getValue(BoundedQueueThreadPool record) {
                return record.getName();
            }
        };

        TextColumn<BoundedQueueThreadPool> size = new TextColumn<BoundedQueueThreadPool>() {
            @Override
            public String getValue(BoundedQueueThreadPool record) {
                return String.valueOf(record.getMaxThreadsCount());
            }
        };

        table.addColumn(name, "Thread Pool");
        table.addColumn(size, "Max Threads");

        ToolStrip topLevelTools = new ToolStrip();
        topLevelTools.addToolButtonRight(new ToolButton("Add", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewPoolDialoge(contextName, shortRunning);
            }
        }));

        topLevelTools.addToolButtonRight(new ToolButton("Remove", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                Feedback.confirm(
                        "Remove Pool Configuration",
                        "Really remove this pool configuration?",
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed) {
                                    SingleSelectionModel<BoundedQueueThreadPool> selectionModel = (SingleSelectionModel<BoundedQueueThreadPool>) table.getSelectionModel();
                                    presenter.onRemovePoolConfig(
                                            contextName,
                                            shortRunning,
                                            selectionModel.getSelectedObject()
                                    );
                                }
                            }
                        });
            }
        }));

        // ---

        attributesForm = new Form<BoundedQueueThreadPool>(BoundedQueueThreadPool.class);
        attributesForm.setNumColumns(2);
        attributesForm.setEnabled(false);

        TextItem nameItem = new TextItem("name", "Name");
        CheckBoxItem blocking = new CheckBoxItem ("blocking", "Is Blocking?");
        CheckBoxItem allowCore = new CheckBoxItem ("allowCoreTimeout", "Allow Core Timeout?");
        NumberBoxItem keepAliveTimeout = new NumberBoxItem("keepaliveTimeout", "Keep Alive Timeout (ms)") {
            {
                isRequired = false;
            }
        };
        TextBoxItem threadFactory = new TextBoxItem("threadFactory", "Thread Factory") {
            {
                isRequired = false;
            }
        };
        TextBoxItem handoff = new TextBoxItem("handoffExecutor", "Handoff Executor") {
            {
                isRequired = false;
            }
        };

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

        attributesForm.bind(table);
        sizingForm.bind(table);

        FormToolStrip<BoundedQueueThreadPool> sizingTools = new FormToolStrip<BoundedQueueThreadPool>(
                sizingForm,
                new FormToolStrip.FormCallback<BoundedQueueThreadPool>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                        presenter.onSavePoolConfig(
                                contextName,
                                shortRunning,
                                attributesForm.getEditedEntity().getName(),
                                changeset
                        );
                    }

                    @Override
                    public void onDelete(BoundedQueueThreadPool entity) {

                    }
                }
        );
        sizingTools.providesDeleteOp(false);

        FormToolStrip<BoundedQueueThreadPool> attributesTools = new FormToolStrip<BoundedQueueThreadPool>(
                attributesForm,
                new FormToolStrip.FormCallback<BoundedQueueThreadPool>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                        presenter.onSavePoolConfig(
                                contextName,
                                shortRunning,
                                attributesForm.getEditedEntity().getName(),
                                changeset
                        );
                    }

                    @Override
                    public void onDelete(BoundedQueueThreadPool entity) {

                    }
                }
        );
        attributesTools.providesDeleteOp(false);

        Widget attributesPanel = new FormLayout()
                .setForm(attributesForm)
                .setSetTools(attributesTools)
                .build();


        Widget sizingPanel = new FormLayout()
                .setForm(sizingForm)
                .setSetTools(sizingTools)
                .build();


        // ---

        propertyEditor = new PropertyEditor(presenter);

        headline = new Label("HEADLINE");
        headline.setStyleName("content-header-label");

        VerticalPanel header = new VerticalPanel();
        header.add(new HTML("<a href='javascript:history.go(-1)'>&larr; Back to Overview</a>"));
        header.add(headline);

        // ---
        Widget panel = new MultipleToOneLayout()
                .setPlain(true)
                .setHeadlineWidget(header)
                .setTitle("Thread Pool")
                .setDescription("A thread pool executor with a bounded queue used by a JCA workmanager.")
                .setMaster("Configured Thread Pools", table)
                .setTopLevelTools(topLevelTools.asWidget())
                .addDetail("Attributes", attributesPanel)
                .addDetail("Sizing", sizingPanel)
                .addDetail("Properties", propertyEditor.asWidget())
                .build();


        table.getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                BoundedQueueThreadPool pool = ((SingleSelectionModel<BoundedQueueThreadPool>) table.getSelectionModel()).getSelectedObject();

                String ref = createReferenceToken(pool);
                propertyEditor.setProperties(ref, pool.getProperties());
            }
        });
        return panel;
    }

    private String createReferenceToken(BoundedQueueThreadPool pool) {
        String type = shortRunning ? "short-running-threads":"long-running-threads";
        return contextName+"/"+type+"/"+pool.getName();
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
        this.headline.setText("Workmanager: " +contextName);
    }


    public void setPools(List<BoundedQueueThreadPool> pools) {

        dataProvider.setList(pools);

        if(!pools.isEmpty())
            table.getSelectionModel().setSelected(pools.get(0), true);

    }

}
