package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.*;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.domain.model.MockServerGroupStore;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.shared.PropertyTable;
import org.jboss.as.console.client.widgets.ContentGroupLabel;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.TitleBar;
import org.jboss.as.console.client.widgets.forms.CheckBoxItem;
import org.jboss.as.console.client.widgets.forms.ComboBoxItem;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.TextItem;
import org.jboss.as.console.client.widgets.icons.Icons;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;
import org.jboss.as.console.client.widgets.tables.DefaultEditTextCell;

import java.util.Collections;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/3/11
 */
public class ServerView extends SuspendableViewImpl implements ServerPresenter.MyView{


    private ServerPresenter presenter;
    private Form<Server> form;
    private ContentHeaderLabel nameLabel;
    private ComboBoxItem groupItem;
    private ComboBoxItem socketItem;

    @Override
    public void setPresenter(ServerPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new LayoutPanel();

        TitleBar titleBar = new TitleBar("Server Configuration");
        layout.add(titleBar);
        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 28, Style.Unit.PX);

        VerticalPanel vpanel = new VerticalPanel();
        vpanel.getElement().setAttribute("style", "margin:15px;");

        nameLabel = new ContentHeaderLabel("Name here ...");
        vpanel.add(nameLabel);
        vpanel.add(new ContentGroupLabel("Attributes"));

        form = new Form<Server>(Server.class);
        form.setNumColumns(2);

        TextItem nameItem = new TextItem("name", "Server Name");
        CheckBoxItem startedItem = new CheckBoxItem("started", "Start Instances?");
        groupItem = new ComboBoxItem("group", "Server Group");


        // ------------------------------------------------------

        socketItem = new ComboBoxItem("socketBinding", "Socket Binding Group");
        socketItem.setValueMap(new String[] {
                MockServerGroupStore.SOCKET_DEFAULT,
                MockServerGroupStore.SOCKET_DMZ,
                MockServerGroupStore.SOCKET_NO_HTTP
        });

        ComboBoxItem jvmItem = new ComboBoxItem("jvm", "Virtual Machine");
        jvmItem.setValueMap(new String[]{
                MockServerGroupStore.JVM_DEFAULT,
                MockServerGroupStore.JVM_15
        });

        form.setFields(nameItem, startedItem, groupItem);
        /*form.setFieldsInGroup(
                "Advanced",
                new DisclosureGroupRenderer(),
                socketItem, jvmItem
        );*/

        vpanel.add(form.asWidget());

        layout.add(vpanel);
        layout.setWidgetTopHeight(vpanel, 28, Style.Unit.PX, 190, Style.Unit.PX);

        // ------------------------------------------------------

        TabLayoutPanel tabLayoutpanel = new TabLayoutPanel(25, Style.Unit.PX);
        tabLayoutpanel.addStyleName("default-tabpanel");

        CellTable<ServerInstance> instanceTable = new DefaultCellTable<ServerInstance>(10);

        // Create columns
        Column<ServerInstance, String> nameColumn = new Column<ServerInstance, String>(new DefaultEditTextCell()) {
            @Override
            public String getValue(ServerInstance object) {
                return object.getName();
            }
        };

        Column<ServerInstance, ImageResource> statusColumn =
                new Column<ServerInstance, ImageResource>(new ImageResourceCell()) {
            @Override
            public ImageResource getValue(ServerInstance instance) {

                ImageResource res = null;

                if(instance.isRunning())
                    res = Icons.INSTANCE.statusGreen_small();
                else
                    res = Icons.INSTANCE.statusRed_small();

                return res;
            }
        };

        instanceTable.addColumn(nameColumn, "Instance Name");
        instanceTable.addColumn(statusColumn, "Status");

        VerticalPanel instancePanel = new VerticalPanel();
        instancePanel.setStyleName("fill-layout-width");
        instancePanel.add(instanceTable);

        tabLayoutpanel.add(instancePanel, "Server Instances");



        PropertyTable propertyTable = new PropertyTable();
        propertyTable.asTable().setRowData(0, Collections.EMPTY_LIST);
        tabLayoutpanel.add(propertyTable, "Advanced Configuration");

        tabLayoutpanel.selectTab(0);

        layout.add(tabLayoutpanel);
        layout.setWidgetTopHeight(tabLayoutpanel, 220, Style.Unit.PX, 100, Style.Unit.PCT);


        return layout;
    }

    @Override
    public void setSelectedRecord(Server selectedRecord) {
        nameLabel.setText(selectedRecord.getName());
        form.edit(selectedRecord);
    }

    @Override
    public void updateServerGroups(List<ServerGroupRecord> serverGroupRecords) {

        String[] names = new String[serverGroupRecords.size()];
        int i=0;
        for(ServerGroupRecord group : serverGroupRecords)
        {
            names[i] = group.getGroupName();
            i++;
        }
        groupItem.setValueMap(names);
    }
}
