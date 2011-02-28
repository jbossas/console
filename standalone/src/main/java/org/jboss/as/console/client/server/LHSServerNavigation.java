package org.jboss.as.console.client.server;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.*;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.Places;
import org.jboss.as.console.client.shared.SubsystemRecord;
import org.jboss.as.console.client.widgets.LHSNavItem;
import org.jboss.as.console.client.widgets.StackSectionHeader;

import java.util.List;

/**
 * LHS navigation for standalone server management.
 *
 * @author Heiko Braun
 * @date 2/10/11
 */
public class LHSServerNavigation {

    private StackLayoutPanel stack;
    TreeItem subsysRoot;

    public LHSServerNavigation() {
        super();

        stack = new StackLayoutPanel(Style.Unit.PX);
        stack.addStyleName("section-stack");
        stack.setWidth("250");


        // ----------------------------------------------------

        LayoutPanel subsysLayout = new LayoutPanel();
        subsysLayout.setStyleName("stack-section");

        Tree subsysTree = new Tree();
        subsysTree.addSelectionHandler(new SelectionHandler<TreeItem>() {
            @Override
            public void onSelection(SelectionEvent<TreeItem> event) {
                TreeItem selectedItem = event.getSelectedItem();
                String token = selectedItem.getElement().getAttribute("token");
                Console.MODULES.getPlaceManager().revealPlaceHierarchy(
                        Places.fromString(token)
                );
            }
        });

        subsysRoot = new TreeItem("Subsystems:");
        subsysTree.addItem(subsysRoot);

        subsysLayout.add(subsysTree);
        stack.add(subsysLayout, new StackSectionHeader("Profile"), 28);

        // ----------------------------------------------------

        LayoutPanel dplLayout = new LayoutPanel();
        dplLayout.setStyleName("stack-section");

        LHSNavItem[] dplItems = new LHSNavItem[] {
                new LHSNavItem("Web Applications", "server-deployments;type=web"),
                new LHSNavItem("Enterprise Applications", "server-deployments;type=ee"),
                new LHSNavItem("Resource Adapters", "server-deployments;type=jca"),
                new LHSNavItem("Other", "server-deployments;type=other")
        };

        int i =0;
        for(LHSNavItem item : dplItems)
        {
            dplLayout.add(item);
            dplLayout.setWidgetTopHeight(item, i, Style.Unit.PX, 25, Style.Unit.PX);
            i+=25;
        }

        stack.add(dplLayout, new StackSectionHeader("Deployments"), 28);

                       
        // ----------------------------------------------------

        LayoutPanel commonLayout = new LayoutPanel();
         commonLayout.setStyleName("stack-section");

        LHSNavItem[] commonItems = new LHSNavItem[] {
                new LHSNavItem("Paths", "server/server-paths"),
                new LHSNavItem("Interfaces", "server/server-interfaces"),
                new LHSNavItem("Socket Binding Groups", "server/server-sockets"),
                new LHSNavItem("System Properties", "server/server-properties")
        };

        i =0;
        for(LHSNavItem item : commonItems)
        {
            commonLayout.add(item);
            commonLayout.setWidgetTopHeight(item, i, Style.Unit.PX, 25, Style.Unit.PX);
            i+=25;
        }

        stack.add(commonLayout, new StackSectionHeader("General Config"), 28);

    }

    public Widget asWidget()
    {
        return stack;
    }

    public void updateFrom(List<SubsystemRecord> subsystems) {

        subsysRoot.removeItems();

        for(SubsystemRecord subsys: subsystems)
        {
            TreeItem item = new TreeItem(new HTML(subsys.getTitle()));
            item.getElement().setAttribute("token", subsys.getToken());
            item.setStyleName("lhs-tree-item");
            subsysRoot.addItem(item);
        }

        subsysRoot.setState(true);

    }
}