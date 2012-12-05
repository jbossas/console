package org.jboss.as.console.client.widgets.tree;

import com.google.gwt.user.client.ui.TreeItem;

public class GroupItem extends TreeItem {

    public GroupItem(String title) {
        super();

        setText(title);
        getElement().setAttribute("style", "cursor:pointer;");
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
    }
}
