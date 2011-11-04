package org.jboss.as.console.client.widgets.nav;

import com.google.gwt.user.client.ui.DisclosurePanel;
import org.jboss.ballroom.client.widgets.icons.Icons;

/**
 * @author Heiko Braun
 * @date 11/4/11
 */
public class NavPanel {

    private DisclosurePanel panel;

    public NavPanel(String title) {

        panel = new DisclosurePanel(Icons.INSTANCE.stack_opened(), Icons.INSTANCE.stack_closed(), title);
        panel.setOpen(true);
        panel.getElement().setAttribute("style", "width:100%;");
        panel.getHeader().setStyleName("stack-section-header");
        panel.setWidth("100%"); // IE 7

    }

    public DisclosurePanel asWidget() {
        return panel;
    }

    public void setHighlight(boolean b) {
        if(b)
            panel.getHeader().addStyleName("nav-panel-highlight");
        else
            panel.getHeader().removeStyleName("nav-panel-highlight");

    }
}