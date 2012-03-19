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

package org.jboss.as.console.client.shared.subsys.web;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.web.model.HttpConnector;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;
import org.jboss.dmr.client.ModelNode;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 5/12/11
 */
public class NewConnectorWizard {
    private WebPresenter presenter;
    private List<HttpConnector> connectors;
    private List<String> socketBindings;

    public NewConnectorWizard(WebPresenter presenter, List<HttpConnector> connectors, List<String> socketBindings) {
        this.presenter = presenter;
        this.connectors = connectors;
        this.socketBindings = socketBindings;
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");
        final Form<HttpConnector> form = new Form<HttpConnector>(HttpConnector.class);

        TextBoxItem name = new TextBoxItem("name", "Name");

        ComboBoxItem socket = new ComboBoxItem("socketBinding", "Socket Binding") {

            private String errOrig;

            @Override
            public boolean validate(String value) {


                boolean parentValid = super.validate(value);
                boolean bindingValid = true;
                if(parentValid)
                {
                    for(HttpConnector existing : connectors)
                    {
                        if(existing.getSocketBinding().equals(value))
                        {
                            errOrig = getErrMessage();
                            setErrMessage("Socket binding already in use");
                            bindingValid = false;
                        }
                    }
                }

                return parentValid && bindingValid;
            }
        };
        socket.setValueMap(socketBindings);



        ComboBoxItem protocol = new ComboBoxItem("protocol", "Protocol");
        ComboBoxItem scheme = new ComboBoxItem("scheme", "Scheme");

        protocol.setDefaultToFirstOption(true);
        protocol.setValueMap(new String[]{"HTTP/1.1", "AJP/1.3"});

        scheme.setDefaultToFirstOption(true);
        scheme.setValueMap(new String[]{"http", "https"});

        CheckBoxItem enabled = new CheckBoxItem("enabled", "Enabled?");
        enabled.setValue(Boolean.TRUE);

        form.setFields(name,socket,protocol,scheme, enabled);

        final FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        ModelNode address = Baseadress.get();
                        address.add("subsystem", "web");
                        address.add("connector", "*");
                        return address;
                    }
                }, form
        );
        layout.add(helpPanel.asWidget());


        layout.add(form.asWidget());

        DialogueOptions options = new DialogueOptions(
                new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {

                        FormValidation validation = form.validate();
                        if(!validation.hasErrors())
                            presenter.onCreateConnector(form.getUpdatedEntity());
                    }
                },
                new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        presenter.closeDialogue();
                    }
                }
        );

        layout.add(options);

        return new WindowContentBuilder(layout, options).build();
    }
}
