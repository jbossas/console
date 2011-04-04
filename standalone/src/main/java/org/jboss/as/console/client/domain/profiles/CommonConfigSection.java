package org.jboss.as.console.client.domain.profiles;

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.widgets.DisclosureStackHeader;
import org.jboss.as.console.client.widgets.LHSNavTree;
import org.jboss.as.console.client.widgets.LHSNavTreeItem;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
class CommonConfigSection {

    private DisclosurePanel panel;
    private LHSNavTree commonTree;

    public CommonConfigSection() {
        super();

        panel = new DisclosureStackHeader("General Configuration").asWidget();
        commonTree = new LHSNavTree("profiles");
        panel.setContent(commonTree);

        LHSNavTreeItem paths = new LHSNavTreeItem("Paths", "domain/paths");
        LHSNavTreeItem interfaces = new LHSNavTreeItem("Interfaces", "domain/domain-interfaces");
        LHSNavTreeItem sockets = new LHSNavTreeItem("Socket Binding Groups", "domain/socket-bindings");
        LHSNavTreeItem properties = new LHSNavTreeItem("System Properties", "domain/domain-properties");

        commonTree.addItem(paths);
        commonTree.addItem(interfaces);
        commonTree.addItem(sockets);
        commonTree.addItem(properties);
    }

    public Widget asWidget() {
        return panel;
    }

}
