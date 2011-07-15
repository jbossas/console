package org.jboss.as.console.client.shared.general;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.as.console.client.shared.general.model.SocketBinding;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 6/7/11
 */
public class SocketTable {

    private DefaultCellTable<SocketBinding> table;
    private ListDataProvider<SocketBinding> dataProvider;
    private int portOffset = 0;

    public SocketTable() {
    }

    public SocketTable(int portOffset) {
        this.portOffset = portOffset;
    }

    public DefaultCellTable asWidget() {

        table = new DefaultCellTable<SocketBinding>(6);
        dataProvider = new ListDataProvider<SocketBinding>();
        dataProvider.addDataDisplay(table);

        TextColumn<SocketBinding> nameColumn = new TextColumn<SocketBinding>() {
            @Override
            public String getValue(SocketBinding record) {
                return record.getName();
            }
        };

        TextColumn<SocketBinding> portColumn = new TextColumn<SocketBinding>() {
            @Override
            public String getValue(SocketBinding record) {
                if(record.getPort()>0)
                    return String.valueOf(record.getPort()+portOffset);
                else
                    return "";
            }
        };

        TextColumn<SocketBinding> mcastColumn = new TextColumn<SocketBinding>() {
            @Override
            public String getValue(SocketBinding record) {
                if(record.getMultiCastPort()>0)
                    return String.valueOf(record.getMultiCastPort()+portOffset);
                else
                    return "";
            }
        };

        table.addColumn(nameColumn, "Name");
        table.addColumn(portColumn, "Port");
        table.addColumn(mcastColumn, "MCast Port");

        return table;
    }

    public CellTable<SocketBinding> getCellTable() {
        return table;
    }

    public void updateFrom(String groupName, List<SocketBinding> bindings) {
        dataProvider.setList(bindings);
        if(!bindings.isEmpty() && table.getSelectionModel()!=null)
            table.getSelectionModel().setSelected(bindings.get(0), true);
    }
}
