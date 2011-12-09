package org.jboss.as.console.client.shared.runtime.charts;

import com.google.gwt.visualization.client.AbstractDataTable;

/**
 * @author Heiko Braun
 * @date 11/3/11
 */
public class NumberColumn extends Column<Integer> {

    @Deprecated
    public NumberColumn(String label) {
        super(AbstractDataTable.ColumnType.NUMBER, label);
    }

    public NumberColumn(String detypedName, String label) {
        super(AbstractDataTable.ColumnType.NUMBER, label);
        setDeytpedName(detypedName);
    }

    @Override
    Integer cast(String value) {
        return Integer.valueOf(value).intValue();
    }
}
