package org.jboss.as.console.client.domain.groups;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.widgets.LHSNavItem;
import org.jboss.as.console.client.widgets.icons.Icons;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
class DeploymentSection {

    private LayoutPanel layout;

    public DeploymentSection() {

        layout = new LayoutPanel();
        layout.setStyleName("stack-section");

        LHSNavItem current = new LHSNavItem(
                "Current Deployments",
                NameTokens.DeploymentsPresenter,
                Icons.INSTANCE.inventory_small()
        );

        LHSNavItem createNew = new LHSNavItem(
                "New Deployment",
                "current-deployments;action=new",
                Icons.INSTANCE.add_small()
        );

        LHSNavItem plans= new LHSNavItem("Deployment Plans", "deployment-plans");

        addNavItems(current, createNew, plans);
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
