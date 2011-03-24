package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.widgets.LHSNavItem;
import org.jboss.as.console.client.widgets.LHSNavigationTree;
import org.jboss.as.console.client.widgets.LHSTreeItem;
import org.jboss.as.console.client.widgets.icons.Icons;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/2/11
 */
class ServersSection {

    private TreeItem root;
    private Tree hostTree;

    private LayoutPanel layout;
    private String selectedHost = null;

    public ServersSection() {

        layout = new LayoutPanel();
        layout.setStyleName("stack-section");

        LHSNavItem createNew = new LHSNavItem(
                "Create Server",
                "hosts/" + NameTokens.ServerPresenter + ";action=new",
                Icons.INSTANCE.add_small()
        );

        // --------------------------------------------------

        hostTree = new LHSNavigationTree();
        root = new TreeItem("Servers on Host:");
        hostTree.addItem(root);

        // --------------------------------------------------

        layout.add(createNew);
        layout.add(hostTree);

        layout.setWidgetTopHeight(createNew, 0, Style.Unit.PX, 25, Style.Unit.PX);
        layout.setWidgetTopHeight(hostTree, 28, Style.Unit.PX, 100, Style.Unit.PCT);
    }

    public void setSelectedHost(String selectedHost) {
        this.selectedHost = selectedHost;
    }

    public Widget asWidget()
    {
        return layout;
    }

    public void updateServers(List<Server> servers) {

        root.setState(false); // hide it
        root.removeItems();

        for(Server server: servers)
        {
            final String serverName = server.getName();
            final TreeItem item = new LHSTreeItem(serverName, buildToken(serverName));
            root.addItem(item);
        }

        if(servers.isEmpty())
        {
            TreeItem empty = new TreeItem(new HTML("(no servers)"));
            root.addItem(empty);
        }

        root.setState(true);
        root.setState(true);

    }

    public String buildToken(String serverName) {
        assert selectedHost!=null : "host selection is null!";
        final String token = "hosts/" + NameTokens.ServerPresenter+
                ";host="+selectedHost +
                ";server=" + serverName;
        return token;
    }
}
