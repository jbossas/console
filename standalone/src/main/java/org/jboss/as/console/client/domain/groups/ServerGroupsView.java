package org.jboss.as.console.client.domain.groups;

import com.google.gwt.user.client.ui.Widget;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import org.jboss.as.console.client.components.SuspendableViewImpl;
import org.jboss.as.console.client.components.sgwt.TitleBar;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 2/16/11
 */
public class ServerGroupsView extends SuspendableViewImpl implements ServerGroupsPresenter.MyView {

    private ServerGroupsPresenter presenter;
    private DynamicForm form;
    private ListGrid groupGrid;
    private ListGrid propertyGrid;

    @Override
    public void setPresenter(ServerGroupsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        final VLayout layout = new VLayout();
        layout.setWidth100();
        layout.setHeight100();

        TitleBar titleBar = new TitleBar("Server Groups");
        layout.addMember(titleBar);

        // ---------------------------------------------------

        groupGrid = new ListGrid();
        groupGrid.setMargin(15);
        groupGrid.setWidth100();
        groupGrid.setHeight100();

        ListGridField groupNameField = new ListGridField("group-name", "Server Group");
        ListGridField profileNameField = new ListGridField("profile-name", "Profile");

        groupGrid.setFields(groupNameField, profileNameField);
        //groupGrid.groupBy("profile-name");

        groupGrid.addRecordClickHandler(new RecordClickHandler()
        {
            @Override
            public void onRecordClick(RecordClickEvent event) {
                presenter.onSelectServerGroup(event.getRecord().getAttribute("group-name"));
            }
        });
        layout.addMember(groupGrid);

        // ---------------------------------------------------

        final TabSet tabSet = new TabSet();
        tabSet.setTabBarPosition(Side.TOP);
        tabSet.setWidth100();
        tabSet.setHeight100();
        //tabSet.setMargin(15);

        Tab tTab1 = new Tab("Properties");
        VLayout t1Layout = new VLayout();

        form = new DynamicForm();
        form.setWidth100();
        form.setHeight100();
        TextItem nameField = new TextItem("group-name", "Group Name");
        TextItem jvmField = new TextItem("jvm", "JVM");
        TextItem socketBindingField = new TextItem("socket-binding", "Socket Binding");

        form.setFields(nameField, jvmField, socketBindingField);
        //form.setTitleField("group-name");
        t1Layout.addMember(form);

        propertyGrid = new ListGrid();
        propertyGrid.setWidth100();
        propertyGrid.setHeight100();
        ListGridField keyField = new ListGridField("key", "Property Name");
        ListGridField valueField = new ListGridField("value", "Property Value");
        propertyGrid.setFields(keyField, valueField);
        propertyGrid.setMargin(10);

        t1Layout.addMember(propertyGrid);

        Button saveBtn = new Button("Save");
        saveBtn.setLayoutAlign(Alignment.RIGHT);
        t1Layout.addMember(saveBtn);

        tTab1.setPane(t1Layout);


        // -------------------------------------------------

        Tab tTab2 = new Tab("Deployments");

        tabSet.addTab(tTab1);
        tabSet.addTab(tTab2);


        layout.addMember(tabSet);

        return layout;
    }

    @Override
    public void updateFrom(ServerGroupRecord[] serverGroupRecords) {
        groupGrid.setData(serverGroupRecords);
    }

    public void setSelectedRecord(ServerGroupRecord record)
    {
        form.editRecord(record);
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
