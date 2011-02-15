package org.jboss.as.console.client.server;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.components.NavTreeGrid;
import org.jboss.as.console.client.components.NavTreeNode;
import org.jboss.as.console.client.components.sgwt.NavigationSection;
import org.jboss.as.console.client.shared.SubsystemRecord;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LHS navigation for standalone server management.
 *
 * @author Heiko Braun
 * @date 2/10/11
 */
public class LHSServerNavigation {

    private Map<String, TreeGrid> treeGrids = new LinkedHashMap<String, TreeGrid>();
    private Map<String, NavigationSection> sectionsByName;
    private SectionStack sectionStack;

    private NavTreeGrid subsysTreeGrid;
    private NavTreeNode subsysNode;

    public LHSServerNavigation() {
        super();


        sectionStack = new SectionStack();
        sectionStack.addStyleName("lhs-section-stack");
        sectionStack.setShowResizeBar(true);
        sectionStack.setVisibilityMode(VisibilityMode.MULTIPLE);
        sectionStack.setWidth(220);
        sectionStack.setHeight100();

        // ----------------------------------------------------

        subsysTreeGrid = new NavTreeGrid("subsys");

        subsysTreeGrid.addCellClickHandler(new CellClickHandler() {
            @Override
            public void onCellClick(CellClickEvent event) {
                final NavTreeNode selectedRecord = (NavTreeNode) subsysTreeGrid.getSelectedRecord();

                Console.MODULES.getPlaceManager().revealPlaceHierarchy(
                        new ArrayList<PlaceRequest>() {{
                            add(new PlaceRequest("server"));
                            add(new PlaceRequest(selectedRecord.getName()));
                        }}
                );
            }
        });

        subsysNode = new NavTreeNode( "subsystems", false);
        Tree profileTree = new Tree();
        profileTree.setRoot(subsysNode);

        subsysTreeGrid.setData(profileTree);
        subsysTreeGrid.getTree().openAll();

        SectionStackSection profileSection = new SectionStackSection("Profile");
        profileSection.setExpanded(true);
        profileSection.addItem(subsysTreeGrid);

        sectionStack.addSection(profileSection);

        // ----------------------------------------------------

        final NavTreeGrid deploymentGrid = new NavTreeGrid("Deployments");

        deploymentGrid.addCellClickHandler(new CellClickHandler() {
            @Override
            public void onCellClick(CellClickEvent event) {
                final NavTreeNode selectedRecord =
                        (NavTreeNode) deploymentGrid.getSelectedRecord();

                // TODO: why does revealPlaceHierarchy() not work?
                History.newItem("server/"+selectedRecord.getName());
            }
        });


        final TreeNode deploymentNode = new NavTreeNode(
                "server-deployments", "Server Deployments",false,
                new NavTreeNode("server-deployments;type=web", "Web Applications"),
                new NavTreeNode("server-deployments;type=ee", "Enterprise Applications"),
                new NavTreeNode("server-deployments;type=rar", "Resource Adapters"),
                new NavTreeNode("server-deployments;type=other", "Other")

        );
        Tree deploymentTree = new Tree();
        deploymentTree.setRoot(deploymentNode);
        deploymentGrid.setData(deploymentTree);


        SectionStackSection deploymentSection = new SectionStackSection("Deployments");
        deploymentSection.setExpanded(true);
        deploymentSection.addItem(deploymentGrid);

        sectionStack.addSection(deploymentSection);

        // ----------------------------------------------------

        NavTreeGrid commonGrid = new NavTreeGrid("Common");
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

        SectionStackSection commonSection = new SectionStackSection("General Configuration");
        commonSection.addItem(commonGrid);

        sectionStack.addSection(commonSection);


    }

    public Widget asWidget()
    {
        return sectionStack;
    }

    public void updateFrom(SubsystemRecord[] subsystems) {

        subsysTreeGrid.getTree().closeAll(subsysNode);

        TreeNode[] nodes = new TreeNode[subsystems.length];
        int i = 0;
        for(SubsystemRecord subsys: subsystems)
        {
            nodes[i] = new NavTreeNode(subsys.getToken(), subsys.getTitle());
            i++;
        }

        subsysNode.setChildren(nodes);

        subsysTreeGrid.markForRedraw();
        subsysTreeGrid.getTree().openAll(subsysNode);
    }

    /*
   private void highlightTool(String sectionName, String pageName)
   {
       for (String name : treeGrids.keySet()) {
           TreeGrid treeGrid = treeGrids.get(name);
           if (!name.equals(sectionName)) {
               treeGrid.deselectAllRecords();
           } else {
               Tree tree = treeGrid.getTree();
               TreeNode node = tree.find(sectionName + "/" + pageName);
               if (node != null) {
                   treeGrid.selectSingleRecord(node);
               } else {
                   Log.error("Unknown page: " + sectionName + "/" + pageName);
               }
           }
       }
   } */
}