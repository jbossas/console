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

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.ballroom.client.layout.LHSNavTree;
import org.jboss.ballroom.client.layout.LHSNavTreeItem;
import org.jboss.ballroom.client.layout.LHSTreeSection;
import org.jboss.ballroom.client.widgets.stack.DisclosureStackPanel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/2/11
 */
class LHSHostsNavigation {

    private VerticalPanel layout;
    private VerticalPanel stack;

    private DisclosurePanel panel;
    private LHSNavTree hostTree;

    private HostSelector hostSelector;
    private ScrollPanel scroll;
    private LHSNavTree navigation;

    public LHSHostsNavigation() {


        layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        stack = new VerticalPanel();
        stack.setStyleName("fill-layout-width");

        // --------

       // VerticalPanel innerlayout = new VerticalPanel();
        //innerlayout.setStyleName("fill-layout-width");

        hostSelector = new HostSelector();
        stack.add(hostSelector.asWidget());

        navigation = new LHSNavTree("profiles");
        navigation.getElement().setAttribute("aria-label", "Profile Tasks");


        LHSTreeSection hostLeaf = new LHSTreeSection(Console.CONSTANTS.common_label_serverConfigs());
        navigation.addItem(hostLeaf);

        //DisclosurePanel hostPanel = new DisclosureStackPanel(Console.CONSTANTS.common_label_serverConfigs(), true).asWidget();
        //hostPanel.setContent(innerlayout);

        //hostTree = new LHSNavTree("hosts");
        //innerlayout.add(hostTree);

        LHSNavTreeItem serversItem = new LHSNavTreeItem(Console.CONSTANTS.common_label_serverConfigs(), NameTokens.ServerPresenter);
        //LHSNavTreeItem paths = new LHSNavTreeItem(Console.CONSTANTS.common_label_paths(), "hosts/host-paths");
        LHSNavTreeItem jvms = new LHSNavTreeItem(Console.CONSTANTS.common_label_virtualMachines(), "host-jvms");
        LHSNavTreeItem interfaces = new LHSNavTreeItem(Console.CONSTANTS.common_label_interfaces(), "host-interfaces");
        LHSNavTreeItem properties = new LHSNavTreeItem("Host Properties", "host-properties");

        hostLeaf.addItem(serversItem);
        hostLeaf.addItem(jvms);
        hostLeaf.addItem(interfaces);
        hostLeaf.addItem(properties);

        stack.add(navigation);

        // --------


        layout.add(stack);

        scroll = new ScrollPanel(layout);

    }

    public Widget asWidget()
    {
        return scroll;
    }

    public void setHosts(List<Host> hosts) {
        List<String> hostNames = new ArrayList<String>(hosts.size());
        for(Host h : hosts)
        {
            hostNames.add(h.getName());
        }

        hostSelector.setHosts(hostNames);

    }
}
