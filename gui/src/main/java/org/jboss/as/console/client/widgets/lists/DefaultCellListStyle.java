package org.jboss.as.console.client.widgets.lists;

import com.google.gwt.user.cellview.client.CellList;

/**
 * @author Heiko Braun
 * @date 12/14/11
 */
public class DefaultCellListStyle implements CellList.Style {
    @Override
    public String cellListEvenItem() {
        return "cellListEvenItem";
    }

    @Override
    public String cellListKeyboardSelectedItem() {
        return "cellListKeyboardSelectedItem";
    }

    @Override
    public String cellListOddItem() {
        return "cellListOddItem";
    }

    @Override
    public String cellListSelectedItem() {
        return "cellListSelectedItem";
    }

    @Override
    public String cellListWidget() {
        return "cellListWidget";
    }

    @Override
    public boolean ensureInjected() {
        return true;
    }

    @Override
    public String getText() {
        return "DefaultCellListStyle-Text";
    }

    @Override
    public String getName() {
        return "DefaultCellListStyle";
    }
}
