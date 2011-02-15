package org.jboss.as.console.client.server;

import com.google.gwt.user.client.ui.Widget;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;
import org.jboss.as.console.client.components.NavTreeGrid;
import org.jboss.as.console.client.components.NavTreeNode;
import org.jboss.as.console.client.components.sgwt.NavigationSection;
import org.jboss.as.console.client.shared.SubsystemRecord;

import java.util.LinkedHashMap;
import java.util.Map;

/**
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

        subsysTreeGrid = new NavTreeGrid("profile");

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
            nodes[i] = new NavTreeNode(subsys.getName(), subsys.getTitle());
            i++;
        }

        subsysNode.setChildren(nodes);

        subsysTreeGrid.markForRedraw();
        subsysTreeGrid.getTree().openAll(subsysNode);
    }

    /*protected TreeGrid buildTreeGridForSection(NavigationSection navigationSection) {
       final TreeGrid treeGrid = new TreeGrid();
       treeGrid.addStyleName("lhs-treeGrid");
       treeGrid.setTitle(navigationSection.getTitle());
       treeGrid.setLeaveScrollbarGap(false);
       treeGrid.setShowHeader(false);

       List<NavigationItem> navigationItems = navigationSection.getNavigationItems();
       TreeNode[] treeNodes = new TreeNode[navigationItems.size()];
       for (int i = 0, navigationItemsSize = navigationItems.size(); i < navigationItemsSize; i++) {
           NavigationItem item = navigationItems.get(i);
           final TreeNode treeNode = new TreeNode(item.getName());
           //treeNode.setIcon(item.getIcon());
           treeNode.setTitle(item.getTitle());
           if(!item.getName().equals(""))   //TODO: development only, should be removed
               treeNode.setEnabled(item.isEnabled());
           treeNodes[i] = treeNode;
       }

       TreeNode rootNode = new TreeNode(navigationSection.getName() ,treeNodes);
       Tree tree = new Tree();
       tree.setRoot(rootNode);
       treeGrid.setData(tree);

       return treeGrid;
   }

  protected void addSection(final TreeGrid treeGrid) {
       final String sectionName = treeGrid.getTree().getRoot().getName();
       final String sectionTitle = treeGrid.getTitle();
       this.treeGrids.put(sectionName, treeGrid);

       treeGrid.addCellClickHandler(new CellClickHandler() {
           @Override
           public void onCellClick(CellClickEvent event)
           {


               TreeNode selectedRecord = (TreeNode) treeGrid.getSelectedRecord();
               if(selectedRecord.getName().equals("") ) return; // TODO: development only

               if (selectedRecord != null) {
                   final String pageName = selectedRecord.getName();
                   //final String viewPath = pageName;//sectionName + "/" + pageName;
                   //History.newItem(viewPath);

                   Console.MODULES.getPlaceManager().revealPlaceHierarchy(
                           new ArrayList<PlaceRequest>(){{
                               add(new PlaceRequest(appId));
                               add(new PlaceRequest(pageName));
                           }}
                   );

                   highlightTool(sectionName, pageName);
               }
           }
       });

       SectionStackSection section = new SectionStackSection(sectionTitle);
       section.setExpanded(true);
       section.addItem(treeGrid);

       this.sectionStack.addSection(section);
   }

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