package org.jboss.as.console.client.domain.profiles;

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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.events.ProfileSelectionEvent;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.ballroom.client.widgets.forms.ComboBox;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/4/11
 */
public class ProfileSelector {

    private ComboBox profileSelection;
    private HTMLPanel panel;

    public ProfileSelector() {


        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        builder.appendHtmlConstant("<div class='host-selector'>");
        builder.appendHtmlConstant("<div class='host-selector-header'>Profile:</div>");
        builder.appendHtmlConstant("<div id='host-selector-content'/>");
        builder.appendHtmlConstant("</div>");

        panel = new HTMLPanel(builder.toSafeHtml());
        profileSelection = new ComboBox();
        profileSelection.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                fireProfileSelection(event.getValue());
            }
        });
        panel.add(profileSelection.asWidget(), "host-selector-content");

    }

    public Widget asWidget() {
        return panel;
    }

    private void fireProfileSelection(String name) {
       Console.MODULES.getEventBus().fireEvent(new ProfileSelectionEvent(name));
    }

    public void updateProfiles(final List<ProfileRecord> profileRecords) {

        profileSelection.clearValues();

        for(ProfileRecord record : profileRecords)
        {
            profileSelection.addItem(record.getName());
        }

        // select first option when updated
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {

                int selectedIndex = 0;
                for(int i=0; i<profileSelection.getItemCount(); i++)
                {
                    if("preview-base".equals(profileSelection.getValue(i)))     // domain-preview.xml
                    {
                        selectedIndex=i;
                        break;
                    }
                    else if("web-base".equals(profileSelection.getValue(i)))     // domain.xml
                    {
                        selectedIndex=i;
                        break;
                    }

                }

                profileSelection.setItemSelected(selectedIndex, true);
                String selectedProfile = profileSelection.getSelectedValue();

                // just to make sure
                Console.MODULES.getCurrentSelectedProfile().setName(selectedProfile);

                // this will rebuild the tree
                fireProfileSelection(selectedProfile);
            }
        });

    }

}
