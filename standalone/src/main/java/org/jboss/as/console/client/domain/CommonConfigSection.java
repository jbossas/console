package org.jboss.as.console.client.domain;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import org.jboss.as.console.client.components.LHSNavItem;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
class CommonConfigSection extends SectionStackSection{
    private LayoutPanel layout;

    public CommonConfigSection() {

        layout = new LayoutPanel();
        layout.setStyleName("stack-section");

        LHSNavItem paths = new LHSNavItem("Paths", "domain/paths");
        LHSNavItem interfaces = new LHSNavItem("Interfaces", "domain/domain-interfaces");
        LHSNavItem sockets = new LHSNavItem("Socket Binding Groups", "domain/socket-bindings");
        LHSNavItem properties = new LHSNavItem("System Properties", "domain/domain-properties");

        addNavItems(paths, interfaces, sockets, properties);
    }

    private void addNavItems(LHSNavItem... items) {
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
