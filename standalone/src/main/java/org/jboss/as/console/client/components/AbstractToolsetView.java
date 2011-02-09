package org.jboss.as.console.client.components;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;
import org.jboss.as.console.client.Console;

import java.util.*;

/**
 * @author Heiko Braun
 * @since 1/31/11
 */
public abstract class AbstractToolsetView  {

    protected String appId;  // distinct, top level navigation element

    private Map<String, TreeGrid> treeGrids = new LinkedHashMap<String, TreeGrid>();
    private Map<String, NavigationSection> sectionsByName;

    private SectionStack sectionStack;

    private Canvas contentCanvas;

    private String currentSectionViewId;
    private String currentPageViewId;

    private HLayout layout;

    public AbstractToolsetView(String topLevelId) {
        super();
        this.appId = topLevelId;

        layout = new HLayout()
        {{
                setWidth100();
                setHeight100();
                setStyleName("abstract-toolset-nav");

                contentCanvas = new Canvas();
                contentCanvas.setWidth100();//setWidth("*");
                contentCanvas.setHeight100();

                //contentCanvas.addChild(new Label("Empty"));

                sectionStack = new SectionStack();
                sectionStack.setShowResizeBar(true);
                sectionStack.setVisibilityMode(VisibilityMode.MULTIPLE);
                sectionStack.setWidth(250);
                sectionStack.setHeight100();

                // Build left hand navigation
                final List<NavigationSection> sections = getNavigationSections();
                sectionsByName = new HashMap<String, NavigationSection>(sections.size());

                for (NavigationSection section : sections)
                {
                    TreeGrid treeGrid = buildTreeGridForSection(section);
                    addSection(treeGrid);
                    treeGrid.getTree().openAll();

                    sectionsByName.put(section.getName(), section);
                }

                addMember(sectionStack);
                addMember(contentCanvas);
            }
        };
    }

    public Widget asWidget()
    {
        return layout;
    }

    protected abstract List<NavigationSection> getNavigationSections();

    public void setContent(Widget newContent) {
        // A call to destroy (e.g. certain IFrames/FullHTMLPane) can actually remove multiple children of the
        // contentCanvas. As such, we need to query for the children after each destroy to ensure only valid children
        // are in the array.
        Canvas[] children;
        while ((children = contentCanvas.getChildren()).length > 0) {
            children[0].destroy();
        }

        contentCanvas.addChild(newContent);
        contentCanvas.markForRedraw();
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

    // --------------------------------------------------------------

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
