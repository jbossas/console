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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.events.ProfileSelectionEvent;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.shared.model.SubsystemRecord;
import org.jboss.as.console.client.shared.subsys.SubsystemTreeBuilder;
import org.jboss.as.console.client.widgets.ComboBox;
import org.jboss.as.console.client.widgets.DisclosureStackHeader;
import org.jboss.as.console.client.widgets.LHSNavTree;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
class ProfileSection {

    private LHSNavTree subsysTree;
    private ComboBox selection;
    private DisclosurePanel panel;

    public ProfileSection()  {

        panel = new DisclosureStackHeader(Console.CONSTANTS.common_label_subsystems()).asWidget();
        subsysTree = new LHSNavTree("profiles");
        panel.setContent(subsysTree);

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        selection = new ComboBox();
        selection.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                fireProfileSelection(event.getValue());
            }
        });

        Widget dropDown = selection.asWidget();

        HorizontalPanel horz = new HorizontalPanel();
        horz.getElement().setAttribute("width", "100%");
        HTML title = new HTML(Console.CONSTANTS.common_label_profile()+":&nbsp;");
        horz.add(title);
        horz.add(dropDown);

        title.getElement().getParentElement().setAttribute("class", "profile-selector");

        layout.add(horz);
        layout.add(subsysTree);

        panel.setContent(layout);

    }

    public Widget asWidget()
    {
        return panel;
    }

    private void fireProfileSelection(String profileName) {
        Console.MODULES.getEventBus().fireEvent(new ProfileSelectionEvent(profileName));
    }

    public void updateProfiles(final List<ProfileRecord> profileRecords) {

        selection.clearValues();

        for(ProfileRecord record : profileRecords)
        {
            selection.addItem(record.getName());
        }

        // select first option when updated
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {

                int selectedIndex = 0;
                for(int i=0; i<selection.getItemCount(); i++)
                {
                    if("preview-base".equals(selection.getValue(i)))     // domain-preview.xml
                    {
                        selectedIndex=i;
                        break;
                    }
                    else if("web-base".equals(selection.getValue(i)))     // domain.xml
                    {
                        selectedIndex=i;
                        break;
                    }

                }

                selection.setItemSelected(selectedIndex, true);
                fireProfileSelection(selection.getSelectedValue());
            }
        });

    }

    public void updateSubsystems(List<SubsystemRecord> subsystems) {

        subsysTree.removeItems();

        SubsystemTreeBuilder.build("domain/profile/", subsysTree, subsystems);
    }
}
