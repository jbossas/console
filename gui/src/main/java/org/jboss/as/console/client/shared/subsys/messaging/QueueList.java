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
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.messaging.model.Queue;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.dmr.client.ModelNode;

import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 5/10/11
 */
public class QueueList {

    private DefaultCellTable<Queue> queueTable;
    private ListDataProvider<Queue> queueProvider;

    private MsgDestinationsPresenter presenter;
    private Form<Queue> form ;

    public QueueList(MsgDestinationsPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {

        VerticalPanel layout = new VerticalPanel();

        form = new Form(Queue.class);
        form.setNumColumns(2);

        FormToolStrip<Queue> formTools = new FormToolStrip<Queue>(
                form,
                new FormToolStrip.FormCallback<Queue>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                        presenter.onSaveQueue(form.getEditedEntity().getName(), form.getChangedValues());
                    }

                    @Override
                    public void onDelete(Queue entity) {

                    }
                }
        );


        ToolStrip tableTools = new ToolStrip();
        ToolButton addBtn = new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewQueueDialogue();
            }
        });
        addBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_add_queueList());
        tableTools.addToolButtonRight(addBtn);

        ToolButton removeBtn = new ToolButton(Console.CONSTANTS.common_label_delete(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                 Feedback.confirm(
                         Console.MESSAGES.deleteTitle("Queue"),
                         Console.MESSAGES.deleteConfirm("Queue"),
                         new Feedback.ConfirmationHandler() {
                             @Override
                             public void onConfirmation(boolean isConfirmed) {
                                 if (isConfirmed)
                                     presenter.onDeleteQueue(form.getEditedEntity());
                             }
                         });

            }
        });

        tableTools.addToolButtonRight(removeBtn);

        layout.add(tableTools.asWidget());

        queueTable = new DefaultCellTable<Queue>(8, new ProvidesKey<Queue>() {
            @Override
            public Object getKey(Queue item) {
                return item.getJndiName();
            }
        });
        queueProvider = new ListDataProvider<Queue>();
        queueProvider.addDataDisplay(queueTable);

        TextColumn<Queue> nameColumn = new TextColumn<Queue>() {
            @Override
            public String getValue(Queue record) {
                return record.getName();
            }
        };

        TextColumn<Queue> jndiNameColumn = new TextColumn<Queue>() {
            @Override
            public String getValue(Queue record) {
                return record.getJndiName();
            }
        };

        queueTable.addColumn(nameColumn, "Name");
        queueTable.addColumn(jndiNameColumn, "JNDI");

        layout.add(queueTable);
        queueTable.getElement().setAttribute("style", "margin-bottom:15px;");

        // ----

        TextItem name = new TextItem("name", "Name");
        TextItem jndi = new TextItem("jndiName", "JNDI");

        CheckBoxItem durable = new CheckBoxItem("durable", "Durable?");
        TextBoxItem selector = new TextBoxItem("selector", "Selector") {
            @Override
            public boolean isUndefined() {
                return getValue().equals("");
            }

            @Override
            public boolean isRequired() {
                return false;
            }
        };

        form.setFields(name, jndi, durable, selector);
        form.bind(queueTable);


        final FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        ModelNode address = Baseadress.get();
                        address.add("subsystem", "messaging");
                        address.add("hornetq-server", "*");
                        address.add("jms-queue", "*");
                        return address;
                    }
                }, form
        );


        Widget formToolsWidget = formTools.asWidget();
        formToolsWidget.getElement().setAttribute("style", "padding-top:15px;");

        layout.add(formToolsWidget);
        layout.add(helpPanel.asWidget());

        layout.add(form.asWidget());


        return layout;
    }

    void setQueues(List<Queue> queues)
    {
        queueProvider.setList(queues);

        queueTable.selectDefaultEntity();

        form.setEnabled(false);
    }

    public void setEnabled(boolean b) {
        form.setEnabled(b);
    }
}
