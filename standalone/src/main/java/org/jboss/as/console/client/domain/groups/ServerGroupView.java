package org.jboss.as.console.client.domain.groups;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.widgets.ContentGroupLabel;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.RHSContentPanel;
import org.jboss.as.console.client.widgets.forms.ComboBoxItem;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.TextBoxItem;
import org.jboss.as.console.client.widgets.forms.TextItem;
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

    private ToolButton edit;

    private ListDataProvider<PropertyRecord> propertyProvider;
    private BeanFactory beanFactory = GWT.create(BeanFactory.class);
    private Button addProp;

    @Override
    public void setPresenter(ServerGroupPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new RHSContentPanel("Server Group");

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
                presenter.deleteCurrentRecord();
            }
        });
        toolStrip.addToolButton(delete);

        layout.add(toolStrip);

        nameLabel = new ContentHeaderLabel("Name here ...");
        nameLabel.setIcon("common/server_group.png");
        layout.add(nameLabel);

        // ---------------------------------------------------

        form = new Form<ServerGroupRecord>(ServerGroupRecord.class);
        form.setNumColumns(2);

        TextItem nameField = new TextItem("groupName", "Group Name");
        TextBoxItem jvmField = new TextBoxItem("jvm", "JVM");

        final ComboBoxItem socketBindingItem = new ComboBoxItem("socketBinding", "Socket Binding");
        socketBindingItem.setValueMap(presenter.getSocketBindings());
        socketBindingItem.setDefaultToFirstOption(true);

        final ComboBoxItem profileItem = new ComboBoxItem("profileName", "Profile");
        profileItem.setValueMap(presenter.getProfileNames());
        profileItem.setDefaultToFirstOption(true);

        form.setFields(nameField, jvmField, socketBindingItem, profileItem);

        layout.add(new ContentGroupLabel("Attributes"));

        layout.add(form.asWidget());

        // ---------------------------------------------------

        layout.add(new ContentGroupLabel("System Properties"));

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
        layout.add(addProp);


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

        layout.add(propertyTable);


        // -----------------



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

    public void setEnabled(boolean isEnabled) {
        form.setEnabled(isEnabled);
        propertyTable.setEnabled(isEnabled);

        edit.setText(
            isEnabled ? "Save" : "Edit"
        );

        addProp.setVisible(isEnabled);
    }
}
