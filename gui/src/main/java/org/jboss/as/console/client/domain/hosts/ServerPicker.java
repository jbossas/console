package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.widgets.tables.TablePicker;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/4/11
 */
public class ServerPicker {

    private DefaultCellTable<Server> serverTable;
    private TablePicker<Server> serverSelection;

    private SelectionHandler handler;

    public ServerPicker(SelectionHandler handler) {
        this.handler = handler;
    }

    public Widget asWidget() {

        serverTable = new DefaultCellTable<Server>(5);

        Column<Server, String> nameCol = new Column<Server, String>(new TextCell()) {
            @Override
            public String getValue(Server object) {
                return object.getName();
            }
        };
        serverTable.addColumn(nameCol, "Server");

        serverSelection = new TablePicker(serverTable, new TablePicker.ValueRenderer<Server>() {
            @Override
            public String render(Server selection) {
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
                Server server = ((SingleSelectionModel<Server>) serverTable.getSelectionModel()).getSelectedObject();
                handler.onSelection(server);
            }
        });

        return widget;
    }

    public void setServers(List<Server> servers) {
        serverTable.setRowCount(servers.size(), true);
        serverTable.setRowData(0, servers);
    }

    public void setSelected(Server server, boolean isSelected)
    {
        serverTable.getSelectionModel().setSelected(server, isSelected);
    }

    public interface SelectionHandler {
        void onSelection(Server server);
    }

}
