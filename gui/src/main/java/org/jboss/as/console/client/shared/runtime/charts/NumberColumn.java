package org.jboss.as.console.client.shared.runtime.charts;

import com.google.gwt.visualization.client.AbstractDataTable;

/**
 * @author Heiko Braun
 * @date 11/3/11
 */
public class NumberColumn extends Column<Integer> {

    public NumberColumn( String label) {
        super(AbstractDataTable.ColumnType.NUMBER, label);
    }

    @Override
    Integer cast(String value) {
        return Integer.valueOf(value).intValue();
    }
}
