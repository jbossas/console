package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.widgets.DisclosureStackHeader;
import org.jboss.as.console.client.widgets.LHSNavTree;
import org.jboss.as.console.client.widgets.LHSNavTreeItem;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/2/11
 */
class ServersConfigSection {

    private Tree hostTree;

    private String selectedHost = null;
    private DisclosurePanel panel;

    public ServersConfigSection() {

        panel = new DisclosureStackHeader("Server Configurations").asWidget();
        hostTree = new LHSNavTree("hosts");
        panel.setContent(hostTree);
    }

    public void setSelectedHost(String selectedHost) {
        this.selectedHost = selectedHost;
    }

    public Widget asWidget()
    {
        return panel;
    }

    public void updateServers(List<Server> servers) {


        hostTree.removeItems();

        for(Server server: servers)
        {
            final String serverName = server.getName();
            final TreeItem item = new LHSNavTreeItem(serverName, buildToken(serverName));
            hostTree.addItem(item);
        }

        if(servers.isEmpty())
        {
            TreeItem empty = new TreeItem(new HTML("(no servers)"));
            hostTree.addItem(empty);
        }

    }

    public String buildToken(String serverName) {
        assert selectedHost!=null : "host selection is null!";
        final String token = "hosts/" + NameTokens.ServerPresenter+
                ";host="+selectedHost +
                ";server=" + serverName;
        return token;
    }
}
