package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.properties.NewPropertyWizard;
import org.jboss.as.console.client.shared.properties.PropertyEditor;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.jca.model.AdminObject;
import org.jboss.as.console.client.shared.subsys.jca.model.ResourceAdapter;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.icons.Icons;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.dmr.client.ModelNode;

import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 12/13/11
 */
public class AdminObjectList implements PropertyManagement {

    private ResourceAdapterPresenter presenter;
    private DefaultCellTable<AdminObject> table;
    private ListDataProvider<AdminObject> dataProvider;
    private PropertyEditor configProperties;
    private HTML headline;
    private DefaultWindow window;
    private ToolButton disableBtn;
    private ResourceAdapter currentAdapter;

    public AdminObjectList(ResourceAdapterPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
        ToolStrip topLevelTools = new ToolStrip();
        topLevelTools.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewAdminWizard();
            }
        }));

        ClickHandler clickHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                final AdminObject selection = getCurrentSelection();

                if(selection!=null)
                {
                    Feedback.confirm(
                            Console.MESSAGES.deleteTitle("Admin Object"),
                            Console.MESSAGES.deleteConfirm("Admin Object" + selection.getJndiName()),
                            new Feedback.ConfirmationHandler() {
                                @Override
                                public void onConfirmation(boolean isConfirmed) {
                                    if (isConfirmed) {
                                        presenter.onRemoveAdmin(selection);
                                    }
                                }
                            });
                }
            }
        };

        ToolButton deleteBtn = new ToolButton(Console.CONSTANTS.common_label_delete());
        deleteBtn.addClickHandler(clickHandler);
        topLevelTools.addToolButtonRight(deleteBtn);

        disableBtn = new ToolButton("", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                final AdminObject selection = getCurrentSelection();

                if(selection!=null)
                {
                    selection.setEnabled(!selection.isEnabled());

                    Feedback.confirm(
                            Console.MESSAGES.modify("Admin Object"),
                            Console.MESSAGES.modifyConfirm("Admin Object " + selection.getJndiName()),
                            new Feedback.ConfirmationHandler() {
                                @Override
                                public void onConfirmation(boolean isConfirmed) {
                                    if (isConfirmed) {
                                        presenter.enOrDisbaleAdminObject(currentAdapter, selection);
                                    }
                                }
                            });
                }

            }
        });
        topLevelTools.addToolButtonRight(disableBtn);

        // -------

        table = new DefaultCellTable<AdminObject>(10, new ProvidesKey<AdminObject>() {
            @Override
            public Object getKey(AdminObject item) {
                return item.getJndiName();
            }
        });

        dataProvider = new ListDataProvider<AdminObject>();
        dataProvider.addDataDisplay(table);

        TextColumn<AdminObject> nameColumn = new TextColumn<AdminObject>() {
            @Override
            public String getValue(AdminObject record) {
                return record.getJndiName();
            }
        };

        Column<AdminObject, ImageResource> statusColumn =
                new Column<AdminObject, ImageResource>(new ImageResourceCell()) {
                    @Override
                    public ImageResource getValue(AdminObject ra) {

                        ImageResource res = null;

                        if(ra.isEnabled())
                            res = Icons.INSTANCE.status_good();
                        else
                            res = Icons.INSTANCE.status_bad();

                        return res;
                    }
                };


        table.addColumn(nameColumn, "JNDI Name");
        table.addColumn(statusColumn, "Enabled?");


        // ---

        configProperties = new PropertyEditor(this, true);

        // ----

        VerticalPanel formpanel = new VerticalPanel();
        formpanel.setStyleName("fill-layout-width");

        Form<AdminObject> form = new Form<AdminObject>(AdminObject.class);
        form.setNumColumns(2);

        TextItem jndiItem = new TextItem("jndiName", "JNDI");
        TextBoxItem classItem = new TextBoxItem("adminClass", "Class Name");
        CheckBoxItem enabled = new CheckBoxItem("enabled", "Enabled?");

        form.setFields(jndiItem, classItem, enabled);

        form.setEnabled(false);
        form.bind(table);

        FormToolStrip<AdminObject> tools = new FormToolStrip<AdminObject>(
                form, new FormToolStrip.FormCallback<AdminObject>() {
            @Override
            public void onSave(Map<String, Object> changeset) {
                presenter.onSaveAdmin(getCurrentSelection(), changeset);
            }

            @Override
            public void onDelete(AdminObject entity) {
                // not possible
            }
        }
        );

        tools.providesDeleteOp(false);

        final FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        ModelNode address = Baseadress.get();
                        address.add("subsystem", "resource-adapters");
                        address.add("resource-adapter", "*");
                        address.add("admin-objects", "*");
                        return address;
                    }
                }, form
        );

        formpanel.add(tools.asWidget());
        formpanel.add(helpPanel.asWidget());
        formpanel.add(form.asWidget());


        table.getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {

            public void onSelectionChange(SelectionChangeEvent event) {
                AdminObject selection = getCurrentSelection();
                configProperties.setProperties("", selection.getProperties());

                String nextState = selection.isEnabled() ?
                        Console.CONSTANTS.common_label_disable():Console.CONSTANTS.common_label_enable();
                disableBtn.setText(nextState);
            }
        });

        // ----


        headline = new HTML("HEADLINE");
        headline.setStyleName("content-header-label");

        MultipleToOneLayout layout = new MultipleToOneLayout()
                .setPlain(true)
                .setHeadlineWidget(headline)
                .setTitle("TITLE")
                .setDescription(Console.CONSTANTS.subsys_jca_adminobject_desc())
                .setMaster(Console.MESSAGES.available("Admin Objects"), table)
                .setMasterTools(topLevelTools.asWidget())
                .addDetail("Attributes", formpanel)
                .addDetail("Properties", configProperties.asWidget());

        configProperties.setAllowEditProps(false);


        return layout.build();
    }

    private AdminObject getCurrentSelection() {
        return ((SingleSelectionModel<AdminObject >) table.getSelectionModel()).getSelectedObject();
    }

    @Override
    public void onCreateProperty(String reference, PropertyRecord prop) {
        closePropertyDialoge();
        presenter.onCreateAdminProperty(getCurrentSelection(), prop);
    }

    @Override
    public void onDeleteProperty(String reference, PropertyRecord prop) {
        presenter.onRemoveAdminProperty(getCurrentSelection(), prop);
    }

    @Override
    public void onChangeProperty(String reference, PropertyRecord prop) {
        // not possible
    }

    @Override
    public void launchNewPropertyDialoge(String reference) {
        window = new DefaultWindow(Console.MESSAGES.createTitle("Config Property"));
        window.setWidth(480);
        window.setHeight(360);

        window.trapWidget(
                new NewPropertyWizard(this, "").asWidget()
        );

        window.setGlassEnabled(true);
        window.center();
    }

    @Override
    public void closePropertyDialoge() {
        window.hide();
    }

    public void setAdapter(ResourceAdapter adapter) {

        this.currentAdapter = adapter;

        configProperties.clearValues();

        headline.setText("Resource Adapter: "+adapter.getArchive());

        List<AdminObject> list = adapter.getAdminObjects();
        dataProvider.setList(list);

        table.selectDefaultEntity();

    }
}
