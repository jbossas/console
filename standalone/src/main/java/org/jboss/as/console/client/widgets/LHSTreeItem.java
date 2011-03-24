package org.jboss.as.console.client.widgets;

import com.google.gwt.user.client.ui.TreeItem;

/**
 * @author Heiko Braun
 * @date 3/24/11
 */
public class LHSTreeItem extends TreeItem {
    public LHSTreeItem(String text, String token) {
        setText(text);
        setStyleName("lhs-tree-item");
        getElement().setAttribute("token", token);
    }

    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if(selected)
            addStyleName("lhs-tree-item-selected");
        else
            removeStyleName("lhs-tree-item-selected");
    }
}
