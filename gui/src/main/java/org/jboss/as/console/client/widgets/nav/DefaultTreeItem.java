package org.jboss.as.console.client.widgets.nav;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * @author Heiko Braun
 * @date 11/9/11
 */
public class DefaultTreeItem extends TreeItem {

    public DefaultTreeItem(String title) {
        super();

        HTML html = new HTML(title);
        html.getElement().setAttribute("style", "cursor:pointer;");

        html.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                setState(!getState());
            }
        });
        setWidget(html);
    }
}
