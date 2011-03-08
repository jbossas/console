package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.widgets.StackSectionHeader;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/2/11
 */
class LHSHostsNavigation implements HostSelectionEvent.HostSelectionListener {

    private static final int SELECTOR_HEIGHT = 60;
    private static final int HEADER_SIZE = 28;

    private ServersSection serversSection;
    private InstancesSection instanceSection;
    private HostConfigSection hostConfigSection;

    private HostSelector selector;
    private StackLayoutPanel stack;
    private DockLayoutPanel layout;

    public LHSHostsNavigation() {

        layout = new DockLayoutPanel(Style.Unit.PX);
        layout.setStyleName("fill-layout");

        selector = new HostSelector();
        final Widget selectorWidget = selector.asWidget();

        stack = new StackLayoutPanel(Style.Unit.PX);
        stack.addStyleName("section-stack");

        serversSection = new ServersSection();
        stack.add(serversSection.asWidget(), new StackSectionHeader("Servers"), HEADER_SIZE);

        instanceSection = new InstancesSection();
        stack.add(instanceSection.asWidget(), new StackSectionHeader("Server Instances"), HEADER_SIZE);

        hostConfigSection = new HostConfigSection();
        stack.add(hostConfigSection.asWidget(), new StackSectionHeader("General Configuration"), HEADER_SIZE);

        // -----------------------------

        layout.addNorth(selectorWidget, SELECTOR_HEIGHT);
        layout.add(stack);

        // listen on host selection events
        // TODO: should this be moved ot presenter onBind()?
        Console.MODULES.getEventBus().addHandler(
                HostSelectionEvent.TYPE, this
        );

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

    @Override
    public void onHostSelection(String hostName) {
        serversSection.setSelectedHost(hostName);
        instanceSection.setSelectedHost(hostName);
    }
}
