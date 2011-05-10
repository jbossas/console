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

import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.subsys.messaging.model.MessagingProvider;
import org.jboss.as.console.client.widgets.ContentGroupLabel;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.RHSContentPanel;
import org.jboss.as.console.client.widgets.forms.CheckBoxItem;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.TextItem;


/**
 * @author Heiko Braun
 * @date 5/10/11
 */
public class MessagingView extends DisposableViewImpl implements MessagingPresenter.MyView{

    private MessagingPresenter presenter;
    private Form<MessagingProvider> form;
    private SecurityDetails secDetails;
    private AddressingDetails addrDetails;

    @Override
    public Widget createWidget() {

        RHSContentPanel layout = new RHSContentPanel("Messaging Provider");

        layout.add(new ContentHeaderLabel("Messaging Subsystem Configuration"));

        layout.add(new ContentGroupLabel("Attributes"));

        form = new Form(MessagingProvider.class);
        form.setNumColumns(2);

        TextItem name = new TextItem("name", "Provider");
        CheckBoxItem persistenceItem = new CheckBoxItem("persistenceEnabled", "Persistence enabled?");

        TextItem connector = new TextItem("connectorBinding", "Connector Binding");
        TextItem acceptor = new TextItem("acceptorBinding", "Acceptor Binding");

        form.setFields(name, connector, persistenceItem, acceptor);
        layout.add(form.asWidget());

        // ------

        layout.add(new ContentGroupLabel("Subresources"));

        TabPanel bottomLayout = new TabPanel();
        bottomLayout.addStyleName("default-tabpanel");
        bottomLayout.getElement().setAttribute("style", "padding-top:20px;");

        secDetails = new SecurityDetails(presenter);
        bottomLayout.add(secDetails.asWidget(), "Security");

        addrDetails = new AddressingDetails(presenter);
        bottomLayout.add(addrDetails.asWidget(), "Addressing");

        bottomLayout.selectTab(0);

        layout.add(bottomLayout);

        return layout;
    }

    @Override
    public void setPresenter(MessagingPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setProviderDetails(MessagingProvider provider) {
        form.edit(provider);
        form.setEnabled(false);

        secDetails.setProvider(provider);
        addrDetails.setProvider(provider);

    }

    @Override
    public void editSecDetails(boolean b) {
        secDetails.setEnabled(b);
    }

    @Override
    public void editAddrDetails(boolean b) {
        addrDetails.setEnabled(b);
    }
}
