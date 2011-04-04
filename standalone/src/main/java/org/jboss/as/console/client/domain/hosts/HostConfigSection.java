package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.widgets.DisclosureStackHeader;
import org.jboss.as.console.client.widgets.LHSNavTree;
import org.jboss.as.console.client.widgets.LHSNavTreeItem;

/**
 * @author Heiko Braun
 * @date 3/4/11
 */
class HostConfigSection {

    private DisclosurePanel panel;

    private LHSNavTree hostTree;

    public HostConfigSection() {
        super();

        panel = new DisclosureStackHeader("Host Configuration").asWidget();

        hostTree = new LHSNavTree("hosts");

        LHSNavTreeItem paths = new LHSNavTreeItem("Paths", "hosts/host-paths");
        LHSNavTreeItem jvms = new LHSNavTreeItem("Virtual Machines", "hosts/host-interfaces");
        LHSNavTreeItem sockets = new LHSNavTreeItem("Socket Binding Groups", "hosts/host-socket-bindings");
        LHSNavTreeItem properties = new LHSNavTreeItem("System Properties", "host/host-properties");

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