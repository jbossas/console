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
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.subsys.messaging.model.ConnectionFactory;
import org.jboss.as.console.client.shared.subsys.messaging.model.JMSEndpoint;
import org.jboss.as.console.client.shared.subsys.messaging.model.MessagingProvider;
import org.jboss.as.console.client.shared.subsys.messaging.model.Queue;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 5/10/11
 */
public class MessagingView extends SuspendableViewImpl implements MessagingPresenter.MyView, MessagingPresenter.JMSView{

    private MessagingPresenter presenter;
    private MessagingProviderEditor providerEditor;
    private JMSEditor jmsEditor;

    @Override
    public Widget createWidget() {

        TabLayoutPanel tabLayoutpanel = new TabLayoutPanel(25, Style.Unit.PX);
        tabLayoutpanel.addStyleName("default-tabpanel");

        providerEditor = new MessagingProviderEditor(presenter);
        jmsEditor = new JMSEditor(presenter);

        tabLayoutpanel.add(jmsEditor.asWidget(), "JMS Destinations");
        tabLayoutpanel.add(providerEditor.asWidget(), "JMS Provider");

        tabLayoutpanel.selectTab(0);

        return tabLayoutpanel;
    }

    @Override
    public void setPresenter(MessagingPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setProviderDetails(MessagingProvider provider) {
        providerEditor.setProviderDetails(provider);
    }

    @Override
    public void editSecDetails(boolean b) {
        providerEditor.editSecDetails(b);
    }

    @Override
    public void editAddrDetails(boolean b) {
        providerEditor.editAddrDetails(b);
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
}
