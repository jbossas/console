package org.jboss.as.console.client.domain.groups;

import com.google.gwt.user.client.ui.Widget;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
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
    private DynamicForm profileform;
    private ListGrid propertyGrid;
    private ComboBoxItem socketBindingItem;

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
        //form.setHeight100();
        TextItem nameField = new TextItem("group-name", "Group Name");
        TextItem jvmField = new TextItem("jvm", "JVM");

        socketBindingItem = new ComboBoxItem();
        socketBindingItem.setTitle("Socket Binding");
        socketBindingItem.setType("comboBox");
        socketBindingItem.setValueMap("default", "DMZ");
        socketBindingItem.setDefaultToFirstOption(true);

        form.setFields(nameField, jvmField, socketBindingItem);

        form.setMargin(15);

        HLayout attributeLayout = new HLayout();
        attributeLayout.setWidth100();

        VLayout attLeft = new VLayout();
        VLayout attRight = new VLayout();

        attLeft.addMember(new ContentGroupLabel("Attributes"));
        attLeft.addMember(form);

        attRight.addMember(new ContentGroupLabel("Profile"));
        profileform = new DynamicForm();
        profileform.setMargin(15);
        TextItem profileNameField = new TextItem("profile-name", "Profile Name");
        profileform.setFields(profileNameField);
        attRight.addMember(profileform);

        attributeLayout.addMember(attLeft);
        attributeLayout.addMember(attRight);
        layout.addMember(attributeLayout);

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


        // ---------------------------------------------------

        Button saveBtn = new Button("Save");
        saveBtn.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent clickEvent) {
                Console.MODULES.getMessageCenter().notify(
                        new Message("Server Group changes saved!", Message.Severity.Info)
                );
            }
        });
        saveBtn.setLayoutAlign(Alignment.RIGHT);

        return layout;
    }

    public void setSelectedRecord(final ServerGroupRecord record)
    {
        final String selectedGroupName = record.getAttribute("group-name");

        nameLabel.setContents(selectedGroupName);

        form.editRecord(record);
        profileform.editRecord(record);

        // TODO: update socket binding ref

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
}
