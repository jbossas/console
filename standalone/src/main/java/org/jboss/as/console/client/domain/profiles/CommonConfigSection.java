package org.jboss.as.console.client.domain.profiles;

import org.jboss.as.console.client.widgets.LHSNavItem;
import org.jboss.as.console.client.widgets.SimpleLHSSection;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
class CommonConfigSection extends SimpleLHSSection {

    public CommonConfigSection() {
        super();

        LHSNavItem paths = new LHSNavItem("Paths", "domain/paths");
        LHSNavItem interfaces = new LHSNavItem("Interfaces", "domain/domain-interfaces");
        LHSNavItem sockets = new LHSNavItem("Socket Binding Groups", "domain/socket-bindings");
        LHSNavItem properties = new LHSNavItem("System Properties", "domain/domain-properties");

        addNavItems(paths, interfaces, sockets, properties);
    }

}
