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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.widgets.ContentDescription;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;
import org.jboss.dmr.client.ModelNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/28/11
 */
public class CopyServerWizard {

    private ServerConfigPresenter presenter;
    private Server original;
    private List<Host> hosts;
    private ComboBoxItem hostItem;
    private String currentHost;

    public CopyServerWizard(
            final ServerConfigPresenter presenter, Server orig, List<Host> hosts, String currentHost)
    {
        this.presenter = presenter;
        this.original = orig;
        this.hosts = hosts;
        this.currentHost = currentHost;

    }

    public Widget asWidget() {

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");


        layout.add(new ContentDescription("<h3>Create copy</h3> You are about to create a copy of server-configuration <b>'"+original.getName()+
        "'</b>. Please verify the port offset to avoid conflicts when starting the server."));

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

        nameItem.setValue(original.getName()+"_copy");

        NumberBoxItem portOffset = new NumberBoxItem("portOffset", Console.CONSTANTS.common_label_portOffset());

        // host names

        List<String> hostNames = new ArrayList<String>(hosts.size());
        int preselection = 1;
        int index = 0;
        for(Host item : hosts)
        {
            String hostName = item.getName();
            hostNames.add(hostName);

            if(hostName.equals(currentHost))
                preselection = index;
            index++;

        }

        hostItem = new ComboBoxItem("host", Console.CONSTANTS.common_label_host());
        hostItem.setValueMap(hostNames);
        hostItem.selectItem(preselection+1);

        form.setFields(nameItem, portOffset, hostItem);

        final FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        ModelNode address = new ModelNode();
                        address.add("host", Console.MODULES.getDomainEntityManager().getSelectedHost());
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
                if (!validation.hasErrors())
                {
                    presenter.onSaveCopy(hostItem.getValue(), original, newServer);
                }
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
}
