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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.subsys.messaging.model.JMSEndpoint;
import org.jboss.as.console.client.shared.subsys.messaging.model.Topic;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tools.ToolButton;

import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 5/10/11
 */
public class TopicList {

    private EndpointTable table;
    private MessagingPresenter presenter;
    private Form<Topic> form;

    public TopicList(MessagingPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();

        form = new Form(Topic.class);
        form.setNumColumns(2);

        FormToolStrip<Topic> toolStrip = new FormToolStrip<Topic>(
                form,
                new FormToolStrip.FormCallback<Topic>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                        presenter.onSaveTopic(form.getEditedEntity().getName(), form.getChangedValues());
                    }

                    @Override
                    public void onDelete(Topic entity) {
                        presenter.onDeleteTopic(entity);
                    }
                }
        );

        ToolButton addBtn = new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewTopicDialogue();
            }
        });
        addBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_add_topicList());
        toolStrip.addToolButtonRight(addBtn);

        layout.add(toolStrip.asWidget());

        // -----
        table = new EndpointTable();

        layout.add(table);


        // -----

        TextItem name = new TextItem("name", "Name");
        TextItem jndi = new TextItem("jndiName", "JNDI");

        form.setFields(name, jndi);

        Widget formWidget = form.asWidget();
        formWidget.getElement().setAttribute("style", "padding-top:15px;");
        layout.add(formWidget);

        form.bind(table);

        return layout;
    }

    public void setTopics(List<JMSEndpoint> topics) {

        table.setRowCount(topics.size(),true);
        table.setRowData(0, topics);

        if(!topics.isEmpty())
            table.getSelectionModel().setSelected(topics.get(0), true);
    }

    public void setEnabled(boolean b) {
        form.setEnabled(b);
    }
}
