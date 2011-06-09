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

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.help.StaticHelpPanel;
import org.jboss.as.console.client.widgets.DialogueOptions;
import org.jboss.as.console.client.widgets.forms.ComboBoxItem;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.FormValidation;
import org.jboss.as.console.client.widgets.forms.TextBoxItem;
import org.jboss.dmr.client.ModelNode;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/1/11
 */
class NewServerGroupWizard {

    VerticalPanel layout;
    DockLayoutPanel wrapper;

    public NewServerGroupWizard(final ServerGroupPresenter presenter, final List<ServerGroupRecord> existing) {


        wrapper = new DockLayoutPanel(Style.Unit.PX);

        layout = new VerticalPanel();
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

        final ComboBoxItem basedOnSelection = new ComboBoxItem("based-on", Console.CONSTANTS.common_label_basedOn());

        String[] exists = new String[existing.size()];
        int i=0;
        for(ServerGroupRecord rec : existing)
        {
            exists[i] = rec.getGroupName();
            i++;
        }

        basedOnSelection.setDefaultToFirstOption(true);
        basedOnSelection.setValueMap(exists);

        form.setFields(nameField, basedOnSelection);

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

                        ServerGroupRecord base = null;
                        for(ServerGroupRecord rec : existing)
                        {
                            if(rec.getGroupName().equals(basedOnSelection.getValue()))
                            {
                                base = rec;
                                break;
                            }
                        }

                        newGroup.setJvm(base.getJvm());
                        newGroup.setSocketBinding(base.getSocketBinding());
                        newGroup.setProfileName(base.getProfileName());
                        newGroup.setProperties(base.getProperties());

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

        StaticHelpPanel helpPanel = new StaticHelpPanel(
                Console.MESSAGES.commmon_description_newServerGroup()
        );

        layout.add(helpPanel.asWidget());

        layout.add(formWidget);

        wrapper.addSouth(options, 35);
        wrapper.add(layout);

    }

    public Widget asWidget() {
        return wrapper;
    }
}
