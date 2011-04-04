package org.jboss.as.console.client.server.deployment;

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.widgets.DisclosureStackHeader;
import org.jboss.as.console.client.widgets.LHSNavTree;
import org.jboss.as.console.client.widgets.LHSNavTreeItem;

/**
 * LHS navigation for standalone deployment management.
 *
 * @author Heiko Braun
 * @date 2/10/11
 */
public class LHSDeploymentNavigation {

    private LayoutPanel layout;
    private VerticalPanel stack;

    public LHSDeploymentNavigation () {
        super();

        layout = new LayoutPanel();
        layout.getElement().setAttribute("style", "width:99%;border-right:1px solid #E0E0E0");
        layout.setStyleName("fill-layout");

        DisclosurePanel panel = new DisclosureStackHeader("Deployments").asWidget();

        stack = new VerticalPanel();
        stack.setStyleName("fill-layout-width");

        // ----------------------------------------------------

        Tree deploymentTree = new LHSNavTree("standalone-deployments");

        LHSNavTreeItem current = new LHSNavTreeItem(
                "Current Deployments",
                NameTokens.DeploymentListPresenter
        );

        deploymentTree.addItem(current);
        stack.add(deploymentTree);

        panel.setContent(stack);

        layout.add(panel);
    }

    public Widget asWidget()
    {
        return layout;
    }

}