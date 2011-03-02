package org.jboss.as.console.client.widgets;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Heiko Braun
 * @date 3/2/11
 */
public class SimpleLHSSection {
    protected LayoutPanel layout;

    public SimpleLHSSection() {
        layout = new LayoutPanel();
        layout.setStyleName("stack-section");
    }

    protected void addNavItems(LHSNavItem... items) {
        int i=0;
        int height = 25;
        for(LHSNavItem navItem : items)
        {
            layout.add(navItem);
            layout.setWidgetTopHeight(navItem, i*height, Style.Unit.PX, height, Style.Unit.PX);
            i++;
        }
    }

    public Widget asWidget() {
        return layout;
    }
}
