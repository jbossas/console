package org.jboss.as.console.client.domain;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.components.LHSNavItem;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
class DeploymentSection {

    private LayoutPanel layout;

    public DeploymentSection() {

        layout = new LayoutPanel();
        layout.setStyleName("stack-section");

        LHSNavItem webApps = new LHSNavItem("Web Applications", "domain-deployments;type=web");
        LHSNavItem eeApps = new LHSNavItem("Enterprise Applications", "domain-deployments;type=ee");
        LHSNavItem resourceAdapters = new LHSNavItem("Resource Adapters", "domain-deployments;type=jca");
        LHSNavItem other = new LHSNavItem("Other", "domain-deployments;type=other");

        addNavItems(webApps, eeApps, resourceAdapters, other);
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
