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

package org.jboss.as.console.client.shared.sockets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.widgets.ComboBox;
import org.jboss.as.console.client.widgets.ContentGroupLabel;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.RHSContentPanel;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.NumberBoxItem;
import org.jboss.as.console.client.widgets.forms.TextItem;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;
import org.jboss.as.console.client.widgets.tools.ToolButton;
import org.jboss.as.console.client.widgets.tools.ToolStrip;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 4/6/11
 */
public class SocketBindingView extends DisposableViewImpl implements SocketBindingPresenter.MyView {

    private SocketBindingPresenter presenter;

    private DefaultCellTable<SocketBinding> socketTable;
    private ComboBox groupFilter;

    @Override
    public Widget createWidget() {
        LayoutPanel layout = new RHSContentPanel("Socket Binding Groups");
        layout.setStyleName("fill-layout");

        ToolStrip toolstrip = new ToolStrip();
        toolstrip.addToolButton(new ToolButton("Add", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {

            }
        }));

        //layout.add(toolstrip);


        // --

        // -----------

        ContentHeaderLabel nameLabel = new ContentHeaderLabel("Current Socket Bindings");

        HorizontalPanel horzPanel = new HorizontalPanel();
        horzPanel.getElement().setAttribute("style", "width:100%;");
        //Image image = new Image(Icons.INSTANCE.deployment());
        //horzPanel.add(image);
        //image.getElement().getParentElement().setAttribute("width", "25");

        horzPanel.add(nameLabel);

        layout.add(horzPanel);

        socketTable = new DefaultCellTable<SocketBinding>(20);

        TextColumn<SocketBinding> nameColumn = new TextColumn<SocketBinding>() {
            @Override
            public String getValue(SocketBinding record) {
                return record.getName();
            }
        };

        TextColumn<SocketBinding> portColumn = new TextColumn<SocketBinding>() {
            @Override
            public String getValue(SocketBinding record) {
                return String.valueOf(record.getPort());
            }
        };

        socketTable.addColumn(nameColumn, "Name");
        socketTable.addColumn(portColumn, "Port");

        HorizontalPanel tableOptions = new HorizontalPanel();
        tableOptions.getElement().setAttribute("cellpadding", "2px");

        groupFilter = new ComboBox();
        groupFilter.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                presenter.onFilterGroup(event.getValue());
            }
        });
        Widget groupFilterWidget = groupFilter.asWidget();
        groupFilterWidget.getElement().setAttribute("style", "width:200px;");


        tableOptions.add(new Label("Socket Binding Group:"));
        tableOptions.add(groupFilterWidget);


        tableOptions.getElement().setAttribute("style", "float:right;");
        layout.add(tableOptions);
        layout.add(socketTable);

        // -----------

        /*final ToolStrip toolStrip = new ToolStrip();
        final ToolButton edit = new ToolButton("Edit");
        edit.addClickHandler(new ClickHandler(){
            @Override
            public void onClick(ClickEvent clickEvent) {
                if(edit.getText().equals("Edit"))
                {

                }
                else
                {

                }
            }
        });

        toolStrip.addToolButton(edit);
        ToolButton delete = new ToolButton("Delete");
        delete.addClickHandler(new ClickHandler(){
            @Override
            public void onClick(ClickEvent clickEvent) {
                Feedback.confirm(
                        "Delete Deployment",
                        "Do you want to delete this deployment?",
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed) {
                                    SingleSelectionModel<DeploymentRecord> selectionModel = (SingleSelectionModel) deploymentTable.getSelectionModel();
                                    presenter.deleteDeployment(
                                            selectionModel.getSelectedObject()

                                    );
                                }
                            }
                        });
            }
        });
        toolStrip.addToolButton(delete);

        formPanel.add(toolStrip);
        formPanel.setWidgetTopHeight(toolStrip, 0, Style.Unit.PX, 30, Style.Unit.PX);*/

        Form<SocketBinding> form = new Form<SocketBinding>(SocketBinding.class);
        form.setNumColumns(2);

        TextItem nameItem = new TextItem("name", "Name");
        NumberBoxItem portItem = new NumberBoxItem("port", "Port");
        TextItem interfaceItem = new TextItem("interface", "Interface");
        TextItem multicastItem = new TextItem("multiCastAddress", "Multicast Address");
        NumberBoxItem multicastPortItem = new NumberBoxItem("multiCastPort", "Multicast Port");

        form.setFields(nameItem, portItem, interfaceItem, multicastItem, multicastPortItem);
        form.bind(socketTable);

        Widget formWidget = form.asWidget();

        layout.add(new ContentGroupLabel("Details"));
        layout.add(formWidget);

        // ------------------------------------------

        return layout;
    }

    @Override
    public void setPresenter(SocketBindingPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateGroups(List<String> groups) {
        groupFilter.setValues(groups);
        groupFilter.setItemSelected(0, true);
    }

    @Override
    public void setBindings(String groupName, List<SocketBinding> bindings) {
        socketTable.setRowData(0, bindings);
        socketTable.setRowCount(bindings.size(), true);
        socketTable.getSelectionModel().setSelected(bindings.get(0), true);
    }
}
