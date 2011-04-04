package org.jboss.as.console.client.widgets;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Heiko Braun
 * @date 3/2/11
 */
public class SimpleLHSSection {
    protected VerticalPanel layout;

    public SimpleLHSSection() {
        layout = new VerticalPanel();
        layout.setStyleName("stack-section");
    }

    protected void addNavItems(LHSNavItem... items) {
        int i=0;
        int height = 25;
        for(LHSNavItem navItem : items)
        {
            layout.add(navItem);
            i++;
        }
    }

    public Widget asWidget() {
        return layout;
    }
}
