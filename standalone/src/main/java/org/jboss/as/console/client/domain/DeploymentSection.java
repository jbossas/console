package org.jboss.as.console.client.domain;

import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeNode;
import org.jboss.as.console.client.components.NavTreeNode;
import org.jboss.as.console.client.components.NavigationTreeGrid;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
class DeploymentSection extends SectionStackSection {
    public DeploymentSection() {
        super("Deployments");

        final NavigationTreeGrid deploymentGrid = new NavigationTreeGrid("Deployments");

        final TreeNode deploymentNode = new NavTreeNode(
                "domain-deployments", "Domain Deployments",false,
                new NavTreeNode("domain-deployments;type=web", "Web Applications"),
                new NavTreeNode("domain-deployments;type=ee", "Enterprise Applications"),
                new NavTreeNode("domain-deployments;type=rar", "Resource Adapters"),
                new NavTreeNode("domain-deployments;type=other", "Other")

        );
        Tree deploymentTree = new Tree();
        deploymentTree.setRoot(deploymentNode);
        deploymentGrid.setData(deploymentTree);

        this.addItem(deploymentGrid);

    }
}
