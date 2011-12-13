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

    public AdminObjectList(ResourceAdapterPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
        ToolStrip topLevelTools = new ToolStrip();
        topLevelTools.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewConnectionWizard();
            }
        }));

        ClickHandler clickHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                final AdminObject selection = getCurrentSelection();

                Feedback.confirm(
                        Console.MESSAGES.deleteTitle("connection definition"),
                        Console.MESSAGES.deleteConfirm("connection definition" + selection.getJndiName()),
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                /*if (isConfirmed) {
                                    presenter.onDeleteConnection(selection);
                                } */
                            }
                        });
            }
        };
        ToolButton deleteBtn = new ToolButton(Console.CONSTANTS.common_label_delete());
        deleteBtn.addClickHandler(clickHandler);
        topLevelTools.addToolButtonRight(deleteBtn);

        // -------

        table = new DefaultCellTable<AdminObject>(10);
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
                            res = Icons.INSTANCE.statusGreen_small();
                        else
                            res = Icons.INSTANCE.statusRed_small();

                        return res;
                    }
                };


        table.addColumn(nameColumn, "JNDI Name");
        table.addColumn(statusColumn, "Enabled?");


        // ---

        configProperties = new PropertyEditor(this);


        // ----

        VerticalPanel formpanel = new VerticalPanel();
        formpanel.setStyleName("fill-layout-width");

        Form<AdminObject> form = new Form<AdminObject>(AdminObject.class);
        form.setNumColumns(2);

        TextItem jndiItem = new TextItem("jndiName", "JNDI");
        TextBoxItem classItem = new TextBoxItem("adminClass", "Admin Class");
        CheckBoxItem enabled = new CheckBoxItem("enabled", "Enabled?");

        form.setFields(jndiItem, classItem, enabled);

        form.setEnabled(false);
        form.bind(table);

        FormToolStrip<AdminObject> tools = new FormToolStrip<AdminObject>(
                form, new FormToolStrip.FormCallback<AdminObject>() {
            @Override
            public void onSave(Map<String, Object> changeset) {

            }

            @Override
            public void onDelete(AdminObject entity) {

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
            }
        });

        // ----


        headline = new HTML("HEADLINE");
        headline.setStyleName("content-header-label");

        MultipleToOneLayout layout = new MultipleToOneLayout()
                .setPlain(true)
                .setHeadlineWidget(headline)
                .setTitle("TITLE")
                .setDescription("The administration objects for a resource adapter.")
                .setMaster("Registered Admin Objects", table)
                .setMasterTools(topLevelTools.asWidget())
                .addDetail("Attributes", formpanel)
                .addDetail("Properties", configProperties.asWidget());


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
        window = new DefaultWindow(Console.MESSAGES.createTitle("configuration properties"));
        window.setWidth(480);
        window.setHeight(360);

        window.setWidget(
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

        headline.setText("Resource Adapter: "+adapter.getArchive());

        List<AdminObject> list = adapter.getAdminObjects();
        dataProvider.setList(list);

        if(!list.isEmpty())
            table.getSelectionModel().setSelected(list.get(0), true);

    }
}
