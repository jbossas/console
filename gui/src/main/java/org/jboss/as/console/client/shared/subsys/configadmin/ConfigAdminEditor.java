package org.jboss.as.console.client.shared.subsys.configadmin;

import java.util.List;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.general.InputWindow;
import org.jboss.as.console.client.shared.properties.PropertyEditor;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.configadmin.model.ConfigAdminData;
import org.jboss.as.console.client.shared.subsys.osgi.config.wizard.NewPropertyWizard;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.ballroom.client.widgets.window.Feedback;

public class ConfigAdminEditor implements PropertyManagement {
    private final ConfigAdminPresenter presenter;
    private PIDTable pidTable;
    private PropertyEditor propertyEditor;
    private DefaultWindow dialog;

    ConfigAdminEditor(ConfigAdminPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
        LayoutPanel layout = new LayoutPanel();

        ToolStrip topLevelTools = new ToolStrip();
        ToolButton editBtn = new ToolButton(Console.CONSTANTS.common_label_edit(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                final ConfigAdminData model = pidTable.getSelection();

                dialog = new DefaultWindow(Console.CONSTANTS.subsys_configadmin_editPID());
                dialog.setWidth(320);
                dialog.setHeight(140);
                dialog.setWidget(new InputWindow(model.getPid(), new InputWindow.Result() {
                    @Override
                    public void result(String value) {
                        if (value != null && !value.equals(model.getPid())) {
                            presenter.onDeleteConfigurationAdminData(model.getPid());
                            model.setPid(value);
                            presenter.onAddConfigurationAdminData(model);
                        }
                        closePropertyDialoge();
                    }
                }).asWidget());
                dialog.setGlassEnabled(true);
                dialog.center();
            }
        });
        editBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_edit_configAdminEditor());
        topLevelTools.addToolButton(editBtn);

        ToolButton deleteBtn = new ToolButton(Console.CONSTANTS.common_label_delete(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                final ConfigAdminData model = pidTable.getSelection();
                Feedback.confirm(Console.MESSAGES.subsys_configadmin_remove(), Console.MESSAGES.subsys_configadmin_removeConfirm(model.getPid()),
                    new Feedback.ConfirmationHandler() {
                        @Override
                        public void onConfirmation(boolean isConfirmed) {
                            if (isConfirmed)
                                presenter.onDeleteConfigurationAdminData(model.getPid());
                        }
                    });
            }
        });
        deleteBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_delete_configAdminEditor());
        topLevelTools.addToolButton(deleteBtn);

        ToolButton addBtn = new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewCASPropertyWizard();
            }
        });
        addBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_add_configAdminEditor());
        topLevelTools.addToolButtonRight(addBtn);
        layout.add(topLevelTools);

        VerticalPanel vpanel = new VerticalPanel();
        vpanel.setStyleName("rhs-content-panel");

        ScrollPanel scroll = new ScrollPanel(vpanel);
        layout.add(scroll);

        layout.setWidgetTopHeight(topLevelTools, 0, Style.Unit.PX, 30, Style.Unit.PX);
        layout.setWidgetTopHeight(scroll, 30, Style.Unit.PX, 100, Style.Unit.PCT);

        HorizontalPanel horzPanel = new HorizontalPanel();
        horzPanel.add(new ContentHeaderLabel(Console.CONSTANTS.subsys_configadmin_header()));
        vpanel.add(horzPanel);

        pidTable = new PIDTable();

        vpanel.add(new ContentGroupLabel(Console.CONSTANTS.subsys_configadmin_PIDLabel()));
        vpanel.add(pidTable.asWidget());

        propertyEditor = new PropertyEditor(this, true, 10);
        final SingleSelectionModel<ConfigAdminData> selectionModel = new SingleSelectionModel<ConfigAdminData>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                ConfigAdminData pid = selectionModel.getSelectedObject();
                propertyEditor.setProperties(pid.getPid(), pid.getProperties());
            }
        });
        pidTable.setSelectionModel(selectionModel);

        vpanel.add(new ContentGroupLabel(Console.CONSTANTS.subsys_configadmin_valuesLabel()));
        vpanel.add(propertyEditor.asWidget());
        propertyEditor.setAllowEditProps(false);

        return layout;
    }

    private ConfigAdminData findData(String pid) {
        for (ConfigAdminData data : pidTable.getData()) {
            if (pid.equals(data.getPid())) {
                return data;
            }
        }
        return null;
    }

    @Override
    public void onCreateProperty(String reference, PropertyRecord prop) {
        dialog.hide();
        ConfigAdminData data = findData(reference);
        if (data == null) {
            Feedback.alert(Console.CONSTANTS.subsys_configadmin_add(),
                new SafeHtmlBuilder().appendEscaped(Console.MESSAGES.subsys_configadmin_addNoPIDselected()).toSafeHtml());
        } else {
            data.getProperties().add(prop);
            presenter.onUpdateConfigurationAdminData(data);
        }
    }

    @Override
    public void onDeleteProperty(final String reference, PropertyRecord prop) {
        ConfigAdminData data = findData(reference);
        data.getProperties().remove(prop);
        if (data.getProperties().size() > 0) {
            presenter.onUpdateConfigurationAdminData(data);
        } else {
            // There are no properties left, remove the PID completely
            Feedback.confirm(Console.MESSAGES.subsys_configadmin_remove(),
                Console.MESSAGES.subsys_configadmin_removeOnLastValueConfirm(reference),
                new Feedback.ConfirmationHandler() {
                    @Override
                    public void onConfirmation(boolean isConfirmed) {
                        if (isConfirmed)
                            presenter.onDeleteConfigurationAdminData(reference);
                    }
                });
        }
    }

    @Override
    public void onChangeProperty(String reference, PropertyRecord prop) {
        // do nothing
    }

    @Override
    public void launchNewPropertyDialoge(String reference) {
        dialog = new DefaultWindow(Console.CONSTANTS.subsys_configadmin_valueAdd());
        dialog.setWidth(320);
        dialog.setHeight(240);
        dialog.setWidget(new NewPropertyWizard(this, reference).asWidget());
        dialog.setGlassEnabled(true);
        dialog.center();
    }

    @Override
    public void closePropertyDialoge() {
        dialog.hide();
    }

    void update(List<ConfigAdminData> casDataList, String selectPid) {
        pidTable.setData(casDataList, selectPid);
    }
}
