package org.jboss.as.console.client.domain;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.events.ChangeEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangeHandler;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
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
    private ComboBoxItem groupSelection;
    private NavTreeNode serverGroupNode;

    public ServerGroupSection() {
        super("Server Groups");

        final DynamicForm form = new DynamicForm();
        form.setWidth100();

        groupSelection = new ComboBoxItem();
        groupSelection.setTitle("Group");
        groupSelection.setType("comboBox");
        groupSelection.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                Console.MODULES.getEventBus().fireEvent(new ServerGroupSelectionEvent((String) changeEvent.getValue()));
                groupSelection.blurItem();
            }
        });
        form.setFields(groupSelection);
        form.setLayoutAlign(Alignment.CENTER);

        serverGroupTreeGrid = new NavTreeGrid("Server Groups");
        serverGroupTreeGrid.setEmptyMessage("Please select a server group.");

        serverGroupNode = new NavTreeNode(
                "server-groups", "Server Group", false
        );

        Tree sgTree = new Tree();
        sgTree.setRoot(serverGroupNode);
        serverGroupTreeGrid.setData(sgTree);

        /*ToolStrip toolStrip = new ToolStrip();
        toolStrip.setAlign(Alignment.RIGHT);
        toolStrip.setWidth100();

        ToolStripButton inventoryBtn = new ToolStripButton();
        inventoryBtn.setIcon("common/xs/inventory.png");
        inventoryBtn.setIconWidth(10);
        inventoryBtn.setIconHeight(10);

        ToolStripButton addBtn = new ToolStripButton();
        addBtn.setIcon("common/xs/add.png");
        addBtn.setIconWidth(10);
        addBtn.setIconHeight(10);
        addBtn.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent clickEvent) {
                SC.confirm("Create new Server Group?", new BooleanCallback() {
                    public void execute(Boolean value) {

                    }
                });
            }
        });

        toolStrip.addButton(inventoryBtn);
        toolStrip.addSeparator();
        toolStrip.addButton(addBtn);
        toolStrip.addSpacer(10);

        this.addItem(toolStrip);*/

        NavLabel overviewLabel = new NavLabel("server-groups","Overview");
        overviewLabel.setIcon("common/inventory_grey.png");

        NavLabel createNewLabel = new NavLabel("server-groups;action=new","Create New Group");
        createNewLabel.setIcon("common/add.png");
        this.addItem(overviewLabel);
        this.addItem(createNewLabel);
        this.addItem(new SpacerLabel());
        this.addItem(form);
        this.addItem(serverGroupTreeGrid);
    }

    public void setSelectedServerGroup(ServerGroupRecord serverGroupRecord) {
        serverGroupTreeGrid.getTree().closeAll(serverGroupNode);

        String groupName = serverGroupRecord.getAttribute("group-name");
        TreeNode[] nodes = new TreeNode[] {
                new NavTreeNode("group-jvm;group="+groupName, "JVM"),
                new NavTreeNode("group-sockets;group="+groupName, "Socket Bindings"),
                new NavTreeNode("group-properties;group="+groupName, "System Properties"),
                new NavTreeNode("group-deployments;group="+groupName, "Deployments")
        };

        serverGroupNode.setChildren(nodes);

        serverGroupTreeGrid.markForRedraw();
        serverGroupTreeGrid.getTree().openAll(serverGroupNode);
    }

    public void updateFrom(ServerGroupRecord[] serverGroupRecords) {
        String[] updates = new String[serverGroupRecords.length];
        int i=0;
        for(ServerGroupRecord record : serverGroupRecords)
        {
            updates[i] = record.getAttribute("group-name");
            i++;
        }

        groupSelection.setValueMap(updates);
        serverGroupTreeGrid.markForRedraw();
    }
}
