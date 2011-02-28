package org.jboss.as.console.client.widgets.tables;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.dom.client.Node;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

import java.util.List;

/**
 * Default cell table (styles).
 *
 * @author Heiko Braun
 * @date 2/22/11
 */
public class DefaultCellTable<T> extends CellTable {

    private static final DefaultCellTableResources DEFAULT_CELL_TABLE_RESOURCES =
            new DefaultCellTableResources();
    private static final String CELLTABLE_EMPTY_DIV = "celltable-empty-div";

    private boolean state = false;

    public DefaultCellTable(int pageSize) {

        super(pageSize, DEFAULT_CELL_TABLE_RESOURCES);
        setStyleName("default-cell-table");
        setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.ENABLED);
    }

    @Override
    public void setRowCount(int size, boolean isExact) {
        super.setRowCount(size, isExact);
    }

    @Override
    public void setRowData(int start, List values) {
        setEmpty(values.isEmpty());
        super.setRowData(start, values);
    }

    private void setEmpty(boolean isEmpty)
    {
        if(state == isEmpty) return;

        if(isEmpty)
        {
            Element div = DOM.createDiv();
            div.setId(CELLTABLE_EMPTY_DIV);
            div.setInnerHTML("No records!");
            div.setAttribute("style", "text-align:center;width:100%; height:50px; color:#cccccc; padding-top:22px;");
            getElement().appendChild(div);
        }
        else
        {
            Node lastChild = getElement().getLastChild();

            if(lastChild.getNodeType() == Node.ELEMENT_NODE)
            {
                com.google.gwt.dom.client.Element el = (com.google.gwt.dom.client.Element)lastChild;
                if(el.getId().equals(CELLTABLE_EMPTY_DIV))
                   DOM.removeChild(getElement(), (Element)el);
            }

        }

        this.state = isEmpty;
    }

    public void setEnabled(boolean b)
    {
        for(int i=0; i<getColumnCount(); i++)
        {
            Cell cell = getColumn(i).getCell();
            if(cell instanceof DefaultEditTextCell)
            {
                DefaultEditTextCell defaultCell = (DefaultEditTextCell)cell;
                defaultCell.setEnabled(b);
            }
        }

    }
}
