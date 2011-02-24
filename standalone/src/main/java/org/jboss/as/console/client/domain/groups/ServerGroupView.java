package org.jboss.as.console.client.domain.groups;

import com.google.gwt.autobean.shared.AutoBeanUtils;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.RHSContentPanel;
import org.jboss.as.console.client.widgets.ContentGroupLabel;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.widgets.forms.*;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;

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
    private Form form;
    private CellTable<PropertyRecord> propertyTable;
    private ContentHeaderLabel nameLabel;

        @Override
    public void setPresenter(ServerGroupPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new RHSContentPanel("Server Group");

        nameLabel = new ContentHeaderLabel("Name here ...");
        nameLabel.setIcon("common/server_group.png");
        layout.add(nameLabel);

        // ---------------------------------------------------

        form = new Form();
        form.setNumColumns(2);

        TextItem nameField = new TextItem("groupName", "Group Name");
        TextItem jvmField = new TextItem("jvm", "JVM");

        final ComboBoxItem socketBindingItem = new ComboBoxItem("socketBinding", "Socket Binding");
        socketBindingItem.setValueMap(presenter.getSocketBindings());
        socketBindingItem.setDefaultToFirstOption(true);

        final ComboBoxItem profileItem = new ComboBoxItem("profileName", "Profile");
        profileItem.setValueMap(presenter.getProfileNames());
        profileItem.setDefaultToFirstOption(true);


        final Button button = new Button("Save");
        button.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                Scheduler.get().scheduleDeferred(
                        new Scheduler.ScheduledCommand() {
                            @Override
                            public void execute() {

                                presenter.persistChanges(form.getChangedValues());
                                form.rememberValues();
                                //form.setBackgroundColor("#ffffff");
                                //button.animateFade(0);
                            }
                        }
                );
            }
        });

        //button.setAnimateTime(600);
        //button.setVisible(false);

        form.setFields(nameField, jvmField, socketBindingItem, profileItem);

        form.addItemChangedHandler(new ItemChangedHandler()
        {
            @Override
            public void onItemChanged(FormItem item) {
                /*button.setVisible(true);
                button.animateFade(100);
                form.setBackgroundColor("#F0F0D8");*/
            }
        });

        layout.add(new ContentGroupLabel("Attributes"));

        layout.add(form.asWidget());
        //layout.add(button);

        // ---------------------------------------------------

        layout.add(new ContentGroupLabel("System Properties"));

        propertyTable = new DefaultCellTable<PropertyRecord>(5);

        // Add a text input column to edit the name.
        final EditTextCell nameCell = new EditTextCell();
        Column<PropertyRecord, String> keyColumn = new Column<PropertyRecord, String>(nameCell) {
            @Override
            public String getValue(PropertyRecord object) {
                return object.getKey();
            }
        };

        keyColumn.setFieldUpdater(new FieldUpdater<PropertyRecord, String>() {
            public void update(int index, PropertyRecord object, String value) {
                object.setKey(value);
                System.out.println("field change " + value);
            }
        });

        // Create address column.
        final EditTextCell valueCell = new EditTextCell();
        Column<PropertyRecord, String> valueColumn = new Column<PropertyRecord, String>(valueCell) {
            @Override
            public String getValue(PropertyRecord object) {
                return object.getValue();
            }
        };

        valueColumn.setFieldUpdater(new FieldUpdater<PropertyRecord, String>() {
            public void update(int index, PropertyRecord object, String value) {
                object.setValue(value);
                System.out.println("field change "+value);
            }
        });

        // Add the columns.
        propertyTable.addColumn(keyColumn, "Key");
        propertyTable.addColumn(valueColumn, "Value");

        propertyTable.setColumnWidth(keyColumn, 50, Style.Unit.PCT);
        propertyTable.setColumnWidth(valueColumn, 50, Style.Unit.PCT);

        /*final SingleSelectionModel<PropertyRecord> selectionModel = new SingleSelectionModel<PropertyRecord>();
        propertyTable.setSelectionModel(selectionModel);
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            public void onSelectionChange(SelectionChangeEvent event) {
                PropertyRecord property = selectionModel.getSelectedObject();
            }
        });*/


        layout.add(propertyTable);

        /*
                // ---------------------------------------------------

                layout.addMember(new ContentGroupLabel("Deployments"));

                ListGrid deploymentGrid = new ListGrid();
                deploymentGrid.setWidth100();
                deploymentGrid.setHeight("*");
                deploymentGrid.setShowAllRecords(true);

                ListGridField dplNameField = new ListGridField("name", "Name");
                ListGridField dplRtField = new ListGridField("runtime-name", "Runtime Name");
                deploymentGrid.setFields(dplNameField, dplRtField);

                ToolStrip dplToolStrip = new ToolStrip();
                dplToolStrip.setStyleName("inline-toolstrip");
                dplToolStrip.setWidth100();
                dplToolStrip.setHeight(10);

                ToolStripButton dplAddButton = new ToolStripButton();
                dplAddButton.setIcon("common/xs/add.png");
                dplAddButton.setIconWidth(10);
                dplAddButton.setIconHeight(10);

                ToolStripButton dplDelButton = new ToolStripButton();
                dplDelButton.setIcon("common/xs/delete.png");
                dplDelButton.setIconWidth(10);
                dplDelButton.setIconHeight(10);

                dplToolStrip.addButton(dplAddButton);
                dplToolStrip.addSeparator();
                dplToolStrip.addButton(dplDelButton);

                dplToolStrip.setAlign(Alignment.RIGHT);

                dplAddButton.addClickHandler(new ClickHandler()
                {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        // TODO: implement remove deployment ..
                    }
                });

                dplDelButton.addClickHandler(new ClickHandler()
                {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        ListGridRecord selectedRecord = propertyGrid.getSelectedRecord();
                        if(selectedRecord!=null)
                        {
                            // TODO: implement add deployment ..
                        }

                    }
                });

                VLayout dplLayout = new VLayout();
                dplLayout.addMember(dplToolStrip);
                dplLayout.addMember(deploymentGrid);
                dplLayout.setLayoutLeftMargin(15);
                dplLayout.setLayoutRightMargin(15);
                dplLayout.setLayoutBottomMargin(10);

                layout.addMember(dplLayout);

        */

        return layout;
    }

    public void setSelectedRecord(final ServerGroupRecord record) {

        final String selectedGroupName = record.getGroupName();

        nameLabel.setHTML(selectedGroupName);

        form.editRecord(AutoBeanUtils.getAutoBean(record));

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
    }
}
