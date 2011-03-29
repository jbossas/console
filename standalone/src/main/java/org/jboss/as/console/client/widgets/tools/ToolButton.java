package org.jboss.as.console.client.widgets.tools;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

/**
 * @author Heiko Braun
 * @date 2/28/11
 */
public class ToolButton extends Button
{
    public ToolButton(String title) {
        super(title);
        setStyleName("default-button");
    }

    public ToolButton(String title, ClickHandler handler) {
        this(title);
        addClickHandler(handler);
    }
}

