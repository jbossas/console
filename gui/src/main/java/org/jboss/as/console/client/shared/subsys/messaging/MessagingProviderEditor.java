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

package org.jboss.as.console.client.shared.subsys.messaging;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.messaging.model.MessagingProvider;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.dmr.client.ModelNode;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 5/10/11
 */
public class MessagingProviderEditor {

    private MsgDestinationsPresenter presenter;
    private Form<MessagingProvider> form;

    public MessagingProviderEditor(MsgDestinationsPresenter presenter) {
        this.presenter = presenter;
    }

    public Widget asWidget() {

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        form = new Form(MessagingProvider.class);
        form.setNumColumns(2);

        CheckBoxItem persistenceItem = new CheckBoxItem("persistenceEnabled", "Persistence enabled?");
        CheckBoxItem securityItem = new CheckBoxItem("securityEnabled", "Security enabled?");
        CheckBoxItem messageCounterItem = new CheckBoxItem("messageCounterEnabled", "Message Counter enabled?");

        //TextItem connector = new TextItem("connectorBinding", "Connector Binding");
        //TextItem acceptor = new TextItem("acceptorBinding", "Acceptor Binding");

        form.setFields(persistenceItem, securityItem, messageCounterItem);
        //form.setFieldsInGroup("Connections", new DisclosureGroupRenderer(), acceptor, connector);

        FormToolStrip<MessagingProvider> toolStrip = new FormToolStrip<MessagingProvider>(
                form,
                new FormToolStrip.FormCallback<MessagingProvider>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                        presenter.onSaveProviderConfig(changeset);
                    }

                    @Override
                    public void onDelete(MessagingProvider entity) {

                    }
                });
        toolStrip.providesDeleteOp(false);

        FormHelpPanel helpPanel = new FormHelpPanel(new FormHelpPanel.AddressCallback(){
            @Override
            public ModelNode getAddress() {
                ModelNode address = Baseadress.get();
                address.add("subsystem", "messaging");
                address.add("hornetq-server", "*");
                return address;
            }
        }, form);


        Widget formWidget = form.asWidget();
        form.setEnabled(false);

        layout.add(toolStrip.asWidget());
        layout.add(helpPanel.asWidget());
        layout.add(formWidget);

        return layout;
    }


    public void setProviderDetails(MessagingProvider provider) {

        form.edit(provider);
        form.setEnabled(false);

    }
}
