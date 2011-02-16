package org.jboss.as.console.client.domain;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.events.ChangeEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangeHandler;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeNode;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.components.NavLabel;
import org.jboss.as.console.client.components.NavTreeGrid;
import org.jboss.as.console.client.components.NavTreeNode;
import org.jboss.as.console.client.components.SpacerLabel;
import org.jboss.as.console.client.domain.events.ServerGroupSelectionEvent;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
class ServerGroupSection extends SectionStackSection{

    private NavTreeGrid serverGroupTreeGrid ;
    private NavTreeNode serverGroupNode;

    public ServerGroupSection() {
        super("Server Groups");

        serverGroupTreeGrid = new NavTreeGrid("Server Groups");
        serverGroupTreeGrid.setEmptyMessage("Please select a server group.");
        serverGroupNode = new NavTreeNode("server-groups", "Server Group", false);

        Tree serverGroupTree = new Tree();
        serverGroupTree.setRoot(serverGroupNode);
        serverGroupTreeGrid.setData(serverGroupTree);

        NavLabel overviewLabel = new NavLabel("server-groups","Overview");
        overviewLabel.setIcon("common/inventory_grey.png");

        NavLabel createNewLabel = new NavLabel("server-groups;action=new","Create New Group");
        createNewLabel.setIcon("common/add.png");
        this.addItem(overviewLabel);
        this.addItem(createNewLabel);
        this.addItem(new SpacerLabel());

        this.addItem(serverGroupTreeGrid);
    }

    public void setSelectedServerGroup(ServerGroupRecord serverGroupRecord) {
        /*serverGroupTreeGrid.getTree().closeAll(serverGroupNode);

        String groupName = serverGroupRecord.getAttribute("group-name");
        TreeNode[] nodes = new TreeNode[] {
                new NavTreeNode("group-jvm;group="+groupName, "JVM"),
                new NavTreeNode("group-sockets;group="+groupName, "Socket Bindings"),
                new NavTreeNode("group-properties;group="+groupName, "System Properties"),
                new NavTreeNode("group-deployments;group="+groupName, "Deployments")
        };

        serverGroupNode.setChildren(nodes);

        serverGroupTreeGrid.markForRedraw();
        serverGroupTreeGrid.getTree().openAll(serverGroupNode);*/
    }

    public void updateFrom(ServerGroupRecord[] serverGroupRecords) {

        serverGroupTreeGrid.getTree().closeAll(serverGroupNode);

        TreeNode[] nodes = new TreeNode[serverGroupRecords.length];

        int i=0;
        for(ServerGroupRecord record : serverGroupRecords)
        {
            String groupName = record.getAttribute("group-name");
            nodes[i] = new NavTreeNode("server-group;name="+ groupName.toLowerCase(),groupName);
            i++;
        }

        NavTreeNode folder = new NavTreeNode("", "Current Groups");
        folder.setChildren(nodes);

        serverGroupNode.setChildren(new NavTreeNode[] {folder});

        serverGroupTreeGrid.markForRedraw();
        serverGroupTreeGrid.getTree().openAll(serverGroupNode);
    }
}
