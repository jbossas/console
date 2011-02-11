package org.jboss.as.console.client.domain;

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
import org.jboss.as.console.client.domain.profiles.ProfileRecord;

import java.util.ArrayList;

/**
 * @author Heiko Braun
 * @date 2/11/11
 */
public class TreeLHSDomainNavigation {

    private SectionStack sectionStack;
    private TreeNode profileNode ;
    private TreeGrid treeGrid;

    public TreeLHSDomainNavigation() {

        sectionStack = new SectionStack();
        sectionStack.addStyleName("lhs-section-stack");
        sectionStack.setShowResizeBar(true);
        sectionStack.setVisibilityMode(VisibilityMode.MULTIPLE);
        sectionStack.setWidth(220);
        sectionStack.setHeight100();
        sectionStack.setCanDragScroll(false);
        sectionStack.setMargin(0);

        treeGrid = new TreeGrid();
        treeGrid.setTitle("Domain Configuration");
        treeGrid.setWidth100();
        treeGrid.setHeight100();
        treeGrid.setShowHeader(false);
        treeGrid.setCanSort(false);

        treeGrid.addCellClickHandler(new CellClickHandler() {
            @Override
            public void onCellClick(CellClickEvent event) {
                final TreeNode selectedRecord = (TreeNode) treeGrid.getSelectedRecord();

                 /*Console.MODULES.getPlaceManager().revealPlaceHierarchy(
                         new ArrayList<PlaceRequest>() {{
                             add(new PlaceRequest("domain"));
                             add(new PlaceRequest(selectedRecord.getName()));
                         }}
                 );*/

                History.newItem(selectedRecord.getName());

            }
        });

        profileNode = new NavTreeNode(
                "profiles", "Profiles", false/*,
                new NavTreeNode("profiles;name=ee6", "\"EE6 Web Profile\""),
                new NavTreeNode("profile;name=messaging", "\"Messaging Profile\"")*/
        );

        // -----

        final TreeNode serverGroupNode = new NavTreeNode(
                "server-groups", "Server Groups", false,
                new NavTreeNode("server-groups;name=ee6", "\"EE6 Application Server\""),
                new NavTreeNode("server-groups;name=web server", "\"Web Server\""),
                new NavTreeNode("server-groups;name=messaging", "\"Messaging Server\"")
        );

        // -----

        final TreeNode deploymentNode = new NavTreeNode(
                "domain-deployments", "Domain Deployments",false,
                new NavTreeNode("domain-deployments;type=web", "Web Applications"),
                new NavTreeNode("domain-deployments;type=ee", "Enterprise Applications"),
                new NavTreeNode("domain-deployments;type=rar", "Resource Adapters"),
                new NavTreeNode("domain-deployments;type=other", "Other")

        );

        // -----

        final TreeNode commonNode = new NavTreeNode(
                "common", "Common Settings", true,
                new NavTreeNode("paths", "Paths"),
                new NavTreeNode("interfaces", "Interfaces"),
                new NavTreeNode("sockets", "Socket Binding Groups"),
                new NavTreeNode("properties", "System Properties")
                );

        // -----

        final TreeNode rootNode = new TreeNode(
                "root",
                profileNode, serverGroupNode, deploymentNode, commonNode
        );
        rootNode.setTitle("Domain");
        Tree tree = new Tree();
        tree.setRoot(rootNode);

        treeGrid.setData(tree);
        treeGrid.getTree().openAll();

        SectionStackSection domainSection = new SectionStackSection("Domain Configuration");
        domainSection.setExpanded(true);
        domainSection.addItem(treeGrid);

        SectionStackSection hostSection = new SectionStackSection("Host Specific Settings");
        domainSection.setExpanded(true);


        sectionStack.addSection(domainSection);
        sectionStack.addSection(hostSection);


    }

    public Widget asWidget()
    {
        return sectionStack;
    }

    public void updateFrom(ProfileRecord[] profileRecords) {

        treeGrid.getTree().closeAll(profileNode);

        TreeNode[] nodes = new TreeNode[profileRecords.length];
        int i = 0;
        for(ProfileRecord profile : profileRecords)
        {
            String profileName = profile.getAttribute("profile-name");
            nodes[i] = new NavTreeNode("profiles;name="+profileName, profileName);
            i++;
        }

        profileNode.setChildren(nodes);

        treeGrid.markForRedraw();
        treeGrid.getTree().openAll(profileNode);
    }

    class NavTreeNode extends TreeNode
    {
        NavTreeNode(String name, String title) {
            super(name);
            setTitle(title);
            setIcon("/images/blank.png");
        }

        NavTreeNode(String name, String title, boolean isSecondary, NavTreeNode... children) {
            this(name, title);

            setChildren(children);

            if(isSecondary)
            {
                setCustomStyle("lhs-secondary");
                for(NavTreeNode child : children)
                    child.setCustomStyle("lhs-secondary");
            }
            else
            {
                setCustomStyle("lhs-primary-header");
            }
        }

        @Override
        public String getIcon() {
            return null;
        }
    }
}
