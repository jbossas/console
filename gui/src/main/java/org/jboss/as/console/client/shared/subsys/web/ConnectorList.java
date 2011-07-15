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

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.web.model.HttpConnector;
import org.jboss.as.console.client.widgets.window.Feedback;
import org.jboss.as.console.client.widgets.forms.CheckBoxItem;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.TextBoxItem;
import org.jboss.as.console.client.widgets.forms.TextItem;
import org.jboss.as.console.client.widgets.icons.Icons;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;
import org.jboss.as.console.client.widgets.tools.ToolButton;
import org.jboss.as.console.client.widgets.tools.ToolStrip;
import org.jboss.dmr.client.ModelNode;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 5/11/11
 */
public class ConnectorList {

    private DefaultCellTable<HttpConnector> connectorTable;
    private WebPresenter presenter;
    private ToolButton edit;
    private Form<HttpConnector> form;

    public ConnectorList(WebPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {

        VerticalPanel layout = new VerticalPanel();


        ToolStrip toolStrip = new ToolStrip();
        toolStrip.getElement().setAttribute("style", "margin-bottom:10px;");

        edit = new ToolButton("Edit", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                if(edit.getText().equals("Edit"))
                    presenter.onEditConnector();
                else
                    presenter.onSaveConnector(form.getEditedEntity().getName(), form.getChangedValues());
            }
        });
        toolStrip.addToolButton(edit);

        toolStrip.addToolButton(new ToolButton("Delete", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                final HttpConnector connector = form.getEditedEntity();

                Feedback.confirm("Remove Connector", "Really remove connector '" + connector.getName() + "'?",
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed) {

                                    presenter.onDeleteConnector(connector.getName());
                                }
                            }
                        });
            }
        }));


        toolStrip.addToolButtonRight(new ToolButton("Add", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.launchConnectorDialogue();
            }
        }));


        layout.add(toolStrip);

        // ----

        connectorTable = new DefaultCellTable<HttpConnector>(10);


        Column<HttpConnector, String> nameColumn = new Column<HttpConnector, String>(new TextCell()) {
            @Override
            public String getValue(HttpConnector object) {
                return object.getName();
            }
        };


        Column<HttpConnector, String> protocolColumn = new Column<HttpConnector, String>(new TextCell()) {
            @Override
            public String getValue(HttpConnector object) {
                return object.getProtocol();
            }
        };


        Column<HttpConnector, ImageResource> statusColumn =
                new Column<HttpConnector, ImageResource>(new ImageResourceCell()) {
                    @Override
                    public ImageResource getValue(HttpConnector connector) {

                        ImageResource res = null;

                        if(connector.isEnabled())
                            res = Icons.INSTANCE.statusGreen_small();
                        else
                            res = Icons.INSTANCE.statusRed_small();

                        return res;
                    }
                };

        connectorTable.addColumn(nameColumn, "Name");
        connectorTable.addColumn(protocolColumn, "Protocol");
        connectorTable.addColumn(statusColumn, "Enabled?");

        layout.add(connectorTable);


        // ---

        form = new Form<HttpConnector>(HttpConnector.class);
        form.setNumColumns(2);

        TextItem name = new TextItem("name", "Name");
        TextItem protocol = new TextItem("protocol", "Protocol");

        TextItem scheme = new TextItem("scheme", "Scheme");
        TextBoxItem socketBinding = new TextBoxItem("socketBinding", "Socket Binding");

        CheckBoxItem state = new CheckBoxItem("enabled", "Enabled?");

        form.setFields(name, protocol, scheme, socketBinding, state);
        form.bind(connectorTable);

         final FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        ModelNode address = Baseadress.get();
                        address.add("subsystem", "web");
                        address.add("connector", "*");
                        return address;
                    }
                }, form
        );
        layout.add(helpPanel.asWidget());

        layout.add(form.asWidget());

        return layout;
    }

    public void setConnectors(List<HttpConnector> connectors) {

        connectorTable.setRowCount(connectors.size(), true);
        connectorTable.setRowData(0, connectors);

        if(!connectors.isEmpty())
            connectorTable.getSelectionModel().setSelected(connectors.get(0), true);

        form.setEnabled(false);
    }

    public void setEnabled(boolean b) {
        form.setEnabled(b);

        if(b)
            edit.setText("Save");
        else
            edit.setText("Edit");

    }
}
