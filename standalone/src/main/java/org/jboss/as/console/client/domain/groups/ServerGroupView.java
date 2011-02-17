package org.jboss.as.console.client.domain.groups;

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
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.components.SuspendableViewImpl;
import org.jboss.as.console.client.components.sgwt.ContentGroupLabel;
import org.jboss.as.console.client.components.sgwt.ContentHeaderLabel;
import org.jboss.as.console.client.components.sgwt.TitleBar;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.util.message.Message;

import java.util.Map;

/**
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

        TitleBar titleBar = new TitleBar("Server Groups");
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

        ContentGroupLabel propertiesLabel = new ContentGroupLabel("System Properties");
        layout.addMember(propertiesLabel);

        propertyGrid = new ListGrid();
        propertyGrid.setTitle("System Properties");
        propertyGrid.setWidth100();
        propertyGrid.setHeight("*");
        ListGridField keyField = new ListGridField("key", "Property Name");
        ListGridField valueField = new ListGridField("value", "Property Value");
        propertyGrid.setFields(keyField, valueField);
        propertyGrid.setMargin(15);

        layout.addMember(propertyGrid);

        // ---------------------------------------------------

        ContentGroupLabel deploymentLabel = new ContentGroupLabel("Deployments");
        layout.addMember(deploymentLabel);

        ListGrid deploymentGrid = new ListGrid();
        deploymentGrid.setMargin(15);
        deploymentGrid.setWidth100();
        deploymentGrid.setHeight("*");
        deploymentGrid.setShowAllRecords(true);

        ListGridField dplNameField = new ListGridField("name", "Name");
        ListGridField dplRtField = new ListGridField("runtime-name", "Runtime Name");
        deploymentGrid.setFields(dplNameField, dplRtField);

        layout.addMember(deploymentGrid);


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
