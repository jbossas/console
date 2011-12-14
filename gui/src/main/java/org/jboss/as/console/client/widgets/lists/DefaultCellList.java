package org.jboss.as.console.client.widgets.lists;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.user.cellview.client.CellList;

/**
 * @author Heiko Braun
 * @date 12/14/11
 */
public class DefaultCellList<T> extends CellList {

    private final static DefaultCellListResources RESOURCES = new DefaultCellListResources();

    public DefaultCellList(Cell cell) {
        super(cell, RESOURCES);
    }
}
