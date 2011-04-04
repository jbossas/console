package org.jboss.as.console.client.domain.groups;

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.widgets.DisclosureStackHeader;
import org.jboss.as.console.client.widgets.LHSNavTree;
import org.jboss.as.console.client.widgets.LHSNavTreeItem;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
class DeploymentSection {

    private DisclosurePanel panel;
    private LHSNavTree deploymentTree;

    public DeploymentSection() {

        panel = new DisclosureStackHeader("Deployments").asWidget();
        deploymentTree = new LHSNavTree("groups");
        panel.setContent(deploymentTree);

        LHSNavTreeItem current = new LHSNavTreeItem( "Current Deployments", NameTokens.DeploymentsPresenter);
        //LHSNavTreeItem plans= new LHSNavTreeItem("Deployment Plans", "deployment-plans");

        deploymentTree.addItem(current);
        //deploymentTree.addItem(plans);

    }


    public Widget asWidget() {
        return panel;
    }
}
