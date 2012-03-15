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

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.model.SubsystemRecord;
import org.jboss.as.console.client.shared.subsys.SubsystemTreeBuilder;
import org.jboss.ballroom.client.layout.LHSTreeSection;
import org.jboss.ballroom.client.layout.LHSNavTree;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
class SubsystemSection {

    private LHSTreeSection subsysTree;

    private ProfileSelector profileSelector;
    private VerticalPanel layout;

    public SubsystemSection()  {


        LHSNavTree navigation = new LHSNavTree("profiles");
        navigation.getElement().setAttribute("aria-label", "Profile Tasks");

        subsysTree = new LHSTreeSection(Console.CONSTANTS.common_label_subsystems());
        navigation.addItem(subsysTree);

        layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");


        // ------------

        profileSelector = new ProfileSelector();
        Widget selectorWidget = profileSelector.asWidget();
        layout.add(selectorWidget);

        layout.add(navigation);

    }

    public LHSTreeSection getSubsysTree() {
        return subsysTree;
    }

    public Widget asWidget()
    {
        return layout;
    }

    public void updateSubsystems(List<SubsystemRecord> subsystems) {

        subsysTree.removeItems();

        SubsystemTreeBuilder.build(subsysTree, subsystems);
    }

    public void setProfiles(List<String> profileNames) {
        profileSelector.setProfiles(profileNames);
    }
}
