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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.help.StaticHelpPanel;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.dmr.client.ModelNode;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/1/11
 */
class NewServerGroupWizard {

    private ServerGroupPresenter presenter;
    private List<ServerGroupRecord> existingGroups;
    private List<ProfileRecord> existingProfiles;
    private List<String> existingSockets;

    public NewServerGroupWizard(
            final ServerGroupPresenter presenter,
            final List<ServerGroupRecord> existing,
            List<ProfileRecord> existingProfiles,
            List<String> existingSockets) {
        this.presenter = presenter;
        this.existingGroups = existing;
        this.existingProfiles = existingProfiles;
        this.existingSockets = existingSockets;

    }

    public Widget asWidget() {

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");

        final Form<ServerGroupRecord> form = new Form(ServerGroupRecord.class);

        TextBoxItem nameField = new TextBoxItem("groupName", Console.CONSTANTS.common_label_name())
        {
            @Override
            public boolean validate(String value) {
                boolean hasValue = super.validate(value);
                boolean hasWhitespace = value.contains(" ");
                return hasValue && !hasWhitespace;
            }

            @Override
            public String getErrMessage() {
                return Console.MESSAGES.common_validation_notEmptyNoSpace();
            }
        };

        /*final ComboBoxItem basedOnSelection = new ComboBoxItem("based-on", Console.CONSTANTS.common_label_basedOn());
        basedOnSelection.setDefaultToFirstOption(true);

        if(existingGroups.isEmpty())
        {
            basedOnSelection.setValueMap(new String[]{"none"});
        }
        else
        {
            String[] exists = new String[existingGroups.size()];
            int i=0;
            for(ServerGroupRecord rec : existingGroups)
            {
                exists[i] = rec.getGroupName();
                i++;
            }
            basedOnSelection.setValueMap(exists);
        }      */


        String[] profiles = new String[existingProfiles.size()];
        int i=0;
        for(ProfileRecord rec : existingProfiles)
        {
            profiles[i] = rec.getName();
            i++;
        }

        final ComboBoxItem profileSelection = new ComboBoxItem("profileName", "Profile");
        profileSelection.setDefaultToFirstOption(true);
        profileSelection.setValueMap(profiles);

        final ComboBoxItem socketSelection = new ComboBoxItem("socketBinding", "Socket Binding");
        socketSelection.setDefaultToFirstOption(true);
        socketSelection.setValueMap(existingSockets);

        form.setFields(nameField, profileSelection, socketSelection);

        DialogueOptions options = new DialogueOptions(

                // save
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        // merge base
                        ServerGroupRecord newGroup = form.getUpdatedEntity();

                        FormValidation validation = form.validate();
                        if(validation.hasErrors())
                            return;

                        /*ServerGroupRecord base = null;
                        for(ServerGroupRecord rec : existingGroups)
                        {
                            if(rec.getGroupName().equals(basedOnSelection.getValue()))
                            {
                                base = rec;
                                break;
                            }
                        }

                        if(base!=null)
                        {
                            newGroup.setJvm(base.getJvm());
                            newGroup.setSocketBinding(base.getSocketBinding());
                            newGroup.setProfileName(base.getProfileName());
                            newGroup.setProperties(base.getProperties());
                        }
                          */

                        presenter.createNewGroup(newGroup);

                    }
                },

                // cancel
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        presenter.closeDialoge();
                    }
                }

        );

        // ----------------------------------------

        Widget formWidget = form.asWidget();

        final FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        ModelNode address = new ModelNode();
                        address.add("server-group", "*");
                        return address;
                    }
                }, form
        );

        layout.add(helpPanel.asWidget());

        layout.add(formWidget);

        return new WindowContentBuilder(layout, options).build();
    }
}
