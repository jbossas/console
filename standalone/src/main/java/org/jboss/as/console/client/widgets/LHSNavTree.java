package org.jboss.as.console.client.widgets;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.Places;
import org.jboss.as.console.client.widgets.resource.DefaultTreeResources;

/**
 *
 * A tree that's used as a navigation element on the left hand side.<br>
 * It's driven by a token attribute that's associated with the tree item.
 *
 * @see LHSNavTreeItem
 *
 * @author Heiko Braun
 * @date 3/24/11
 */
public class LHSNavTree extends Tree implements LHSNavEvent.NavItemSelectionHandler{

    private static final String TREE_ID_ATTRIBUTE = "treeid";

    private String treeId;

    public LHSNavTree() {
        super(DefaultTreeResources.INSTANCE);

        this.treeId = "lhs-nav-tree_"+HTMLPanel.createUniqueId();

        addStyleName("stack-section");

        addSelectionHandler(new SelectionHandler<TreeItem>() {
            @Override
            public void onSelection(SelectionEvent<TreeItem> event) {
                TreeItem item = event.getSelectedItem();

                if(item.getElement().hasAttribute("token"))
                {
                    String token = item.getElement().getAttribute("token");
                    Console.MODULES.getPlaceManager().revealPlaceHierarchy(
                            Places.fromString(token)
                    );
                }

                // highlight section
                Console.MODULES.getEventBus().fireEvent(
                        new LHSNavEvent(treeId)
                );
            }
        });

        Console.MODULES.getEventBus().addHandler(LHSNavEvent.TYPE, this);
    }

    @Override
    public void addItem(TreeItem item) {
        item.getElement().setAttribute(TREE_ID_ATTRIBUTE, treeId);
        super.addItem(item);

    }

    @Override
    public void onSelectedNavTree(String selectedId) {

        if(!selectedId.equals(treeId))
        {
            // deselect
            for(int i=0; i<getItemCount(); i++)
            {
                LHSNavTreeItem item = (LHSNavTreeItem)getItem(i);
                item.setSelected(false);
            }
        }

        // NOTE: select is handled by LHSTreeItem itself
    }
}
