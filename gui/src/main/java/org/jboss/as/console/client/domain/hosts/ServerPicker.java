package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.widgets.tables.TablePicker;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/4/11
 */
public class ServerPicker {

    private DefaultCellTable<ServerInstance> serverTable;
    private ListDataProvider<ServerInstance> dataProvider;
    private TablePicker<ServerInstance> serverSelection;

    private SelectionHandler handler;

    public ServerPicker(SelectionHandler handler) {
        this.handler = handler;
    }

    public Widget asWidget() {

        serverTable = new DefaultCellTable<ServerInstance>(5);
        dataProvider = new ListDataProvider<ServerInstance>();
        dataProvider.addDataDisplay(serverTable);

        Column<ServerInstance, String> nameCol = new Column<ServerInstance, String>(new TextCell()) {
            @Override
            public String getValue(ServerInstance object) {
                return object.getName();
            }
        };
        serverTable.addColumn(nameCol, "Server");

        serverSelection = new TablePicker(serverTable, new TablePicker.ValueRenderer<ServerInstance>() {
            @Override
            public String render(ServerInstance selection) {
                return selection.getName();
            }
        });

        serverSelection.setPopupWidth(400);
        serverSelection.setDescription("Please select a server instance:");

        Widget widget = serverSelection.asWidget();
        widget.getElement().setAttribute("style", "width:200px;padding-right:5px;");

        serverTable.getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler(){
            @Override
            public void onSelectionChange(SelectionChangeEvent selectionChangeEvent) {
                ServerInstance server = ((SingleSelectionModel<ServerInstance>) serverTable.getSelectionModel()).getSelectedObject();
                handler.onSelection(server);
            }
        });

        return widget;
    }

    public void setServers(List<ServerInstance> servers) {

        serverSelection.clearSelection();
        dataProvider.setList(servers);

        // TODO: is a default selection right in this case?
        if(!servers.isEmpty())
            setSelected(servers.get(0), true);
    }

    public void setSelected(ServerInstance server, boolean isSelected)
    {
        if(!server.isRunning())
            Console.warning("Selected in-active server instance:"+server.getName());

        serverTable.getSelectionModel().setSelected(server, isSelected);
    }

    public interface SelectionHandler {
        void onSelection(ServerInstance server);
    }

}
