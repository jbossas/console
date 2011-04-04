package org.jboss.as.console.client.widgets;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
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
 * @see LHSTreeItem
 *
 * @author Heiko Braun
 * @date 3/24/11
 */
public class LHSNavigationTree extends Tree {

    public LHSNavigationTree() {
        super(DefaultTreeResources.INSTANCE);

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
            }
        });
    }
}
