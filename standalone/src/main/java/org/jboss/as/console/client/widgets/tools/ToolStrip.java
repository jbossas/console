package org.jboss.as.console.client.widgets.tools;

import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author Heiko Braun
 * @date 2/28/11
 */
public class ToolStrip extends HTMLPanel {

    private String ref = createUniqueId();


    public ToolStrip() {
        super("");
        getElement().setInnerHTML("<div id='"+ref+"' class='default-toolstrip'>");
    }

    public void addToolButton(ToolButton button)
    {
        add(button, ref);
    }

    public void addToolButtonRight(ToolButton button)
    {
        button.getElement().setAttribute("style", "float:right;border-color:#cccccc;");
        add(button, ref);

    }
}
