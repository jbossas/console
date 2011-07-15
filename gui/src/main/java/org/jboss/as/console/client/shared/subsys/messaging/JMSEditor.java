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

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.help.StaticHelpPanel;
import org.jboss.as.console.client.shared.subsys.messaging.model.ConnectionFactory;
import org.jboss.as.console.client.shared.subsys.messaging.model.JMSEndpoint;
import org.jboss.as.console.client.shared.subsys.messaging.model.MessagingDescription;
import org.jboss.as.console.client.shared.subsys.messaging.model.Queue;
import org.jboss.as.console.client.widgets.ContentGroupLabel;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.icons.Icons;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/29/11
 */
public class JMSEditor implements MessagingPresenter.JMSView{

    private MessagingPresenter presenter;
    private TopicList topicList;
    private QueueList queueList;

    private DefaultCellTable<ConnectionFactory> factoryTable;

    public JMSEditor(MessagingPresenter presenter) {
        this.presenter = presenter;
    }

    public Widget asWidget() {

        LayoutPanel layout = new LayoutPanel();

        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("rhs-content-panel");

        ScrollPanel scroll = new ScrollPanel(panel);
        layout.add(scroll);

        layout.setWidgetTopHeight(scroll, 0, Style.Unit.PX, 100, Style.Unit.PCT);

        // ---

        HorizontalPanel horzPanel = new HorizontalPanel();
        horzPanel.getElement().setAttribute("style", "width:100%;");
        Image image = new Image(Icons.INSTANCE.messaging());
        horzPanel.add(image);
        horzPanel.add(new ContentHeaderLabel("JMS Subsystem Configuration"));
        image.getElement().getParentElement().setAttribute("width", "25");

        panel.add(horzPanel);

        // ----

        panel.add(new ContentGroupLabel("Connection Factories"));

        factoryTable = new DefaultCellTable<ConnectionFactory>(10);

        Column<ConnectionFactory, String> nameColumn = new Column<ConnectionFactory, String>(new TextCell()) {
            @Override
            public String getValue(ConnectionFactory object) {
                return object.getName();
            }
        };

        Column<ConnectionFactory, String> jndiColumn = new Column<ConnectionFactory, String>(new TextCell()) {
            @Override
            public String getValue(ConnectionFactory object) {
                return object.getJndiName();
            }
        };

        factoryTable.addColumn(nameColumn, "Name");
        factoryTable.addColumn(jndiColumn, "JNDI");

        StaticHelpPanel helpPanel = new StaticHelpPanel(MessagingDescription.getFactoryDescription());

        panel.add(helpPanel.asWidget());

        panel.add(factoryTable);

        // ----

        panel.add(new ContentGroupLabel("Subresources"));
        TabPanel bottomLayout = new TabPanel();
        bottomLayout.addStyleName("default-tabpanel");
        bottomLayout.getElement().setAttribute("style", "padding-top:20px;");

        queueList = new QueueList(presenter);
        bottomLayout.add(queueList.asWidget(),"Queues");

        topicList = new TopicList(presenter);
        bottomLayout.add(topicList.asWidget(),"Topics");

        bottomLayout.selectTab(0);

        panel.add(bottomLayout);

        return layout;
    }

    @Override
    public void setTopics(List<JMSEndpoint> topics) {
        topicList.setTopics(topics);
    }

    public void setQueues(List<Queue> queues) {
        queueList.setQueues(queues);
    }

    @Override
    public void setConnectionFactories(List<ConnectionFactory> factories) {
        factoryTable.setRowCount(factories.size(), true);
        factoryTable.setRowData(0, factories);
    }

    @Override
    public void enableEditQueue(boolean b) {
        queueList.setEnabled(b);
    }

    @Override
    public void enableEditTopic(boolean b) {
        topicList.setEnabled(b);
    }
}
