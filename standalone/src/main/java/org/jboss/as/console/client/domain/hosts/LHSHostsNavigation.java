package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.dom.client.Style;
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

    private StackLayoutPanel stack;

    private ServersSection serversSection;

    public LHSHostsNavigation() {

        stack = new StackLayoutPanel(Style.Unit.PX);
        stack.addStyleName("section-stack");

        serversSection = new ServersSection();
        stack.add(serversSection.asWidget(), new StackSectionHeader("Servers"), 28);

    }

    public Widget asWidget()
    {
        return stack;
    }

    public void updateHosts(List<Host> hosts) {
        serversSection.updateHosts(hosts);
    }

    public void updateInstances(List<Server> servers) {
        serversSection.updateServers(servers);
    }
}
