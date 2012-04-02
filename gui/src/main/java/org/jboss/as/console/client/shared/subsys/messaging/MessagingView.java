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

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.subsys.messaging.model.AddressingPattern;
import org.jboss.as.console.client.shared.subsys.messaging.model.ConnectionFactory;
import org.jboss.as.console.client.shared.subsys.messaging.model.JMSEndpoint;
import org.jboss.as.console.client.shared.subsys.messaging.model.MessagingProvider;
import org.jboss.as.console.client.shared.subsys.messaging.model.Queue;
import org.jboss.as.console.client.shared.subsys.messaging.model.SecurityPattern;
import org.jboss.as.console.client.widgets.pages.PagedView;
import org.jboss.ballroom.client.widgets.tabs.FakeTabPanel;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 5/10/11
 */
public class MessagingView extends SuspendableViewImpl implements MsgDestinationsPresenter.MyView, MsgDestinationsPresenter.JMSView{

    private MsgDestinationsPresenter presenter;

    private JMSEditor jmsEditor;
    private PagedView panel;
    private ProviderList providerList;

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new LayoutPanel();

        FakeTabPanel titleBar = new FakeTabPanel("Messaging Destinations");
        layout.add(titleBar);

        panel = new PagedView();

        providerList = new ProviderList(presenter);
        jmsEditor = new JMSEditor(presenter);

        panel.addPage(Console.CONSTANTS.common_label_back(), providerList.asWidget());
        panel.addPage("Queues/Topics", jmsEditor.asWidget()) ;
        panel.addPage("Connection Factories", new HTML()) ;
        panel.addPage("Security Settings", new HTML()) ;
        panel.addPage("Addressing Settings", new HTML()) ;
        panel.addPage("Diverts", new HTML()) ;

        // default page
        panel.showPage(0);

        Widget panelWidget = panel.asWidget();
        layout.add(panelWidget);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 40, Style.Unit.PX);
        layout.setWidgetTopHeight(panelWidget, 40, Style.Unit.PX, 100, Style.Unit.PCT);

        return layout;
    }

    @Override
    public void setPresenter(MsgDestinationsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setProviderDetails(MessagingProvider provider) {
        //providerList.setProviderDetails(provider);
    }

    @Override
    public void setQueues(List<Queue> queues) {
        jmsEditor.setQueues(queues);
    }

    @Override
    public void setTopics(List<JMSEndpoint> topics) {
        jmsEditor.setTopics(topics);
    }

    @Override
    public void setConnectionFactories(List<ConnectionFactory> factories) {
        jmsEditor.setConnectionFactories(factories);
    }

    @Override
    public void enableEditQueue(boolean b) {
        jmsEditor.enableEditQueue(b);
    }

    @Override
    public void enableEditTopic(boolean b) {
        jmsEditor.enableEditTopic(b);
    }

    @Override
    public void setSecurityConfig(List<SecurityPattern> secPatterns) {
        //providerEditor.setSecurityConfig(secPatterns);
    }

    @Override
    public void setAddressingConfig(List<AddressingPattern> addrPatterns) {
        //providerEditor.setAddressingConfig(addrPatterns);
    }

    @Override
    public void setProvider(List<String> result) {
        providerList.setProvider(result);
    }

    @Override
    public void setSelectedProvider(String selectedProvider) {


        if(null==selectedProvider)
        {
            panel.showPage(0);
        }
        else{

            presenter.loadDetails(selectedProvider);

            // move to first page if still showing overview
            if(0==panel.getPage())
                panel.showPage(1);
        }
    }


}
