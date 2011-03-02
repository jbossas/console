package org.jboss.as.console.client.domain.groups;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.Places;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.widgets.LHSNavItem;
import org.jboss.as.console.client.widgets.icons.Icons;

import java.util.List;

/**
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

        LHSNavItem createNew = new LHSNavItem(
                "Create Group",
                "domain/" + NameTokens.ServerGroupPresenter + ";action=new",
                Icons.INSTANCE.add_small());

        layout.add(createNew);
        layout.add(serverGroupTree);

        layout.setWidgetTopHeight(createNew, 0, Style.Unit.PX, 25, Style.Unit.PX);
        layout.setWidgetTopHeight(serverGroupTree, 30, Style.Unit.PX, 100, Style.Unit.PCT);
    }

    public Widget asWidget()
    {
        return layout;
    }

    public void updateFrom(List<ServerGroupRecord> serverGroupRecords) {

        root.removeItems();

        for(ServerGroupRecord record : serverGroupRecords)
        {
            String groupName = record.getGroupName();
            final String token = "domain/" + NameTokens.ServerGroupPresenter + ";name=" + groupName;

            HTML link = new HTML(groupName);
            final TreeItem item = new TreeItem(link);
            item.setStyleName("lhs-tree-item");

            link.addClickHandler(new ClickHandler()
            {
                @Override
                public void onClick(ClickEvent event) {
                    serverGroupTree.setSelectedItem(item);
                    Console.MODULES.getPlaceManager().revealPlaceHierarchy(
                            Places.fromString(token)
                    );
                }
            });

            root.addItem(item);
        }

        root.setState(true);
    }


}
