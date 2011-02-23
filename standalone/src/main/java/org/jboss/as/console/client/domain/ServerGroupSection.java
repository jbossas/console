package org.jboss.as.console.client.domain;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.NameTokens;
import org.jboss.as.console.client.components.LHSNavItem;
import org.jboss.as.console.client.icons.Icons;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.util.Places;

/**
 * LHS navigation section of the domain management app.
 * Gives access to server group management use cases.
 *
 * @see LHSDomainNavigation
 *
 * @author Heiko Braun
 * @date 2/15/11
 */
class ServerGroupSection {

    LayoutPanel layout;
    Tree serverGroupTree;
    TreeItem root;

    public ServerGroupSection() {

        layout = new LayoutPanel();
        layout.setStyleName("stack-section");

        serverGroupTree = new Tree();
        root = new TreeItem("Current Groups:");
        serverGroupTree.addItem(root);

        LHSNavItem overview = new LHSNavItem(
                "Overview",
                "domain/" + NameTokens.ServerGroupOverviewPresenter,
                Icons.INSTANCE.inventory()
        );
        LHSNavItem createNew = new LHSNavItem(
                "Create Server Group",
                "domain/" + NameTokens.ServerGroupPresenter + ";action=new",
                Icons.INSTANCE.add());

        layout.add(overview);
        layout.add(createNew);
        layout.add(serverGroupTree);

        layout.setWidgetTopHeight(overview, 0, Style.Unit.PX, 25, Style.Unit.PX);
        layout.setWidgetTopHeight(createNew, 25, Style.Unit.PX, 25, Style.Unit.PX);
        layout.setWidgetTopHeight(serverGroupTree, 55, Style.Unit.PX, 100, Style.Unit.PCT);
    }

    public Widget asWidget()
    {
        return layout;
    }

    public void updateFrom(ServerGroupRecord[] serverGroupRecords) {

        root.removeItems();

        for(ServerGroupRecord record : serverGroupRecords)
        {
            String groupName = record.getGroupName();
            final String token = "domain/" + NameTokens.ServerGroupPresenter + ";name=" + groupName;
            HTML link = new HTML(groupName);
            link.addClickHandler(new ClickHandler()
            {
                @Override
                public void onClick(ClickEvent event) {
                    Console.MODULES.getPlaceManager().revealPlaceHierarchy(
                            Places.fromString(token)
                    );
                }
            });
            TreeItem item = new TreeItem(link);
            item.setStyleName("lhs-tree-item");
            root.addItem(item);
        }

        root.setState(true);
    }


}
