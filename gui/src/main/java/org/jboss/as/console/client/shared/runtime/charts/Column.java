package org.jboss.as.console.client.shared.runtime.charts;

import com.google.gwt.visualization.client.AbstractDataTable;

/**
 * @author Heiko Braun
 * @date 11/3/11
 */
public abstract class Column<T> {

    protected AbstractDataTable.ColumnType type;
    protected String label;
    protected Column comparisonColumn = null;
    protected String deytpedName;
    protected boolean isVisible = true;
    private boolean isBaseline;

    public Column(AbstractDataTable.ColumnType type, String label) {
        this.type = type;
        this.label = label;
    }

    public String getDeytpedName() {
        return deytpedName;
    }

    public void setDeytpedName(String deytpedName) {
        this.deytpedName = deytpedName;
    }

    public AbstractDataTable.ColumnType getType() {
        return type;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public Column setVisible(boolean b) {
        this.isVisible = b;
        return this;
    }

    public Column setBaseline(boolean b) {
        this.isBaseline = b;
        return this;
    }

    public boolean isBaseline() {
        return isBaseline;
    }

    public String getLabel() {
        return label;
    }

    abstract T cast(String value);

    public Column getComparisonColumn() {
        return comparisonColumn;
    }

    public Column setComparisonColumn(Column comparisonColumn) {
        this.comparisonColumn = comparisonColumn;
        return this;
    }


}
