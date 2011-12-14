package org.jboss.as.console.client.widgets.tree;

import com.google.gwt.user.cellview.client.CellTree;

/**
 * @author Heiko Braun
 * @date 12/14/11
 */
public class DefaultCellTreeStyle implements CellTree.Style {

    @Override
    public String cellTreeEmptyMessage() {
        return "cellTreeEmptyMessage";
    }

    @Override
    public String cellTreeItem() {
        return "cellTreeItem";
    }

    @Override
    public String cellTreeItemImage() {
        return "cellTreeItemImage";
    }

    @Override
    public String cellTreeItemImageValue() {
        return "cellTreeItemImageValue";
    }

    @Override
    public String cellTreeItemValue() {
        return "cellTreeItemValue";
    }

    @Override
    public String cellTreeKeyboardSelectedItem() {
        return "cellTreeKeyboardSelectedItem";
    }

    @Override
    public String cellTreeOpenItem() {
        return "cellTreeOpenItem";
    }

    @Override
    public String cellTreeSelectedItem() {
        return "cellTreeSelectedItem";
    }

    @Override
    public String cellTreeShowMoreButton() {
        return "cellTreeShowMoreButton";
    }

    @Override
    public String cellTreeTopItem() {
        return "cellTreeTopItem";
    }

    @Override
    public String cellTreeTopItemImage() {
        return "cellTreeTopItemImage";
    }

    @Override
    public String cellTreeTopItemImageValue() {
        return "cellTreeTopItemImageValue";
    }

    @Override
    public String cellTreeWidget() {
        return "cellTreeWidget";
    }

    @Override
    public boolean ensureInjected() {
        return true;
    }

    @Override
    public String getText() {
        return "DefaultCellTreeStyle-text";
    }

    @Override
    public String getName() {
        return "DefaultCellTreeStyle";
    }
}
