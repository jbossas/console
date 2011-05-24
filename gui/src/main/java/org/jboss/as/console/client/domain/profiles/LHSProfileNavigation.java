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

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.model.SubsystemRecord;

import java.util.List;

/**
 * LHS domain management navigation.
 *
 * @author Heiko Braun
 * @date 2/11/11
 */
class LHSProfileNavigation {

    private LayoutPanel layout;
    private VerticalPanel stack;

    private ProfileSection profileSection;

    public LHSProfileNavigation() {

        layout = new LayoutPanel();
        layout.getElement().setAttribute("style", "width:99%;border-right:1px solid #E0E0E0");
        layout.setStyleName("fill-layout");

        stack = new VerticalPanel();
        stack.setStyleName("fill-layout-width");

        profileSection = new ProfileSection();
        stack.add(profileSection.asWidget());

        CommonConfigSection commonSection = new CommonConfigSection();
        stack.add(commonSection.asWidget());

        layout.add(stack);

    }

    public Widget asWidget()
    {
        return layout;
    }

    public void updateSubsystems(List<SubsystemRecord> subsystems) {

        profileSection.updateSubsystems(subsystems);
    }

    public void updateProfiles(List<ProfileRecord> profiles) {

        profileSection.updateProfiles(profiles);
    }

    public void updateServerGroups(List<ServerGroupRecord> serverGroupRecords) {
        //serverGroupSection.updateSubsystems(serverGroupRecords);
    }

}
