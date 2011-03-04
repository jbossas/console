package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.domain.model.MockServerGroupStore;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.widgets.ContentGroupLabel;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.RHSContentPanel;
import org.jboss.as.console.client.widgets.forms.*;

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

        LayoutPanel layout = new RHSContentPanel("Server Configuration");

        nameLabel = new ContentHeaderLabel("Name here ...");
        layout.add(nameLabel);

        layout.add(new ContentGroupLabel("Attributes"));

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
        form.setFieldsInGroup(
                "Advanced",
                new DisclosureGroupRenderer(),
                socketItem, jvmItem
        );

        layout.add(form.asWidget());

        // ------------------------------------------------------

        /*CellTable<ServerInstance> instanceTable = new DefaultCellTable<ServerInstance>(10);

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

        */



        /*PropertyTable propertyTable = new PropertyTable();
        propertyTable.asTable().setRowData(0, Collections.EMPTY_LIST);
        tabLayoutpanel.add(propertyTable, "Advanced Configuration");

        tabLayoutpanel.selectTab(0);

        layout.add(tabLayoutpanel);
        layout.setWidgetTopHeight(tabLayoutpanel, 220, Style.Unit.PX, 100, Style.Unit.PCT);
        */

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
