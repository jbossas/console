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
public class LHSNavEvent extends GwtEvent<LHSNavEvent.NavItemSelectionHandler> {

    public static final Type TYPE = new Type<NavItemSelectionHandler>();

    private String treeId;


    public LHSNavEvent(String treeId) {
        super();
        this.treeId = treeId;
    }

    @Override
    public Type<NavItemSelectionHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(NavItemSelectionHandler listener) {
        listener.onSelectedNavTree(treeId);
    }

    public String getTreeId() {
        return treeId;
    }

    public interface NavItemSelectionHandler extends EventHandler {
        void onSelectedNavTree(String treeId);
    }
}


