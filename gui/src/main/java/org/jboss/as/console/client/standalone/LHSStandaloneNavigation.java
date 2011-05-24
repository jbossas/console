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

package org.jboss.as.console.client.standalone;

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.shared.model.SubsystemRecord;
import org.jboss.as.console.client.widgets.DisclosureStackHeader;
import org.jboss.as.console.client.widgets.LHSNavTree;
import org.jboss.as.console.client.widgets.LHSNavTreeItem;

import java.util.List;

/**
 * LHS navigation for standalone server management.
 *
 * @author Heiko Braun
 * @date 2/10/11
 */
public class LHSStandaloneNavigation {

    private VerticalPanel stack;

    private LayoutPanel layout;
    private Tree subsysTree;

    public LHSStandaloneNavigation() {
        super();

        layout = new LayoutPanel();
        layout.getElement().setAttribute("style", "width:99%;border-right:1px solid #E0E0E0");
        layout.setStyleName("fill-layout");

        stack = new VerticalPanel();
        stack.setStyleName("fill-layout-width");

        // ----------------------------------------------------


        subsysTree = new LHSNavTree("profile-standalone");

        DisclosurePanel subsysPanel  = new DisclosureStackHeader("Profile").asWidget();
        subsysPanel.setContent(subsysTree);
        stack.add(subsysPanel);

        // ----------------------------------------------------

        Tree deploymentTree = new LHSNavTree("deployment-standalone");
        deploymentTree.addItem(new LHSNavTreeItem("Manage Deployments", NameTokens.DeploymentListPresenter));
        DisclosurePanel deplPanel  = new DisclosureStackHeader("Deployments").asWidget();
        deplPanel.setContent(deploymentTree);

        stack.add(deplPanel);

        // ----------------------------------------------------

        Tree commonTree = new LHSNavTree("profile-standalone");
        DisclosurePanel commonPanel  = new DisclosureStackHeader("General Configuration").asWidget();
        commonPanel.setContent(commonTree);

        LHSNavTreeItem[] commonItems = new LHSNavTreeItem[] {
                new LHSNavTreeItem("Paths", "server/server-paths"),
                new LHSNavTreeItem("Interfaces", "server/server-interfaces"),
                new LHSNavTreeItem("Socket Binding Groups", "server/server-sockets"),
                new LHSNavTreeItem("System Properties", "server/server-properties")
        };

        for(LHSNavTreeItem item : commonItems)
        {
            commonTree.addItem(item);
        }

        stack.add(commonPanel);

        layout.add(stack);

    }

    public Widget asWidget()
    {
        return layout;
    }

    public void updateFrom(List<SubsystemRecord> subsystems) {

        subsysTree.removeItems();

        for(SubsystemRecord subsys: subsystems)
        {
            // TODO: distinguish domain and standalone properly
            String token = "server/_"+subsys.getTitle().toLowerCase().replace(" ","_");
            TreeItem item = new LHSNavTreeItem(subsys.getTitle(), token);
            subsysTree.addItem(item);
        }

    }
}