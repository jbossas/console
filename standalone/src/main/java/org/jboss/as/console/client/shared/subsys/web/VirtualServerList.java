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
import org.jboss.as.console.client.shared.subsys.web.model.VirtualServer;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.ListItem;
import org.jboss.as.console.client.widgets.forms.TextBoxItem;
import org.jboss.as.console.client.widgets.forms.TextItem;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;
import org.jboss.as.console.client.widgets.tools.ToolButton;
import org.jboss.as.console.client.widgets.tools.ToolStrip;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 5/11/11
 */
public class VirtualServerList {

    private DefaultCellTable<VirtualServer> table;
    private WebPresenter presenter;
    private ToolButton edit;
    private Form<VirtualServer> form;

    public VirtualServerList(WebPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {

        VerticalPanel layout = new VerticalPanel();


        ToolStrip toolStrip = new ToolStrip();
        toolStrip.getElement().setAttribute("style", "margin-bottom:10px;");

        edit = new ToolButton("Edit", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                /*if(edit.getText().equals("Edit"))
                   presenter.onEditConnector();
                else
                    presenter.onSaveConnector(form.getEditedEntity().getName(), form.getChangedValues());*/
            }
        });
        toolStrip.addToolButton(edit);

        toolStrip.addToolButton(new ToolButton("Delete", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
               // presenter.onDeleteConnector(form.getEditedEntity().getName());
            }
        }));


        toolStrip.addToolButtonRight(new ToolButton("Add", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        }));


        layout.add(toolStrip);

        // ----

        table = new DefaultCellTable<VirtualServer>(10);


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

        form = new Form<VirtualServer>(VirtualServer.class);
        form.setNumColumns(2);

        TextItem name = new TextItem("name", "Name");
        ListItem alias = new ListItem("alias", "Alias");
        TextBoxItem defaultModule = new TextBoxItem("defaultWebModule", "Default Module");

        form.setFields(name, alias, defaultModule);
        form.bind(table);

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
        table.setRowData(0, servers);

        if(!servers.isEmpty())
            table.getSelectionModel().setSelected(servers.get(0), true);

        form.setEnabled(false);

    }

    public void setEnabled(boolean b) {
        form.setEnabled(true);

        if(b)
            edit.setText("Save");
        else
            edit.setText("Edit");

    }
}
