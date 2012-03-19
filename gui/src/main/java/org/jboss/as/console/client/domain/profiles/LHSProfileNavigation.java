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

import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.shared.model.SubsystemRecord;
import org.jboss.as.console.client.shared.subsys.SubsystemTreeBuilder;
import org.jboss.ballroom.client.layout.LHSNavTree;
import org.jboss.ballroom.client.layout.LHSNavTreeItem;
import org.jboss.ballroom.client.layout.LHSTreeSection;

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

    private ScrollPanel scroll;
    private LHSNavTree navigation;
    private LHSTreeSection subsystemLeaf;
    private LHSTreeSection groupsLeaf;
    private LHSTreeSection commonLeaf;
    private ProfileSelector profileSelector;

    public LHSProfileNavigation() {

        layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        stack = new VerticalPanel();
        stack.setStyleName("fill-layout-width");


        profileSelector = new ProfileSelector();
        Widget selectorWidget = profileSelector.asWidget();
        stack.add(selectorWidget);

        navigation = new LHSNavTree("profiles");
        navigation.getElement().setAttribute("aria-label", "Profile Tasks");

        subsystemLeaf = new LHSTreeSection(Console.CONSTANTS.common_label_subsystems());
        navigation.addItem(subsystemLeaf);

        groupsLeaf = new LHSTreeSection(Console.CONSTANTS.common_label_serverGroups());
        navigation.addItem(groupsLeaf);

        LHSNavTreeItem groupItem = new LHSNavTreeItem(Console.CONSTANTS.common_label_serverGroupConfigurations(), NameTokens.ServerGroupPresenter);
        groupsLeaf.addItem(groupItem);


        // --------

        commonLeaf = new LHSTreeSection(Console.CONSTANTS.common_label_generalConfig());
        navigation.addItem(commonLeaf);

        LHSNavTreeItem interfaces = new LHSNavTreeItem(Console.CONSTANTS.common_label_interfaces(), NameTokens.InterfacePresenter);
        LHSNavTreeItem sockets = new LHSNavTreeItem(Console.CONSTANTS.common_label_socketBinding(), NameTokens.SocketBindingPresenter);
        LHSNavTreeItem properties = new LHSNavTreeItem(Console.CONSTANTS.common_label_systemProperties(), NameTokens.PropertiesPresenter);

        commonLeaf.addItem(interfaces);
        commonLeaf.addItem(sockets);
        commonLeaf.addItem(properties);

        navigation.expandTopLevel();

        stack.add(navigation);

        layout.add(stack);

        scroll = new ScrollPanel(layout);
    }

    public Widget asWidget()
    {
        return scroll;
    }

    public void updateSubsystems(List<SubsystemRecord> subsystems) {

        //subsystemSection.updateSubsystems(subsystems);

        subsystemLeaf.removeItems();

        SubsystemTreeBuilder.build(subsystemLeaf, subsystems);
    }


    public void setProfiles(List<ProfileRecord> profiles) {

        List<String> profileNames = new ArrayList<String>(profiles.size());
        for(ProfileRecord p :profiles)
        {
            profileNames.add(p.getName());
        }

        profileSelector.setProfiles(profileNames);

    }
}
