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

package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.widgets.Feedback;
import org.jboss.as.console.client.widgets.forms.CheckBoxItem;
import org.jboss.as.console.client.widgets.forms.DefaultGroupRenderer;
import org.jboss.as.console.client.widgets.forms.DisclosureGroupRenderer;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.PasswordBoxItem;
import org.jboss.as.console.client.widgets.forms.TextBoxItem;
import org.jboss.as.console.client.widgets.forms.TextItem;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;
import org.jboss.as.console.client.widgets.tools.ToolButton;
import org.jboss.as.console.client.widgets.tools.ToolStrip;

/**
 * @author Heiko Braun
 * @date 5/4/11
 */
public class DataSourceDetails {

    private Form<DataSource> form;
    private ToolButton editBtn;
    private DataSourcePresenter presenter;

    public DataSourceDetails(DataSourcePresenter presenter) {
        this.presenter = presenter;
        form = new Form(DataSource.class);
        form.setNumColumns(2);
    }

    public Widget asWidget() {
        VerticalPanel detailPanel = new VerticalPanel();
        detailPanel.setStyleName("fill-layout-width");


        ToolStrip detailToolStrip = new ToolStrip();
        editBtn = new ToolButton(Console.CONSTANTS.common_label_edit());
        ClickHandler editHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if(editBtn.getText().equals(Console.CONSTANTS.common_label_edit()))
                    presenter.onEditDS(form.getEditedEntity());
                else
                    presenter.onSaveDSDetails(form.getEditedEntity().getName(), form.getChangedValues());
            }
        };
        editBtn.addClickHandler(editHandler);
        detailToolStrip.addToolButton(editBtn);


        ClickHandler clickHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                DataSource currentSelection = form.getEditedEntity();

                Feedback.confirm(
                        "Delete DataSource",
                        "Really delete this DataSource '" + currentSelection.getName() + "' ?",
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed) {
                                    presenter.onDelete(form.getEditedEntity());
                                }
                            }
                        });
            }
        };
        ToolButton deleteBtn = new ToolButton(Console.CONSTANTS.common_label_delete());
        deleteBtn.addClickHandler(clickHandler);
        detailToolStrip.addToolButton(deleteBtn);


        ClickHandler disableHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                String state = form.getEditedEntity().isEnabled() ? Console.CONSTANTS.common_label_disable() : Console.CONSTANTS.common_label_enable();
                final boolean nextState = !form.getEditedEntity().isEnabled();
                Feedback.confirm(state + " datasource", "Do you want to " + state + " this DataSource?",
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed) {
                                    presenter.onDisable(form.getEditedEntity(), nextState);
                                }
                            }
                        });
            }
        };

        ToolButton enableBtn = new ToolButton(Console.CONSTANTS.common_label_enOrDisable());
        enableBtn.addClickHandler(disableHandler);
        detailToolStrip.addToolButtonRight(enableBtn);

        detailPanel.add(detailToolStrip);

        TextItem nameItem = new TextItem("name", "Name");
        TextBoxItem jndiItem = new TextBoxItem("jndiName", "JNDI");
        CheckBoxItem enabledFlagItem = new CheckBoxItem("enabled", "Is enabled?");
        TextBoxItem driverItem = new TextBoxItem("driverName", "Driver");
        TextBoxItem driverVersion = new TextBoxItem("driverVersion", "Version");
        TextBoxItem driverClassItem = new TextBoxItem("driverClass", "Driver Class");

        TextBoxItem urlItem = new TextBoxItem("connectionUrl", "Connection URL");

        TextBoxItem userItem = new TextBoxItem("username", "Username");
        PasswordBoxItem passwordItem = new PasswordBoxItem("password", "Password");

        form.setFields(nameItem, jndiItem, enabledFlagItem);
        form.setFieldsInGroup("Connection", new DefaultGroupRenderer(), userItem, passwordItem, urlItem);
        form.setFieldsInGroup("Driver", new DisclosureGroupRenderer(), driverItem, driverVersion, driverClassItem);

        form.setEnabled(false); // currently not editable

        Widget formWidget = form.asWidget();

        detailPanel.add(formWidget);

        ScrollPanel scroll = new ScrollPanel(detailPanel);
        return scroll;
    }

    public void bind(CellTable<DataSource> dataSourceTable) {
        form.bind(dataSourceTable);
    }

    public void setEnabled(boolean b) {
        form.setEnabled(b);


        if(b)
            editBtn.setText("Save");
        else
            editBtn.setText("Edit");
    }
}
