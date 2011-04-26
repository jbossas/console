/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.Server;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/2/11
 */
class LHSHostsNavigation implements HostSelectionEvent.HostSelectionListener {

    private static final int SELECTOR_HEIGHT = 60;
    private static final int HEADER_SIZE = 28;

    private ServersConfigSection serversSection;
    private ServerInstancesSection instanceSection;
    private HostConfigSection hostConfigSection;

    private HostSelector selector;
    private VerticalPanel stack;
    private DockLayoutPanel layout;

    public LHSHostsNavigation() {

        layout = new DockLayoutPanel(Style.Unit.PX);
        layout.setStyleName("fill-layout");
        layout.getElement().setAttribute("style", "width:99%;border-right:1px solid #E0E0E0");

        selector = new HostSelector();
        final Widget selectorWidget = selector.asWidget();

        stack = new VerticalPanel();
        stack.setStyleName("fill-layout-width");

        serversSection = new ServersConfigSection();
        stack.add(serversSection.asWidget());

        instanceSection = new ServerInstancesSection();
        stack.add(instanceSection.asWidget());

        hostConfigSection = new HostConfigSection();
        stack.add(hostConfigSection.asWidget());

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
