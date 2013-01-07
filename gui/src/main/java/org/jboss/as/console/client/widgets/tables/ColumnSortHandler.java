package org.jboss.as.console.client.widgets.tables;

import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;

import java.util.*;

/**
 * This handler is similar to ColumnSortEvent.ListHandler except that it allows the list to be set after construction
 */
public class ColumnSortHandler<T> implements ColumnSortEvent.Handler {

    private final Map<Column<?, ?>, Comparator<T>> comparators = new HashMap<Column<?, ?>, Comparator<T>>();
    private List<T> list;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public void onColumnSort(ColumnSortEvent event) {
        // Get the sorted column.
        Column<?, ?> column = event.getColumn();
        if (column == null) {
            return;
        }

        // Get the comparator.
        final Comparator<T> comparator = comparators.get(column);
        if (comparator == null) {
            return;
        }

        // Sort using the comparator.
        if (event.isSortAscending()) {
            Collections.sort(list, comparator);
        } else {
            Collections.sort(list, new Comparator<T>() {
                public int compare(T o1, T o2) {
                    return -comparator.compare(o1, o2);
                }
            });
        }
    }

    public void setComparator(Column<T, ?> column, Comparator<T> comparator) {
        comparators.put(column, comparator);
    }
}
