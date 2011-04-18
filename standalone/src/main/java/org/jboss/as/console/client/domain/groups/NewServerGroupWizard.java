/*
 * JBoss, Home of Professional Open Source
 * Copyright <YEAR> Red Hat Inc. and/or its affiliates and other contributors
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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.widgets.DefaultButton;
import org.jboss.as.console.client.widgets.DialogueOptions;
import org.jboss.as.console.client.widgets.forms.ComboBoxItem;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.FormValidation;
import org.jboss.as.console.client.widgets.forms.TextBoxItem;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/1/11
 */
class NewServerGroupWizard {

    VerticalPanel layout;

    public NewServerGroupWizard(final ServerGroupPresenter presenter, final List<ServerGroupRecord> existing) {
        layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");
        layout.getElement().setAttribute("cellpadding", "10");

        final Form<ServerGroupRecord> form = new Form(ServerGroupRecord.class);

        TextBoxItem nameField = new TextBoxItem("groupName", "Group Name")
        {
            @Override
            public boolean validate(String value) {
                boolean hasValue = super.validate(value);
                boolean hasWhitespace = value.contains(" ");
                return hasValue && !hasWhitespace;
            }

            @Override
            public String getErrMessage() {
                return "Not empty, no whitespace";
            }
        };

        final ComboBoxItem basedOnSelection = new ComboBoxItem("based-on", "Based On");

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

        layout.add(new HTML("Create a new server group based on an existing one. " +
                "The new group will inherit the properties of the selected group."));
        layout.add(formWidget);

        layout.add(options);

    }

    public Widget asWidget() {
        return layout;
    }
}
