package org.jboss.as.console.client.components.sgwt;

import com.allen_sauer.gwt.log.client.Log;
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

import java.util.*;

/**
 * Simple LHS navigation. Taken from RHQ.
 *
 * @author Heiko Braun
 * @date 2/10/11
 */
public class LHSNavigation {

    protected String appId;  // distinct, top level navigation element

    private Map<String, TreeGrid> treeGrids = new LinkedHashMap<String, TreeGrid>();
    private Map<String, NavigationSection> sectionsByName;
    private SectionStack sectionStack;

    public LHSNavigation(String appId, final List<NavigationSection> sections) {
        super();
        this.appId = appId;

        sectionStack = new SectionStack();
        sectionStack.setShowResizeBar(true);
        sectionStack.setVisibilityMode(VisibilityMode.MULTIPLE);
        sectionStack.setWidth(250);
        sectionStack.setHeight100();

        // Build left hand navigation
        sectionsByName = new HashMap<String, NavigationSection>(sections.size());

        for (NavigationSection section : sections)
        {
            TreeGrid treeGrid = buildTreeGridForSection(section);
            addSection(treeGrid);
            treeGrid.getTree().openAll();

            sectionsByName.put(section.getName(), section);
        }

    }

    public Widget asWidget()
    {
        return sectionStack;
    }

    protected TreeGrid buildTreeGridForSection(NavigationSection navigationSection) {
        final TreeGrid treeGrid = new TreeGrid();
        treeGrid.setTitle(navigationSection.getTitle());
        treeGrid.setLeaveScrollbarGap(false);
        treeGrid.setShowHeader(false);

        List<NavigationItem> navigationItems = navigationSection.getNavigationItems();
        TreeNode[] treeNodes = new TreeNode[navigationItems.size()];
        for (int i = 0, navigationItemsSize = navigationItems.size(); i < navigationItemsSize; i++) {
            NavigationItem item = navigationItems.get(i);
            final TreeNode treeNode = new TreeNode(item.getName());
            treeNode.setTitle(item.getTitle());
            //treeNode.setIcon(item.getIcon());
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
    }
}