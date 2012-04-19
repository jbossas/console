package org.jboss.as.console.client.shared.general;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import org.jboss.as.console.client.shared.expr.ExpressionColumn;
import org.jboss.as.console.client.shared.general.model.SocketBinding;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

import java.util.Comparator;
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
        table = new DefaultCellTable<SocketBinding>(8, new ProvidesKey<SocketBinding>() {
            @Override
            public Object getKey(SocketBinding item) {
                return item.getName()+String.valueOf(item.getGroup());
            }
        });
        dataProvider = new ListDataProvider<SocketBinding>();
        dataProvider.addDataDisplay(table);
    }

    public DefaultCellTable asWidget() {

        ColumnSortEvent.ListHandler<SocketBinding> sortHandler =
                new ColumnSortEvent.ListHandler<SocketBinding>(dataProvider.getList());

        Column<SocketBinding, String> nameColumn = new Column<SocketBinding, String>(new TextCell()) {
            {
                setFieldUpdater(new FieldUpdater<SocketBinding, String>() {

                    @Override
                    public void update(int index, SocketBinding object, String value) {
                        object.setName(value);
                    }
                });
            }

            @Override
            public String getValue(SocketBinding object) {
                return object.getName();
            }
        };

        nameColumn.setSortable(true);
        sortHandler.setComparator(nameColumn, new Comparator<SocketBinding>() {
            @Override
            public int compare(SocketBinding o1, SocketBinding o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });


        ExpressionColumn<SocketBinding> portColumn = new ExpressionColumn<SocketBinding>("port") {
            @Override
            public String getRealValue(SocketBinding record) {
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

        table.addColumnSortHandler(sortHandler);
        table.getColumnSortList().push(nameColumn);

        return table;
    }

    public CellTable<SocketBinding> getCellTable() {
        return table;
    }

    public void updateFrom(String groupName, List<SocketBinding> bindings) {

        List<SocketBinding> list = dataProvider.getList();
        list.clear(); // cannot call setList() as that breaks the sort handler
        list.addAll(bindings);
        dataProvider.flush();

        table.selectDefaultEntity();
    }

     public void updateFrom(String groupName, List<SocketBinding> bindings, int portOffset) {

         this.portOffset = portOffset;
         updateFrom(groupName, bindings);
    }
}
