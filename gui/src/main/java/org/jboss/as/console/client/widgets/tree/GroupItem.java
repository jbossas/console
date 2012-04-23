package org.jboss.as.console.client.widgets.tree;

import com.google.gwt.user.client.ui.TreeItem;

public class GroupItem extends TreeItem {

    public GroupItem(String title) {
        super();

        setText(title);
        getElement().setAttribute("style", "cursor:pointer;");
    }

    @Override
    public void setState(boolean open) {
        super.setState(open);

        String text = open ? "open " : "close ";
        System.out.println(text+getText());
    }
}
