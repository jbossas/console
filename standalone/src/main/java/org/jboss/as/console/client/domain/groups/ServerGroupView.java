package org.jboss.as.console.client.domain.groups;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.widgets.ComboBox;
import org.jboss.as.console.client.widgets.ContentGroupLabel;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.Feedback;
import org.jboss.as.console.client.widgets.TitleBar;
import org.jboss.as.console.client.widgets.forms.*;
import org.jboss.as.console.client.widgets.icons.Icons;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;
import org.jboss.as.console.client.widgets.tables.DefaultEditTextCell;
import org.jboss.as.console.client.widgets.tables.DefaultOptionRolloverHandler;
import org.jboss.as.console.client.widgets.tables.OptionCell;
import org.jboss.as.console.client.widgets.tools.ToolButton;
import org.jboss.as.console.client.widgets.tools.ToolStrip;

import java.util.*;

/**
 * Shows an editable view of a single server group.
 *
 * @author Heiko Braun
 * @date 2/16/11
 */
public class ServerGroupView extends SuspendableViewImpl implements ServerGroupPresenter.MyView {

    private ServerGroupPresenter presenter;
    private Form<ServerGroupRecord> form;
    private DefaultCellTable<PropertyRecord> propertyTable;
    private ContentHeaderLabel nameLabel;

    private ComboBoxItem profileItem;
    private ComboBoxItem socketBindingItem;
    private ComboBoxItem jvmField;

    private ToolButton edit;

    private ListDataProvider<PropertyRecord> propertyProvider;
    private BeanFactory beanFactory = GWT.create(BeanFactory.class);
    private Button addProp;

    private LayoutPanel layout;

    @Override
    public void setPresenter(ServerGroupPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        layout = new LayoutPanel();

        TitleBar titleBar = new TitleBar("Server Group");
        layout.add(titleBar);

        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("fill-layout-width");
        panel.getElement().setAttribute("style", "padding:15px;");

        layout.add(panel);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 28, Style.Unit.PX);
        layout.setWidgetTopHeight(panel, 35, Style.Unit.PX, 100, Style.Unit.PCT);

        // ---------------------------------------------

        final ToolStrip toolStrip = new ToolStrip();
        edit = new ToolButton("Edit");
        edit.addClickHandler(new ClickHandler(){
            @Override
            public void onClick(ClickEvent clickEvent) {
                if(edit.getText().equals("Edit"))
                {
                    onEdit();
                }
                else
                {
                    onSave();
                }
            }
        });

