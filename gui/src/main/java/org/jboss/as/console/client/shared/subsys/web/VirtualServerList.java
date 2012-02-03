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

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.web.model.VirtualServer;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.ListItem;
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
 * @date 5/11/11
 */
public class VirtualServerList {

    private DefaultCellTable<VirtualServer> table;
    private ListDataProvider<VirtualServer> dataProvider;

    private WebPresenter presenter;
    private Form<VirtualServer> form;

    public VirtualServerList(WebPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout");

        form = new Form<VirtualServer>(VirtualServer.class);
        form.setNumColumns(2);

        FormToolStrip<VirtualServer> formTools = new FormToolStrip<VirtualServer>(
                form,
                new FormToolStrip.FormCallback<VirtualServer>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                        presenter.onSaveVirtualServer(form.getEditedEntity().getName(), form.getChangedValues());
                    }

                    @Override
                    public void onDelete(final VirtualServer entity) {
                        presenter.onDeleteVirtualServer(entity.getName());
                    }
                }
        );

        ToolStrip tableTools = new ToolStrip();
        ToolButton addBtn = new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.launchVirtualServerDialogue();
            }
        });
        addBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_add_virtualServelList());
        tableTools.addToolButtonRight(addBtn);


        ToolButton removeBtn = new ToolButton(Console.CONSTANTS.common_label_delete(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                final VirtualServer selectedObject = ((SingleSelectionModel<VirtualServer>) table.getSelectionModel()).getSelectedObject();
                if(selectedObject!=null)
                {
                    Feedback.confirm(
                            Console.MESSAGES.deleteTitle("Virtual Server"),
                            Console.MESSAGES.deleteConfirm("Virtual Server"), new Feedback.ConfirmationHandler() {
                        @Override
                        public void onConfirmation(boolean isConfirmed) {
                            if (isConfirmed)
                                presenter.onDeleteVirtualServer(selectedObject.getName());
                        }
                    });
                }
            }
        });

        tableTools.addToolButtonRight(addBtn);
        tableTools.addToolButtonRight(removeBtn);

        layout.add(tableTools.asWidget());

        // ----

        table = new DefaultCellTable<VirtualServer>(8, new ProvidesKey<VirtualServer>() {
            @Override
            public Object getKey(VirtualServer item) {
                return item.getName();
            }
        });

        dataProvider = new ListDataProvider<VirtualServer>();
        dataProvider.addDataDisplay(table);

        Column<VirtualServer, String> nameColumn = new Column<VirtualServer, String>(new TextCell()) {
            @Override
            public String getValue(VirtualServer object) {
                return object.getName();
            }
        };


        Column<VirtualServer, String> aliasColumn = new Column<VirtualServer, String>(new TextCell()) {
            @Override
            public String getValue(VirtualServer object) {

                return aliasToString(object);
            }
        };


        table.addColumn(nameColumn, "Name");
        table.addColumn(aliasColumn, "Alias");

        layout.add(table);


        // ---

        TextItem name = new TextItem("name", "Name");
        ListItem alias = new ListItem("alias", "Alias");
        TextBoxItem defaultModule = new TextBoxItem("defaultWebModule", "Default Module");

        form.setFields(name, alias, defaultModule);
        form.bind(table);

        final FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        ModelNode address = Baseadress.get();
                        address.add("subsystem", "web");
                        address.add("virtual-server", "*");
                        return address;
                    }
                }, form
        );


        layout.add(formTools.asWidget());
        layout.add(helpPanel.asWidget());

        layout.add(form.asWidget());

        return layout;
    }

    private String aliasToString(VirtualServer object) {
        StringBuffer sb = new StringBuffer();
        for(String s : object.getAlias())
            sb.append(s).append(" ");

        return sb.toString();
    }

    public void setVirtualServers(List<VirtualServer> servers) {

        dataProvider.setList(servers);

        table.selectDefaultEntity();

        form.setEnabled(false);

    }

    public void setEnabled(boolean b) {
        form.setEnabled(b);
    }
}
