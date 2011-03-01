package org.jboss.as.console.client.widgets.tables;

import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.view.client.ListDataProvider;

/**
 * Treats the last cell as an OptionCell and displays it upon row rollover.
 *
 * @author Heiko Braun
 * @date 3/1/11
 */
public class DefaultOptionRolloverHandler implements DefaultCellTable.RowOverHandler {

    final ListDataProvider<?> provider;
    final DefaultCellTable<?> table;

    public DefaultOptionRolloverHandler(ListDataProvider<?> provider, DefaultCellTable<?> table) {
        this.provider = provider;
        this.table = table;
    }

    @Override
    public void onRowOver(int rowNum) {

        if(!table.isEnabled()) return;

        // toggle rollover tools
        if(rowNum<provider.getList().size())
        {
            TableCellElement rollOverItem = table.getRowElement(rowNum).getCells().getItem(2);
            rollOverItem.getFirstChildElement().getFirstChildElement().addClassName("row-tools-enabled");
        }
    }

    @Override
    public void onRowOut(int rowNum) {

        if(!table.isEnabled()) return;

        if(rowNum<provider.getList().size())
        {
            TableCellElement rollOverItem = table.getRowElement(rowNum).getCells().getItem(2);
            rollOverItem.getFirstChildElement().getFirstChildElement().removeClassName("row-tools-enabled");
        }
    }
}