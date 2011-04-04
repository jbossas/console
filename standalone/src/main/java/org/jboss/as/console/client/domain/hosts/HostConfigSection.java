package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.widgets.DisclosureStackHeader;
import org.jboss.as.console.client.widgets.LHSNavigationTree;
import org.jboss.as.console.client.widgets.LHSTreeItem;

/**
 * @author Heiko Braun
 * @date 3/4/11
 */
class HostConfigSection {

    private DisclosurePanel panel;

    private LHSNavigationTree hostTree;

    public HostConfigSection() {
        super();

        panel = new DisclosureStackHeader("Host Configuration").asWidget();

        hostTree = new LHSNavigationTree();

        LHSTreeItem paths = new LHSTreeItem("Paths", "hosts/host-paths");
        LHSTreeItem jvms = new LHSTreeItem("Virtual Machines", "hosts/host-interfaces");
        LHSTreeItem sockets = new LHSTreeItem("Socket Binding Groups", "hosts/host-socket-bindings");
        LHSTreeItem properties = new LHSTreeItem("System Properties", "host/host-properties");

        hostTree.addItem(paths);
        hostTree.addItem(jvms);
        hostTree.addItem(sockets);
        hostTree.addItem(properties);

        panel.setContent(hostTree);

    }

    public Widget asWidget() {
        return panel;
    }
}