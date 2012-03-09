package org.jboss.as.console.client.widgets.nav;

import com.google.gwt.user.client.ui.TreeItem;

public class TreeSection extends TreeItem {


    public TreeSection(String title) {
        setText(title);
        addStyleName("tree-section");
    }

    public TreeSection(String title, boolean first) {
        setText(title);
        addStyleName("tree-section");
        if(first)
        {
            addStyleName("tree-section-first");
            getElement().setAttribute("tabindex", "-1");
        }
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if(selected)
            addStyleName("tree-section-selected");
        else
            removeStyleName("tree-section-selected");
    }


}
