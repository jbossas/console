package org.jboss.as.console.client.domain.hosts;

import org.jboss.as.console.client.widgets.LHSNavItem;
import org.jboss.as.console.client.widgets.SimpleLHSSection;

/**
 * @author Heiko Braun
 * @date 3/4/11
 */
class HostConfigSection extends SimpleLHSSection {

    public HostConfigSection() {
        super();

        LHSNavItem paths = new LHSNavItem("Paths", "hosts/host-paths");
        LHSNavItem jvms = new LHSNavItem("Virtual Machines", "hosts/host-interfaces");
        LHSNavItem sockets = new LHSNavItem("Socket Binding Groups", "hosts/host-socket-bindings");
        LHSNavItem properties = new LHSNavItem("System Properties", "host/host-properties");

        addNavItems(paths, jvms, sockets, properties);
    }

}