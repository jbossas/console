package org.jboss.as.console.client.components;

import com.google.gwt.user.client.History;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;

/**
 * @author Heiko Braun
 * @date 2/16/11
 */
public class NavLabel extends Label {

    public NavLabel(final String token, String title) {
        super(title);
        //setContents(title);
        setStyleName("lhs-label");
        setHeight(25);

        addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                History.newItem(token);
            }
        });
    }

    public void highlight(boolean enabled)
    {
        if(enabled)
            addStyleName("lhs-label-selected");
    }


}
