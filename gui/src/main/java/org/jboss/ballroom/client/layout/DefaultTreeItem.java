package org.jboss.ballroom.client.layout;

import com.google.gwt.user.client.ui.TreeItem;

/**
 * @author Heiko Braun
 * @date 11/9/11
 */
public class DefaultTreeItem extends TreeItem {

    public DefaultTreeItem(String title) {
        super();

        setText(title);
        getElement().setAttribute("style", "cursor:pointer;");

        /*html.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                setState(!getState());
            }
        });
        setWidget(html);*/
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
