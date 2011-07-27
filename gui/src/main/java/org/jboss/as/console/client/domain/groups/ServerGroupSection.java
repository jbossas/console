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

package org.jboss.as.console.client.domain.groups;

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.ballroom.client.layout.LHSNavTree;
import org.jboss.ballroom.client.layout.LHSNavTreeItem;
import org.jboss.ballroom.client.widgets.stack.DisclosureStackPanel;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
class ServerGroupSection {

    DisclosurePanel panel;
    Tree serverGroupTree;

    public ServerGroupSection() {

        panel = new DisclosureStackPanel(Console.CONSTANTS.common_label_serverGroups()).asWidget();
        serverGroupTree = new LHSNavTree("groups");
        panel.setContent(serverGroupTree);
    }

    public Widget asWidget()
    {
        return panel;
    }

    public void updateFrom(List<ServerGroupRecord> serverGroupRecords) {

        serverGroupTree.removeItems();

        for(ServerGroupRecord record : serverGroupRecords)
        {
            String groupName = record.getGroupName();
            final String token = "domain/" + NameTokens.ServerGroupPresenter + ";name=" + groupName;
            final TreeItem item = new LHSNavTreeItem(groupName, token);
            serverGroupTree.addItem(item);
        }
    }


}
