package org.jboss.as.console.client.widgets;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;


/**
 * Fired when LHS navigation is selected.
 * Used to highlight different content sections.
 *
 * @author Heiko Braun
 * @date 2/7/11
 */
public class LHSHighlightEvent extends GwtEvent<LHSHighlightEvent.NavItemSelectionHandler> {

    public static final Type TYPE = new Type<NavItemSelectionHandler>();

    private String treeId, item, category;

    public LHSHighlightEvent(String treeId, String item, String category) {
        super();
        this.treeId = treeId;
        this.item = item;
        this.category = category;
    }

    @Override
    public Type<NavItemSelectionHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(NavItemSelectionHandler listener) {
        listener.onSelectedNavTree(treeId, item, category);
    }

    public String getTreeId() {
        return treeId;
    }

    public String getItem() {
        return item;
    }

    public String getCategory() {
        return category;
    }

    public interface NavItemSelectionHandler extends EventHandler {
        void onSelectedNavTree(String treeId, String item, String category);
    }
}


