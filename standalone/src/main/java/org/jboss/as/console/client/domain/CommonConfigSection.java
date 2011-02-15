package org.jboss.as.console.client.domain;

import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeNode;
import org.jboss.as.console.client.components.NavTreeGrid;
import org.jboss.as.console.client.components.NavTreeNode;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
class CommonConfigSection extends SectionStackSection{
    public CommonConfigSection() {

        super("General Configuration");

        NavTreeGrid commonGrid = new NavTreeGrid("Deployments");
        final TreeNode commonNode = new NavTreeNode(
                "common", "Common Settings", true,
                new NavTreeNode("paths", "Paths"),
                new NavTreeNode("interfaces", "Interfaces"),
                new NavTreeNode("sockets", "Socket Binding Groups"),
                new NavTreeNode("properties", "System Properties")
        );

        Tree commonTree = new Tree();
        commonTree.setRoot(commonNode);
        commonGrid.setData(commonTree);

        this.addItem(commonGrid);
    }
}
