/*
 * JBoss, Home of Professional Open Source
 * Copyright <YEAR> Red Hat Inc. and/or its affiliates and other contributors
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