package org.jboss.as.console.client.domain.groups;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.components.SuspendableViewImpl;
import org.jboss.as.console.client.components.TitleBar;
import org.jboss.as.console.client.components.sgwt.ContentGroupLabel;
import org.jboss.as.console.client.components.sgwt.ContentHeaderLabel;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.forms.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Shows an editable view of a single server group.
 *
 * @author Heiko Braun
 * @date 2/16/11
 */
public class ServerGroupView extends SuspendableViewImpl implements ServerGroupPresenter.MyView {

    private ServerGroupPresenter presenter;
    private Form form;
    private CellTable<PropertyRecord> propertyList;
    private ContentHeaderLabel nameLabel;

    @Override
    public void setPresenter(ServerGroupPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        final VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        TitleBar titleBar = new TitleBar("Server Group");
        layout.add(titleBar);

        nameLabel = new ContentHeaderLabel("Name here ...");
        nameLabel.setIcon("common/server_group.png");
        layout.add(nameLabel);

        // ---------------------------------------------------

        form = new Form();
        form.setNumColumns(2);

        TextItem nameField = new TextItem("group-name", "Group Name");
        TextItem jvmField = new TextItem("jvm", "JVM");

        final ComboBoxItem socketBindingItem = new ComboBoxItem("socket-binding", "Socket Binding");
        socketBindingItem.setValueMap(presenter.getSocketBindings());
        socketBindingItem.setDefaultToFirstOption(true);

        final ComboBoxItem profileItem = new ComboBoxItem("profile-name", "Profile");
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

        Widget formWidget = form.asWidget();
        formWidget.getElement().setAttribute("style", "padding-left:15px;");
        layout.add(formWidget);
        //layout.add(button);

        // ---------------------------------------------------

        layout.add(new ContentGroupLabel("System Properties"));

        PropertyCell propCell = new PropertyCell();

        // Create a CellTable.
        propertyList = new CellTable<PropertyRecord>();

        TextColumn<PropertyRecord> nameColumn = new TextColumn<PropertyRecord>() {
            @Override
            public String getValue(PropertyRecord prop) {
                return prop.getKey();
            }
        };

        // Create address column.
        TextColumn<PropertyRecord> addressColumn = new TextColumn<PropertyRecord>() {
            @Override
            public String getValue(PropertyRecord prop) {
                return prop.getValue();
            }
        };

        // Add the columns.
        propertyList.addColumn(nameColumn, "Key");
        propertyList.addColumn(addressColumn, "Value");

        propertyList.setStyleName("cell-list");
        propertyList.setPageSize(5);

        final SingleSelectionModel<PropertyRecord> selectionModel = new SingleSelectionModel<PropertyRecord>();
        //propertyList.setSelectionModel(selectionModel);
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            public void onSelectionChange(SelectionChangeEvent event) {
                PropertyRecord property = selectionModel.getSelectedObject();
            }
        });

        propertyList.getElement().setAttribute("style", "padding-left:15px;");

        layout.add(propertyList);

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

    public void setSelectedRecord(final ServerGroupRecord record)
    {
        final String selectedGroupName = record.getAttribute("group-name");

        nameLabel.setHTML(selectedGroupName);

        form.editRecord(record);

        form.rememberValues();

        final Map<String, String> properties = record.getAttributeAsMap("properties");

        if(properties!=null)
        {
            List<PropertyRecord> propRecords = new ArrayList<PropertyRecord>(properties.size());
            for(final String key : properties.keySet())
            {
                propRecords.add(new PropertyRecord(key, properties.get(key)));
            }

            propertyList.setRowCount(propRecords.size());
            propertyList.setRowData(0, propRecords);
        }
        else
        {
            // no system properties available
        }
    }

    public void setEnabled(boolean isEnabled)
    {
    }
}
