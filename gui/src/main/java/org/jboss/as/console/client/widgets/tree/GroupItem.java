package org.jboss.as.console.client.widgets.tree;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TreeItem;

public class GroupItem extends TreeItem {

    public GroupItem(String title) {
        super();

        setText(title);
        getElement().setAttribute("style", "cursor:pointer;");

        HTMLPanel inner = new HTMLPanel("<span>"+title+"</span>")
        {
            {
                sinkEvents(Event.ONKEYDOWN);
                sinkEvents(Event.ONMOUSEDOWN);

            }
            @Override
            public void onBrowserEvent(Event event) {
                GroupItem.this.setState(!GroupItem.this.getState());
                event.preventDefault();
                event.stopPropagation();
            }
        };

        setWidget(inner);
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
