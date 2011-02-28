package org.jboss.as.console.client.domain.groups;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.widgets.ContentGroupLabel;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.RHSContentPanel;
import org.jboss.as.console.client.widgets.forms.ComboBoxItem;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.TextItem;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;
import org.jboss.as.console.client.widgets.tables.DefaultEditTextCell;
import org.jboss.as.console.client.widgets.tools.ToolButton;
import org.jboss.as.console.client.widgets.tools.ToolStrip;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @Override
    public void setPresenter(ServerGroupPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new RHSContentPanel("Server Group");

        ToolStrip toolStrip = new ToolStrip();
        edit = new ToolButton("Edit");
        edit.addClickHandler(new ClickHandler(){
            @Override
            public void onClick(ClickEvent clickEvent) {
                if(edit.getText().equals("Edit"))
                    presenter.editCurrentRecord();
                else
                {
                    presenter.onSaveChanges(form.getUpdatedEntity());
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
        TextItem jvmField = new TextItem("jvm", "JVM");

        final ComboBoxItem socketBindingItem = new ComboBoxItem("socketBinding", "Socket Binding");
        socketBindingItem.setValueMap(presenter.getSocketBindings());
        socketBindingItem.setDefaultToFirstOption(true);

        final ComboBoxItem profileItem = new ComboBoxItem("profileName", "Profile");
        profileItem.setValueMap(presenter.getProfileNames());
        profileItem.setDefaultToFirstOption(true);

        form.setFields(nameField, jvmField, socketBindingItem, profileItem);

        layout.add(new ContentGroupLabel("Attributes"));

        layout.add(form.asWidget());
        //layout.add(button);

        // ---------------------------------------------------

        layout.add(new ContentGroupLabel("System Properties"));

        propertyTable = new DefaultCellTable<PropertyRecord>(5);

        // Add a text input column to edit the name.
        final DefaultEditTextCell nameCell = new DefaultEditTextCell();
        Column<PropertyRecord, String> keyColumn = new Column<PropertyRecord, String>(nameCell) {
            @Override
            public String getValue(PropertyRecord object) {
                return object.getKey();
            }
        };

        keyColumn.setFieldUpdater(new FieldUpdater<PropertyRecord, String>() {
            public void update(int index, PropertyRecord object, String value) {
                object.setKey(value);
            }
        });

        // Create address column.
        final DefaultEditTextCell valueCell = new DefaultEditTextCell();
        Column<PropertyRecord, String> valueColumn = new Column<PropertyRecord, String>(valueCell) {
            @Override
            public String getValue(PropertyRecord object) {
                return object.getValue();
            }
        };

        valueColumn.setFieldUpdater(new FieldUpdater<PropertyRecord, String>() {
            public void update(int index, PropertyRecord object, String value) {
                object.setValue(value);
            }
        });

        // Add the columns.
        propertyTable.addColumn(keyColumn, "Key");
        propertyTable.addColumn(valueColumn, "Value");

        propertyTable.setColumnWidth(keyColumn, 50, Style.Unit.PCT);
        propertyTable.setColumnWidth(valueColumn, 50, Style.Unit.PCT);


        layout.add(propertyTable);


        // -----------------



        return layout;
    }

    public void setSelectedRecord(final ServerGroupRecord record) {

        final String selectedGroupName = record.getGroupName();

        nameLabel.setHTML(selectedGroupName);

        form.edit(record);

        form.rememberValues();

        final Map<String, String> properties = record.getProperties();

        BeanFactory factory = GWT.create(BeanFactory.class);

        if (properties != null) {
            List<PropertyRecord> propRecords = new ArrayList<PropertyRecord>(properties.size());
            Set<String> strings = properties.keySet();

            for (final String key : strings) {
                PropertyRecord propertyRecord = factory.property().as();
                propertyRecord.setKey(key);
                propertyRecord.setValue(properties.get(key));
                propRecords.add(propertyRecord);
            }

            propertyTable.setRowCount(propRecords.size());
            propertyTable.setRowData(0, propRecords);
        } else {
            // no system properties available
        }
    }

    public void setEnabled(boolean isEnabled) {
        form.setEnabled(isEnabled);
        propertyTable.setEnabled(isEnabled);

        edit.setText(
            isEnabled ? "Save" : "Edit"
        );
    }
}
