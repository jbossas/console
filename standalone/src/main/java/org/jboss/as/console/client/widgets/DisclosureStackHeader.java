package org.jboss.as.console.client.widgets;

import com.google.gwt.user.client.ui.DisclosurePanel;
import org.jboss.as.console.client.widgets.icons.Icons;

/**
 * @author Heiko Braun
 * @date 4/4/11
 */
public class DisclosureStackHeader {

    private DisclosurePanel panel;

    public DisclosureStackHeader(String title) {

        panel = new DisclosurePanel(Icons.INSTANCE.stack_opened(), Icons.INSTANCE.stack_closed(), title);
        panel.setOpen(true);
        panel.getElement().setAttribute("style", "width:100%;");
        panel.getHeader().setStyleName("stack-section-header");

    }

    public DisclosurePanel asWidget() {
        return panel;
    }
}
