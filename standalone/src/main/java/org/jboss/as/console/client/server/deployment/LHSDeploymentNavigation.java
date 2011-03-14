package org.jboss.as.console.client.server.deployment;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.widgets.LHSNavItem;
import org.jboss.as.console.client.widgets.StackSectionHeader;
import org.jboss.as.console.client.widgets.icons.Icons;

/**
 * LHS navigation for standalone deployment management.
 *
 * @author Heiko Braun
 * @date 2/10/11
 */
public class LHSDeploymentNavigation {

    private StackLayoutPanel stack;

    public LHSDeploymentNavigation () {
        super();

        stack = new StackLayoutPanel(Style.Unit.PX);
        stack.addStyleName("section-stack");
        stack.setWidth("180");

        // ----------------------------------------------------

        LayoutPanel dplLayout = new LayoutPanel();
        dplLayout.setStyleName("stack-section");

        LHSNavItem current = new LHSNavItem(
                "Available Deployments",
                NameTokens.DeploymentListPresenter,
                Icons.INSTANCE.inventory_small()
        );

        LHSNavItem createNew = new LHSNavItem(
                "Add Deployment",
                "current-deployments;action=new",
                Icons.INSTANCE.add_small()
        );

        addNavItems(dplLayout, current, createNew);

        stack.add(dplLayout, new StackSectionHeader("Deployments"), 28);

    }

    private void addNavItems(LayoutPanel layout, LHSNavItem... items) {
        int i=0;
        int height = 25;
        for(LHSNavItem navItem : items)
        {
            layout.add(navItem);
            layout.setWidgetTopHeight(navItem, i*height, Style.Unit.PX, height, Style.Unit.PX);
            i++;
        }
    }


    public Widget asWidget()
    {
        return stack;
    }

}