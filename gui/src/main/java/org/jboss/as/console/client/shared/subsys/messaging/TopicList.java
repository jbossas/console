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
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.subsys.messaging.model.JMSEndpoint;
import org.jboss.as.console.client.shared.subsys.messaging.model.Topic;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;

import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 5/10/11
 */
public class TopicList {

    private EndpointTable table;
    private ListDataProvider<JMSEndpoint> endpointProvider;

    private MsgDestinationsPresenter presenter;
    private Form<Topic> form;

    public TopicList(MsgDestinationsPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();

        form = new Form(Topic.class);
        form.setNumColumns(2);

        FormToolStrip<Topic> formTools = new FormToolStrip<Topic>(
                form,
                new FormToolStrip.FormCallback<Topic>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                        presenter.onSaveTopic(form.getEditedEntity().getName(), form.getChangedValues());
                    }

                    @Override
                    public void onDelete(Topic entity) {

                    }
                }
        );

        ToolStrip tableTools = new ToolStrip();

        ToolButton addBtn = new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewTopicDialogue();
            }
        });
        addBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_add_topicList());
        tableTools.addToolButtonRight(addBtn);

        ToolButton removeBtn = new ToolButton(Console.CONSTANTS.common_label_delete(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                Feedback.confirm(
                        Console.MESSAGES.deleteTitle("Topic"),
                        Console.MESSAGES.deleteConfirm("Topic"),
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed)
                                    presenter.onDeleteTopic(form.getEditedEntity());
                            }
                        });


            }
        });

        tableTools.addToolButtonRight(removeBtn);

        layout.add(tableTools.asWidget());

        // -----
        table = new EndpointTable();
        endpointProvider = new ListDataProvider<JMSEndpoint>();
        endpointProvider.addDataDisplay(table);

        layout.add(table);
        table.getElement().setAttribute("style", "margin-bottom:15px;");


        // -----

        TextItem name = new TextItem("name", "Name");
        TextItem jndi = new TextItem("jndiName", "JNDI");

        form.setFields(name, jndi);

        Widget formToolsWidget = formTools.asWidget();
        formToolsWidget.getElement().setAttribute("style", "padding-top:15px;");

        layout.add(formToolsWidget);
        layout.add(form.asWidget());

        form.bind(table);

        return layout;
    }

    public void setTopics(List<JMSEndpoint> topics) {

        endpointProvider.setList(topics);

        table.selectDefaultEntity();
    }

    public void setEnabled(boolean b) {
        form.setEnabled(b);
    }
}