        toolStrip.addToolButton(edit);
        ToolButton delete = new ToolButton("Delete");
        delete.addClickHandler(new ClickHandler(){
            @Override
            public void onClick(ClickEvent clickEvent) {
                Feedback.confirm(
                        "Delete Server Group",
                        "Do you want to delete server group '"+form.getEditedEntity().getGroupName()+"'?",
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if(isConfirmed)
                                    presenter.deleteCurrentRecord();
                            }
                        });
            }
        });
        toolStrip.addToolButton(delete);

        nameLabel = new ContentHeaderLabel("Name here ...");

        HorizontalPanel horzPanel = new HorizontalPanel();
        horzPanel.getElement().setAttribute("style", "width:100%;");
        Image image = new Image(Icons.INSTANCE.serverGroup());
        horzPanel.add(image);
        horzPanel.add(nameLabel);
        horzPanel.add(toolStrip);
        toolStrip.getElement().getParentElement().setAttribute("width", "50%");
        image.getElement().getParentElement().setAttribute("width", "25");

        panel.add(horzPanel);

        // ---------------------------------------------------

        form = new Form<ServerGroupRecord>(ServerGroupRecord.class);
        form.setNumColumns(2);

        TextItem nameField = new TextItem("groupName", "Group Name");
        jvmField = new ComboBoxItem("jvm", "Virtual Machine");
        jvmField.setValueMap(new String[] {"default"}); // TODO: https://issues.jboss.org/browse/JBAS-9156

        socketBindingItem = new ComboBoxItem("socketBinding", "Socket Binding");
        socketBindingItem.setDefaultToFirstOption(true);

        profileItem = new ComboBoxItem("profileName", "Profile");

        form.setFields(nameField, profileItem);
        form.setFieldsInGroup("Advanced", new DisclosureGroupRenderer(), socketBindingItem, jvmField);

        panel.add(new ContentGroupLabel("Attributes"));

        panel.add(form.asWidget());

        // ---------------------------------------------------

        panel.add(new ContentGroupLabel("System Properties"));

        propertyTable = new DefaultCellTable<PropertyRecord>(5);
        propertyProvider = new ListDataProvider<PropertyRecord>();
        propertyProvider.addDataDisplay(propertyTable);

        addProp = new Button("Add");
        addProp.setStyleName("default-button");

        addProp.getElement().setAttribute("style", "float:right");
        addProp.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                PropertyRecord newRecord = beanFactory.property().as();
                newRecord.setKey("key");
                newRecord.setValue("value");
                propertyProvider.getList().add(newRecord);
                propertyProvider.refresh();
            }
        });
        panel.add(addProp);


        // Create columns
        Column<PropertyRecord, String> keyColumn = new Column<PropertyRecord, String>(new DefaultEditTextCell()) {

            {
                setFieldUpdater(new FieldUpdater<PropertyRecord, String>() {

                    @Override
                    public void update(int index, PropertyRecord object, String value) {
                        object.setKey(value);
                    }
                });
            }

            @Override
            public String getValue(PropertyRecord object) {
                return object.getKey();
            }

        };

        Column<PropertyRecord, String> valueColumn = new Column<PropertyRecord, String>(new DefaultEditTextCell()) {
            {
                setFieldUpdater(new FieldUpdater<PropertyRecord, String>() {

                    @Override
                    public void update(int index, PropertyRecord object, String value) {
                        object.setValue(value);
                    }
                });
            }

            @Override
            public String getValue(PropertyRecord object) {
                return object.getValue();
            }
        };

        OptionCell optionCell = new OptionCell("remove", new ActionCell.Delegate<String>()
        {
            @Override
            public void execute(String rowNum) {
                Integer row = Integer.valueOf(rowNum);
                PropertyRecord propertyRecord = propertyProvider.getList().get(row);
                propertyProvider.getList().remove(propertyRecord);
                propertyProvider.refresh();
            }
        });

        Column<PropertyRecord, String> optionColumn = new Column<PropertyRecord, String>(optionCell) {
            @Override
            public String getValue(PropertyRecord object) {
                return "";
            }

        };

        // Add the columns.
        propertyTable.addColumn(keyColumn, "Key");
        propertyTable.addColumn(valueColumn, "Value");
        propertyTable.addColumn(optionColumn);


        propertyTable.setColumnWidth(keyColumn, 50, Style.Unit.PCT);
        propertyTable.setColumnWidth(valueColumn, 40, Style.Unit.PCT);
        propertyTable.setColumnWidth(optionColumn, 10, Style.Unit.PCT);


        propertyTable.setRowOverHandler(
                new DefaultOptionRolloverHandler(propertyProvider, propertyTable)
        );

        panel.add(propertyTable);

        return layout;
    }

    private void onSave() {
        ServerGroupRecord updatedEntity = form.getUpdatedEntity();
        updatedEntity.setProperties(new HashMap<String,String>());

        for(PropertyRecord prop : propertyProvider.getList())
        {
            updatedEntity.getProperties().put(prop.getKey(), prop.getValue());
        }

        presenter.onSaveChanges(updatedEntity);
    }

    private void onEdit() {
        presenter.editCurrentRecord();
    }


    public void setSelectedRecord(final ServerGroupRecord record) {

        // title
        final String selectedGroupName = record.getGroupName();
        nameLabel.setHTML(selectedGroupName);

        // form
        form.edit(record);
        form.rememberValues();

        // property records
        final Map<String, String> properties = record.getProperties();
        if (properties != null) {
            List<PropertyRecord> propRecords = new ArrayList<PropertyRecord>(properties.size());
            Set<String> strings = properties.keySet();

            for (final String key : strings) {
                PropertyRecord propertyRecord = beanFactory.property().as();
                propertyRecord.setKey(key);
                propertyRecord.setValue(properties.get(key));
                propRecords.add(propertyRecord);
            }

            propertyProvider.setList(propRecords);

        }
    }

    @Override
    public void updateProfiles(List<ProfileRecord> result) {

        List<String> names = new ArrayList<String>(result.size());
        for(ProfileRecord rec : result)
            names.add(rec.getName());

        profileItem.setValueMap(names);
    }

    public void setEnabled(boolean isEnabled) {
        form.setEnabled(isEnabled);
        propertyTable.setEnabled(isEnabled);

        edit.setText(
            isEnabled ? "Save" : "Edit"
        );

        addProp.setVisible(isEnabled);
    }

    @Override
    public void updateSocketBindings(List<String> result) {
        socketBindingItem.setValueMap(result);
    }
}
