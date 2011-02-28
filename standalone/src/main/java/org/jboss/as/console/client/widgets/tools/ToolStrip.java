package org.jboss.as.console.client.widgets.tools;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;

/**
 * @author Heiko Braun
 * @date 2/28/11
 */
public class ToolStrip extends LayoutPanel {


    private HorizontalPanel innerPanel;

    public ToolStrip() {
        super();
        setStyleName("default-toolstrip");

        innerPanel = new HorizontalPanel();
        add(innerPanel);
        innerPanel.getElement().setAttribute("style", "float:right");

        setWidgetTopHeight(innerPanel, 0, Style.Unit.PX, 25, Style.Unit.PX);
    }

    public void addToolButton(ToolButton button)
    {
        innerPanel.add(button);
        button.getElement().getParentElement().setAttribute("style", "vertical-align:middle");
        innerPanel.add(new HTML("&nbsp;"));
    }
}
