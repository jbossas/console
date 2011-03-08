package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.widgets.StackSectionHeader;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/2/11
 */
class LHSHostsNavigation {

    private static final int SELECTOR_HEIGHT = 60;
    private StackLayoutPanel stack;

    private ServersSection serversSection;

    private HostSelector selector;

    private DockLayoutPanel layout;

    public LHSHostsNavigation() {

        layout = new DockLayoutPanel(Style.Unit.PX);
        layout.setStyleName("fill-layout");

        selector = new HostSelector();
        final Widget selectorWidget = selector.asWidget();

        stack = new StackLayoutPanel(Style.Unit.PX);
        stack.addStyleName("section-stack");

        serversSection = new ServersSection();

        Widget serverSectionWidget = serversSection.asWidget();
        stack.add(serverSectionWidget, new StackSectionHeader("Servers"), 28);

        ServerInstanceSection instanceSection = new ServerInstanceSection();
        stack.add(instanceSection.asWidget(), new StackSectionHeader("Server Instances"), 28);

        HostConfigSection hostConfigSection = new HostConfigSection();
        stack.add(hostConfigSection.asWidget(), new StackSectionHeader("General Configuration"), 28);

        // -----------------------------

        layout.addNorth(selectorWidget, SELECTOR_HEIGHT);
        layout.add(stack);

    }

    public Widget asWidget()
    {
        return layout;
    }

    public void updateHosts(List<Host> hosts) {
        selector.updateHosts(hosts);
    }

    public void updateInstances(List<Server> servers) {
        serversSection.updateServers(servers);
    }
}
