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

package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.widgets.DialogueOptions;
import org.jboss.as.console.client.widgets.WindowContentBuilder;
import org.jboss.as.console.client.widgets.forms.CheckBoxItem;
import org.jboss.as.console.client.widgets.forms.ComboBoxItem;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.FormValidation;
import org.jboss.as.console.client.widgets.forms.NumberBoxItem;
import org.jboss.as.console.client.widgets.forms.TextBoxItem;
import org.jboss.dmr.client.ModelNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/28/11
 */
public class NewServerConfigWizard {

    private ServerConfigPresenter presenter;
    private ComboBoxItem groupItem;
    private List<ServerGroupRecord> serverGroups;

    public NewServerConfigWizard(final ServerConfigPresenter presenter, final List<ServerGroupRecord> serverGroups) {
        this.presenter = presenter;
        this.serverGroups = serverGroups;
    }

    public Widget asWidget() {

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");

        final Form<Server> form = new Form<Server>(Server.class);
        form.setNumColumns(1);

        TextBoxItem nameItem = new TextBoxItem("name", Console.CONSTANTS.common_label_name())
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

        CheckBoxItem startedItem = new CheckBoxItem("autoStart", Console.CONSTANTS.common_label_autoStart());

        // 'socket-binding-group' inherited from group
        // 'jvm' inherited from group

        NumberBoxItem portOffset = new NumberBoxItem("portOffset", Console.CONSTANTS.common_label_portOffset());

        List<String> groups = new ArrayList<String>(serverGroups.size());
        for(ServerGroupRecord rec : serverGroups)
            groups.add(rec.getGroupName());

        groupItem = new ComboBoxItem("group", Console.CONSTANTS.common_label_serverGroup());
        groupItem.setDefaultToFirstOption(true);
        groupItem.setValueMap(groups);

        form.setFields(nameItem, groupItem, portOffset, startedItem);

        final FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        ModelNode address = new ModelNode();
                        address.add("host", Console.MODULES.getCurrentSelectedHost().getName());
                        address.add("server-config", "*");
                        return address;
                    }
                }, form
        );
        layout.add(helpPanel.asWidget());

        layout.add(form.asWidget());


        // ---

        ClickHandler saveHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                final Server newServer = form.getUpdatedEntity();

                FormValidation validation = form.validate();
                if (validation.hasErrors())
                    return;

                // merge inherited values
                ServerGroupRecord selectedGroup =
                        getSelectedServerGroup(serverGroups, newServer.getGroup());
                newServer.setSocketBinding(selectedGroup.getSocketBinding());
                newServer.setJvm(selectedGroup.getJvm());

                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        presenter.createServerConfig(newServer);
                    }
                });


            }
        };


        ClickHandler cancelHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.closeDialoge();
            }
        };

        DialogueOptions options = new DialogueOptions(saveHandler, cancelHandler);

        return new WindowContentBuilder(layout, options).build();

    }

    private ServerGroupRecord getSelectedServerGroup(List<ServerGroupRecord> available, String selectedName)
    {
        ServerGroupRecord match = null;
        for(ServerGroupRecord rec : available)
        {
            if(rec.getGroupName().equals(selectedName))
            {
                match = rec;
                break;
            }
        }

        return match;
    }
}
