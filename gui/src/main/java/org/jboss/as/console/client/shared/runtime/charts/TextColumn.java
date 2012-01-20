package org.jboss.as.console.client.shared.runtime.charts;

import com.google.gwt.visualization.client.AbstractDataTable;

/**
 * @author Heiko Braun
 * @date 1/19/12
 */
public class TextColumn extends Column<String> {

    public TextColumn(String detypedName, String label) {
        super(AbstractDataTable.ColumnType.STRING, label);
        setDeytpedName(detypedName);
    }

    @Override
    String cast(String value) {
        return value;
    }
}
