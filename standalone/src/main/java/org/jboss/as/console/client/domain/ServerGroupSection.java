package org.jboss.as.console.client.domain;

import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.tree.Tree;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.components.NavLabel;
import org.jboss.as.console.client.components.NavTreeGrid;
import org.jboss.as.console.client.components.NavTreeNode;
import org.jboss.as.console.client.components.SpacerLabel;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.util.Places;

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

        serverGroupTreeGrid.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent clickEvent) {
                final NavTreeNode selectedRecord = (NavTreeNode) serverGroupTreeGrid.getSelectedRecord();

                Console.MODULES.getPlaceManager().revealPlaceHierarchy(
                        Places.fromString(selectedRecord.getName())
                );
            }
        });

        serverGroupNode = new NavTreeNode("domain/server-group", "Server Group", false);

        Tree serverGroupTree = new Tree();
        serverGroupTree.setRoot(serverGroupNode);
        serverGroupTreeGrid.setData(serverGroupTree);

        //NavLabel overviewLabel = new NavLabel("domain/server-groups","Overview");
        //overviewLabel.setIcon("common/inventory_grey.png");

        NavLabel createNewLabel = new NavLabel("domain/server-group;action=new","Add Server Group");
        createNewLabel.setIcon("common/add.png");
        //this.addItem(overviewLabel);
        this.addItem(createNewLabel);
        this.addItem(new SpacerLabel());

        this.addItem(serverGroupTreeGrid);
    }

    public void updateFrom(ServerGroupRecord[] serverGroupRecords) {

        serverGroupTreeGrid.getTree().closeAll(serverGroupNode);

        NavTreeNode[] nodes = new NavTreeNode[serverGroupRecords.length];

        int i=0;
        for(ServerGroupRecord record : serverGroupRecords)
        {
            String groupName = record.getAttribute("group-name");
            nodes[i] = new NavTreeNode("domain/server-group;name="+ groupName, groupName);
            i++;
        }

        NavTreeNode folder = new NavTreeNode("", "Current Groups");
        folder.setChildren(nodes);

        serverGroupNode.setChildren(new NavTreeNode[] {folder});

        serverGroupTreeGrid.markForRedraw();
        serverGroupTreeGrid.getTree().openAll(serverGroupNode);
    }
}
