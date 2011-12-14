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

package org.jboss.as.console.client.domain.profiles;

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.model.SubsystemRecord;
import org.jboss.ballroom.client.layout.LHSNavTree;
import org.jboss.ballroom.client.layout.LHSNavTreeItem;
import org.jboss.ballroom.client.widgets.stack.DisclosureStackPanel;

import java.util.ArrayList;
import java.util.List;

/**
 * LHS domain management navigation.
 *
 * @author Heiko Braun
 * @date 2/11/11
 */
class LHSProfileNavigation {

    private VerticalPanel layout;
    private VerticalPanel stack;
    private SubsystemSection subsystemSection;

    public LHSProfileNavigation() {

        layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        stack = new VerticalPanel();
        stack.setStyleName("fill-layout-width");

        subsystemSection = new SubsystemSection();
        Widget subsysWidget = subsystemSection.asWidget();
        stack.add(subsysWidget);

        // -------- groups


        DisclosurePanel groupsPanel = new DisclosureStackPanel("Server Groups").asWidget();
        LHSNavTree groupsTree = new LHSNavTree("profiles");
        groupsPanel.setContent(groupsTree);

        groupsTree.addItem(new LHSNavTreeItem("Group Configurations", NameTokens.ServerGroupPresenter));
        stack.add(groupsPanel);

        // --------

        CommonConfigSection commonSection = new CommonConfigSection();
        stack.add(commonSection.asWidget());

        layout.add(stack);

    }

    public Widget asWidget()
    {
        return layout;
    }

    public void updateSubsystems(List<SubsystemRecord> subsystems) {

        subsystemSection.updateSubsystems(subsystems);
    }

    public void updateServerGroups(List<ServerGroupRecord> serverGroupRecords) {
        //serverGroupSection.updateSubsystems(serverGroupRecords);
    }

    public void setProfiles(List<ProfileRecord> profiles) {

        List<String> profileNames = new ArrayList<String>(profiles.size());
        for(ProfileRecord p :profiles)
        {
            profileNames.add(p.getName());
        }

        subsystemSection.setProfiles(profileNames);

    }
}
