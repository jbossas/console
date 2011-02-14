package org.jboss.as.console.client.domain;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Widget;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.events.ChangeEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangeHandler;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;
import org.jboss.as.console.client.Console;

/**
 * @author Heiko Braun
 * @date 2/11/11
 */
public class LHSDomainNavigation {

    private SectionStack sectionStack;
    private TreeNode subsysNode;
    private TreeGrid subsysTreeGrid;
    private ComboBoxItem profileSelection;

    public LHSDomainNavigation() {

        sectionStack = new SectionStack();
        sectionStack.addStyleName("lhs-section-stack");
        sectionStack.setShowResizeBar(true);
        sectionStack.setVisibilityMode(VisibilityMode.MULTIPLE);
        sectionStack.setWidth(220);
        sectionStack.setHeight100();

        subsysTreeGrid = new NavigationTreeGrid("Profile");

        final DynamicForm form = new DynamicForm();
        form.setWidth100();

        profileSelection = new ComboBoxItem();
        profileSelection.setTitle("Profile");
        profileSelection.setType("comboBox");
        profileSelection.addChangeHandler(new ChangeHandler()
        {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                Console.MODULES.getEventBus().fireEvent(new ProfileSelectionEvent((String) changeEvent.getValue()));
            }
        });
        profileSelection.setShowTitle(false);
        form.setFields(profileSelection);


        subsysNode = new NavTreeNode( "subsystems", false);
        Tree profileTree = new Tree();
        profileTree.setRoot(subsysNode);

        subsysTreeGrid.setData(profileTree);
        subsysTreeGrid.getTree().openAll();


        // -----

        NavigationTreeGrid sgTreeGrid = new NavigationTreeGrid("Server Groups");

        final TreeNode serverGroupNode = new NavTreeNode(
                "server-groups", "Server Groups", false,
                new NavTreeNode("server-groups;name=ee6", "\"EE6 Application Server\""),
                new NavTreeNode("server-groups;name=web server", "\"Web Server\""),
                new NavTreeNode("server-groups;name=messaging", "\"Messaging Server\"")
        );

        Tree sgTree = new Tree();
        sgTree.setRoot(serverGroupNode);
        sgTreeGrid.setData(sgTree);


        // -----
        NavigationTreeGrid deploymentGrid = new NavigationTreeGrid("Deployments");

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


        // -----
        NavigationTreeGrid commonGrid = new NavigationTreeGrid("Deployments");
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

        // -----

        SectionStackSection profileSection = new SectionStackSection("Profile");
        profileSection.setExpanded(true);
        profileSection.addItem(subsysTreeGrid);
        profileSection.setControls(form);

        SectionStackSection sgSection = new SectionStackSection("Server Groups");
        sgSection.setExpanded(true);
        sgSection.addItem(sgTreeGrid);

        SectionStackSection deploymentSection = new SectionStackSection("Deployments");
        deploymentSection.setExpanded(false);
        deploymentSection.addItem(deploymentGrid);

        SectionStackSection commonSection = new SectionStackSection("General Configuration");
        commonSection.setExpanded(false);
        commonSection.addItem(commonGrid);


        sectionStack.addSection(profileSection);
        sectionStack.addSection(sgSection);
        sectionStack.addSection(deploymentSection);
        sectionStack.addSection(commonSection);


    }

    public Widget asWidget()
    {
        return sectionStack;
    }

    public void updateFrom(ProfileRecord[] profileRecords) {

        String[] updates = new String[profileRecords.length];
        int i=0;
        for(ProfileRecord record : profileRecords)
        {
            updates[i] = record.getAttribute("profile-name");
            i++;
        }

        profileSelection.setValueMap(updates);
        subsysTreeGrid.markForRedraw();
    }

    public void updateFrom(SubsystemRecord[] subsystems) {

        subsysTreeGrid.getTree().closeAll(subsysNode);

        TreeNode[] nodes = new TreeNode[subsystems.length];
        int i = 0;
        for(SubsystemRecord subsys: subsystems)
        {
            // TODO: implement
            nodes[i] = new NavTreeNode(subsys.getName(), subsys.getTitle());
            i++;
        }

        subsysNode.setChildren(nodes);

        subsysTreeGrid.markForRedraw();
        subsysTreeGrid.getTree().openAll(subsysNode);
    }

    class NavTreeNode extends TreeNode
    {
        NavTreeNode(String name, String title) {
            super(name);
            setTitle(title);
            //setIcon("/images/blank.png");
        }

        NavTreeNode(String name, boolean isSecondary, NavTreeNode... children) {
            super(name);
            //setIcon("/images/blank.png");

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


    class NavigationTreeGrid extends TreeGrid
    {
        NavigationTreeGrid(String title) {

            setTitle(title);
            setWidth100();
            setHeight100();
            setShowHeader(false);
            setCanSort(false);
            setLeaveScrollbarGap(false);

            addCellClickHandler(new CellClickHandler() {
                @Override
                public void onCellClick(CellClickEvent event) {
                    final TreeNode selectedRecord = (TreeNode) getSelectedRecord();

                    /*Console.MODULES.getPlaceManager().revealPlaceHierarchy(
                            new ArrayList<PlaceRequest>() {{
                                add(new PlaceRequest("domain"));
                                add(new PlaceRequest(selectedRecord.getName()));
                            }}
                    );*/

                    History.newItem(selectedRecord.getName());

                }
            });

        }
    }
}
