package org.jboss.as.console.client.server.subsys.threads;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.LayoutPanel;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;

import java.util.Collections;

/**
 * @author Heiko Braun
 * @date 2/10/11
 */
class ThreadFactoryList extends LayoutPanel {

    public ThreadFactoryList(final ThreadManagementPresenter presenter) {

        DefaultCellTable factoryTable = new DefaultCellTable(10);

        TextColumn<ThreadFactoryRecord> nameColumn = new TextColumn<ThreadFactoryRecord>() {
            @Override
            public String getValue(ThreadFactoryRecord record) {
                return record.getName();
            }
        };

        TextColumn<ThreadFactoryRecord> groupColumn = new TextColumn<ThreadFactoryRecord>() {
            @Override
            public String getValue(ThreadFactoryRecord record) {
                return record.getGroup();
            }
        };

        TextColumn<ThreadFactoryRecord> prioColumn = new TextColumn<ThreadFactoryRecord>() {
            @Override
            public String getValue(ThreadFactoryRecord record) {
                return String.valueOf(record.getPriority());
            }
        };

        factoryTable.addColumn(nameColumn, "Factory Name");
        factoryTable.addColumn(groupColumn, "Group");
        factoryTable.addColumn(prioColumn, "Priority");

        factoryTable.setRowData(0, Collections.EMPTY_LIST);
        add(factoryTable);
        setWidgetTopHeight(factoryTable, 0, Style.Unit.PX, 100, Style.Unit.PCT);
    }
}
