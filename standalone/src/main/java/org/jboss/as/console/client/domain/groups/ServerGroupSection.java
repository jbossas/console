package org.jboss.as.console.client.domain.groups;

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.widgets.DisclosureStackHeader;
import org.jboss.as.console.client.widgets.LHSNavTree;
import org.jboss.as.console.client.widgets.LHSNavTreeItem;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
class ServerGroupSection {

    DisclosurePanel panel;
    Tree serverGroupTree;

    public ServerGroupSection() {

        panel = new DisclosureStackHeader("Server Groups").asWidget();
        serverGroupTree = new LHSNavTree();
        panel.setContent(serverGroupTree);

        serverGroupTree = new LHSNavTree();
        panel.setContent(serverGroupTree);
    }

    public Widget asWidget()
    {
        return panel;
    }

    public void updateFrom(List<ServerGroupRecord> serverGroupRecords) {

        serverGroupTree.removeItems();

        for(ServerGroupRecord record : serverGroupRecords)
        {
            String groupName = record.getGroupName();
            final String token = "domain/" + NameTokens.ServerGroupPresenter + ";name=" + groupName;
            final TreeItem item = new LHSNavTreeItem(groupName, token);
            serverGroupTree.addItem(item);
        }
    }


}
