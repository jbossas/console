package org.jboss.as.console.client.shared.viewframework.builder;

import com.google.gwt.user.cellview.client.CellTable;

class NamedTable<T> {
    String title;
    CellTable<T> widget;

    NamedTable(String title, CellTable<T> table) {
        this.title = title;
        this.widget = table;
    }
}
