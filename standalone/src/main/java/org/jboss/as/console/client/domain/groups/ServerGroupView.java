package org.jboss.as.console.client.domain.groups;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.Widget;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.events.ItemChangedEvent;
import com.smartgwt.client.widgets.form.events.ItemChangedHandler;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import org.jboss.as.console.client.components.SuspendableViewImpl;
import org.jboss.as.console.client.components.sgwt.ContentGroupLabel;
import org.jboss.as.console.client.components.sgwt.ContentHeaderLabel;
import org.jboss.as.console.client.components.sgwt.TitleBar;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;

import java.util.Map;

/**
 * Shows an editable view of a single server group.
 *
 * @author Heiko Braun
 * @date 2/16/11
 */
public class ServerGroupView extends SuspendableViewImpl implements ServerGroupPresenter.MyView {

    private ServerGroupPresenter presenter;
    private DynamicForm form;
    private ListGrid propertyGrid;
    private ContentHeaderLabel nameLabel;

    @Override
    public void setPresenter(ServerGroupPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        final VLayout layout = new VLayout();
        layout.setWidth100();
        layout.setHeight100();

        TitleBar titleBar = new TitleBar("Server Group");
        layout.addMember(titleBar);

        nameLabel = new ContentHeaderLabel();
        nameLabel.setIcon("common/server_group.png");
        layout.addMember(nameLabel);

        // ---------------------------------------------------

        form = new DynamicForm();
        form.setWidth100();
        form.setNumCols(4); //(TextItem, SelectItem, etc) take up two columns by default

        TextItem nameField = new TextItem("group-name", "Group Name");
        TextItem jvmField = new TextItem("jvm", "JVM");

        final ComboBoxItem socketBindingItem = new ComboBoxItem("socket-binding");
        socketBindingItem.setTitle("Socket Binding");
        socketBindingItem.setType("comboBox");
        socketBindingItem.setValueMap(presenter.getSocketBindings());
        socketBindingItem.setDefaultToFirstOption(true);

        final ComboBoxItem profileItem = new ComboBoxItem("profile-name");
        profileItem.setTitle("Profile");
        profileItem.setType("comboBox");
        profileItem.setValueMap(presenter.getProfileNames());
        profileItem.setDefaultToFirstOption(true);
        

        final Button button = new Button("Save");
        button.setWidth(80);
        button.setLayoutAlign(Alignment.CENTER);
        button.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                Scheduler.get().scheduleDeferred(
                        new Scheduler.ScheduledCommand() {
                            @Override
                            public void execute() {
                                presenter.persistChanges(form.getChangedValues());
                                form.rememberValues();
                                form.setBackgroundColor("#ffffff");
                                button.animateFade(0);
                            }
                        }
                );
            }
        });

        button.setAnimateTime(600);
        button.setVisible(false);

        form.setFields(nameField, jvmField, socketBindingItem, profileItem);
        form.setMargin(15);
        form.setPadding(4);
        form.addItemChangedHandler(new ItemChangedHandler()
        {
            @Override
            public void onItemChanged(ItemChangedEvent itemChangedEvent) {
                button.setVisible(true);
                button.animateFade(100);
                form.setBackgroundColor("#F0F0D8");
            }
        });

        for(FormItem item : form.getFields())
            item.setShowDisabled(false);

        layout.addMember(new ContentGroupLabel("Attributes"));
        layout.addMember(form);
        layout.addMember(button);

        // ---------------------------------------------------

        layout.addMember(new ContentGroupLabel("System Properties"));

        propertyGrid = new ListGrid();
        propertyGrid.setTitle("System Properties");
        propertyGrid.setWidth100();
        propertyGrid.setHeight("*");
        ListGridField keyField = new ListGridField("key", "Property Name");
        ListGridField valueField = new ListGridField("value", "Property Value");
        propertyGrid.setFields(keyField, valueField);

        // ------------

        ToolStrip toolStrip = new ToolStrip();
        toolStrip.setStyleName("inline-toolstrip");
        toolStrip.setWidth100();
        toolStrip.setHeight(10);

        ToolStripButton addButton = new ToolStripButton();
        addButton.setIcon("common/xs/add.png");
        addButton.setIconWidth(10);
        addButton.setIconHeight(10);

        ToolStripButton delButton = new ToolStripButton();
        delButton.setIcon("common/xs/delete.png");
        delButton.setIconWidth(10);
        delButton.setIconHeight(10);

        toolStrip.addButton(addButton);
        toolStrip.addSeparator();
        toolStrip.addButton(delButton);

        toolStrip.setAlign(Alignment.RIGHT);

        addButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent clickEvent) {
                propertyGrid.startEditingNew();
            }
        });

        delButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent clickEvent) {
                ListGridRecord selectedRecord = propertyGrid.getSelectedRecord();
                if(selectedRecord!=null)
                    Log.debug("remove " + selectedRecord.getAttribute("key"));
            }
        });

        VLayout propsLayout = new VLayout();
        propsLayout.addMember(toolStrip);
        propsLayout.addMember(propertyGrid);
        propsLayout.setLayoutLeftMargin(15);
        propsLayout.setLayoutRightMargin(15);
        layout.addMember(propsLayout);

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

        return layout;
    }

    public void setSelectedRecord(final ServerGroupRecord record)
    {
        final String selectedGroupName = record.getAttribute("group-name");

        nameLabel.setContents(selectedGroupName);

        form.editRecord(record);
        form.rememberValues();

        final Map<String, String> properties = record.getAttributeAsMap("properties");

        if(properties!=null)
        {
            ListGridRecord[] propRecords = new ListGridRecord[properties.size()];
            int i=0;
            for(final String key : properties.keySet())
            {
                propRecords[i] = new ListGridRecord(){{
                    setAttribute("key", key);
                    setAttribute("value", properties.get(key));

                }};
                i++;
            }

            propertyGrid.setData(propRecords);
        }
        else
        {
            // no system properties available
            propertyGrid.setData(new Record[]{});
        }
    }

    public void setEnabled(boolean isEnabled)
    {
        if(isEnabled)
        {
            form.enable();
            propertyGrid.enable();
        }
        else
        {
            form.disable();
            propertyGrid.disable();
        }
    }
}
