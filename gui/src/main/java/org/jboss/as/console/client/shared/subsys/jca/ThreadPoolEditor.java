package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.properties.PropertyEditor;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaWorkmanager;
import org.jboss.as.console.client.shared.subsys.jca.model.WorkmanagerPool;
import org.jboss.as.console.client.shared.viewframework.builder.FormLayout;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.as.console.client.widgets.forms.BlankItem;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.dmr.client.ModelNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author Heiko Braun
 * @date 11/30/11
 */
public class ThreadPoolEditor {

    private DefaultCellTable<WorkmanagerPool> table;
    private ListDataProvider<WorkmanagerPool> dataProvider;
    private Form<WorkmanagerPool> sizingForm;
    private Form<WorkmanagerPool> attributesForm;
    private PropertyEditor propertyEditor;

    private JcaPresenter presenter;
    private String contextName;
    private Label headline;

    private JcaWorkmanager currentManager;

    private ToolButton add,remove;

    public ThreadPoolEditor(JcaPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
        table = new DefaultCellTable<WorkmanagerPool>(10);

        dataProvider = new ListDataProvider<WorkmanagerPool>();
        dataProvider.addDataDisplay(table);

        TextColumn<WorkmanagerPool> name = new TextColumn<WorkmanagerPool>() {
            @Override
            public String getValue(WorkmanagerPool record) {
                return record.getName();
            }
        };

        TextColumn<WorkmanagerPool> type = new TextColumn<WorkmanagerPool>() {
            @Override
            public String getValue(WorkmanagerPool record) {

                String type = record.isShortRunning() ? "short-running" : "long-runnig";
                return type;
            }
        };

        TextColumn<WorkmanagerPool> size = new TextColumn<WorkmanagerPool>() {
                   @Override
                   public String getValue(WorkmanagerPool record) {
                       return String.valueOf(record.getMaxThreads());
                   }
               };


        table.addColumn(name, "Name");
        table.addColumn(type, "Type");
        table.addColumn(size, "Max Threads");

        ToolStrip topLevelTools = new ToolStrip();
        add = new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                presenter.launchNewPoolDialoge(currentManager);
            }
        });
        topLevelTools.addToolButtonRight(add);

        remove = new ToolButton(Console.CONSTANTS.common_label_remove(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                final SingleSelectionModel<WorkmanagerPool> selectionModel = (SingleSelectionModel<WorkmanagerPool>) table.getSelectionModel();
                final WorkmanagerPool pool = selectionModel.getSelectedObject();

                if (pool.isShortRunning()) {
                    Console.error(Console.CONSTANTS.subsys_jca_error_pool_removal(),
                            Console.CONSTANTS.subsys_jca_error_pool_removal_desc());
                } else {

                    Feedback.confirm(
                            Console.MESSAGES.deleteTitle("Pool Config"),
                            Console.MESSAGES.deleteConfirm("Pool Config"),
                            new Feedback.ConfirmationHandler() {
                                @Override
                                public void onConfirmation(boolean isConfirmed) {
                                    if (isConfirmed) {
                                        presenter.onRemovePoolConfig(
                                                contextName, pool
                                        );
                                    }
                                }
                            });
                }
            }
        });
        topLevelTools.addToolButtonRight(remove);

        // ---

        attributesForm = new Form<WorkmanagerPool>(WorkmanagerPool.class);
        attributesForm.setNumColumns(2);
        attributesForm.setEnabled(false);

        TextBoxItem nameItemAttr = new TextBoxItem("name", "Name");
        CheckBoxItem allowCore = new CheckBoxItem ("allowCoreTimeout", "Allow Core Timeout?");
        NumberBoxItem keepAliveTimeout = new NumberBoxItem("keepaliveTime", "Keep Alive Timeout") {
            {
                isRequired = false;
            }
        };
        TextBoxItem threadFactory = new TextBoxItem("threadFactory", "Thread Factory") {
            {
                isRequired = false;
            }
        };

        ComboBoxItem unit = new ComboBoxItem("keepaliveTimeUnit", "Keepalive Timeout Unit");
        unit.setValueMap(new String[] {"DAYS", "HOURS", "MINUTES", "SECONDS", "MILLISECONDS", "NANOSECONDS"});
        unit.selectItem(4);

        attributesForm.setFields(
                nameItemAttr, BlankItem.INSTANCE,
                keepAliveTimeout, unit,
                allowCore, threadFactory
        );

        // ---


        sizingForm = new Form<WorkmanagerPool>(WorkmanagerPool.class);
        sizingForm.setNumColumns(2);
        sizingForm.setEnabled(false);

        TextBoxItem nameItemSizing = new TextBoxItem("name", "Name");
        NumberBoxItem maxThreads = new NumberBoxItem("maxThreads", "Max Threads");
        NumberBoxItem maxThreadsPerCPU = new NumberBoxItem("coreThreads", "Core threads");
        NumberBoxItem queueLength = new NumberBoxItem("queueLength", "Queue Length");

        sizingForm.setFields(nameItemSizing, BlankItem.INSTANCE, maxThreads, maxThreadsPerCPU, queueLength);

        attributesForm.bind(table);
        sizingForm.bind(table);

        FormToolStrip<WorkmanagerPool> sizingTools = new FormToolStrip<WorkmanagerPool>(
                sizingForm,
                new FormToolStrip.FormCallback<WorkmanagerPool>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                        presenter.onSavePoolConfig(
                                contextName,
                                sizingForm.getEditedEntity(),
                                changeset
                        );
                    }

                    @Override
                    public void onDelete(WorkmanagerPool entity) {

                    }
                }
        );
        sizingTools.providesDeleteOp(false);

        FormToolStrip<WorkmanagerPool> attributesTools = new FormToolStrip<WorkmanagerPool>(
                attributesForm,
                new FormToolStrip.FormCallback<WorkmanagerPool>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                        presenter.onSavePoolConfig(
                                contextName,
                                attributesForm.getEditedEntity(),
                                changeset
                        );
                    }

                    @Override
                    public void onDelete(WorkmanagerPool entity) {

                    }
                }
        );
        attributesTools.providesDeleteOp(false);

        final FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        ModelNode address = Baseadress.get();
                        address.add("subsystem", "jca");
                        address.add("workmanager", "*");
                        address.add("short-running-threads", "*");
                        return address;
                    }
                }, attributesForm
        );

        Widget attributesPanel = new FormLayout()
                .setForm(attributesForm)
                .setHelp(helpPanel)
                .setSetTools(attributesTools)
                .build();


        final FormHelpPanel sizingHelpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        ModelNode address = Baseadress.get();
                        address.add("subsystem", "jca");
                        address.add("workmanager", "*");
                        address.add("short-running-threads", "*");
                        return address;
                    }
                }, sizingForm
        );

        Widget sizingPanel = new FormLayout()
                .setForm(sizingForm)
                .setHelp(sizingHelpPanel)
                .setSetTools(sizingTools)
                .build();


        // ---

        propertyEditor = new PropertyEditor(presenter, true);

        headline = new Label("HEADLINE");
        headline.setStyleName("content-header-label");

        // ---
        Widget panel = new MultipleToOneLayout()
                .setPlain(true)
                .setHeadlineWidget(headline)
                .setTitle("Thread Pool")
                .setDescription(Console.CONSTANTS.subsys_jca_threadpool_config_desc())
                .setMaster(Console.MESSAGES.available("Thread Pools"), table)
                .setMasterTools(topLevelTools.asWidget())
                .addDetail("Attributes", attributesPanel)
                .addDetail("Sizing", sizingPanel)
                .build();

        return panel;
    }

    private String createReferenceToken(WorkmanagerPool pool) {
        String type = pool.isShortRunning() ? "short-running-threads":"long-running-threads";
        return contextName+"/"+type+"/"+pool.getName();
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
        this.headline.setText("Work Manager: " +contextName);
    }

    public void setWorkManager(JcaWorkmanager manager) {

        this.currentManager = manager;

        // don't mess with the default managers
        boolean enabled = !manager.getName().equals("default");
        add.setVisible(enabled);
        remove.setVisible(enabled);

        List<WorkmanagerPool> pools = new ArrayList<WorkmanagerPool>(2);

        pools.addAll(manager.getShortRunning());
        pools.addAll(manager.getLongRunning());

        dataProvider.setList(pools);
        if(!pools.isEmpty())
            table.getSelectionModel().setSelected(pools.get(0), true);

    }
}
