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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.subsys.messaging.model.MessagingProvider;
import org.jboss.as.console.client.shared.subsys.messaging.model.SecurityPattern;
import org.jboss.as.console.client.widgets.forms.CheckBoxItem;
import org.jboss.as.console.client.widgets.forms.DisclosureGroupRenderer;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;
import org.jboss.as.console.client.widgets.tools.ToolButton;
import org.jboss.as.console.client.widgets.tools.ToolStrip;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 5/10/11
 */
public class SecurityDetails {

    private MessagingPresenter presenter;
    private Form<SecurityPattern> form;
    private MessagingProvider providerEntity;
    private DefaultCellTable<SecurityPattern> secTable;
    private ToolButton edit ;

    public SecurityDetails(MessagingPresenter presenter) {
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
                    presenter.onEditSecDetails();
                else
                    presenter.onSaveSecDetails(form.getChangedValues());
            }
        });
        toolStrip.addToolButton(edit);

        toolStrip.addToolButton(new ToolButton("Delete", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.onDeleteSecDetails(form.getEditedEntity());
            }
        }));


        toolStrip.addToolButtonRight(new ToolButton("Add", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewSecDialogue();
            }
        }));

        // TODO: https://issues.jboss.org/browse/AS7-759
        //layout.add(toolStrip);

        // ----

        secTable = new DefaultCellTable<SecurityPattern>(10);
        secTable.getElement().setAttribute("style", "margin-top:10px");

        Column<SecurityPattern, String> principalColumn = new Column<SecurityPattern, String>(new TextCell()) {
            @Override
            public String getValue(SecurityPattern object) {
                return object.getPrincipal();
            }
        };


        Column<SecurityPattern, String> patternColumn = new Column<SecurityPattern, String>(new TextCell()) {
            @Override
            public String getValue(SecurityPattern object) {
                return object.getPattern();
            }
        };

        secTable.addColumn(principalColumn, "Principal");
        secTable.addColumn(patternColumn, "Pattern");

        layout.add(secTable);


        // ---

        form = new Form<SecurityPattern>(SecurityPattern.class);
        form.setNumColumns(2);
        form.bind(secTable);

        CheckBoxItem send = new CheckBoxItem("send", "Send?");
        CheckBoxItem consume = new CheckBoxItem("consume", "Consume?");
        CheckBoxItem manage= new CheckBoxItem("manage", "Manage?");

        CheckBoxItem createDQ = new CheckBoxItem("createDurableQueue", "CreateDurable?");
        CheckBoxItem deleteDQ = new CheckBoxItem("deleteDurableQueue", "DeleteDurable?");

        CheckBoxItem createNDQ = new CheckBoxItem("createNonDurableQueue", "CreateNonDurable?");
        CheckBoxItem deleteNDQ = new CheckBoxItem("deleteNonDurableQueue", "DeleteNonDurable?");


        form.setFields(send, consume, manage);
        form.setFieldsInGroup("Advanced", new DisclosureGroupRenderer(), createDQ, deleteDQ, createNDQ, deleteNDQ);

        layout.add(form.asWidget());
        return layout;
    }

    void setProvider(MessagingProvider provider)
    {
        this.providerEntity = provider;

        List<SecurityPattern> secPatterns = provider.getSecurityPatterns();
        secTable.setRowCount(secPatterns.size(), true);
        secTable.setRowData(0, secPatterns);
        if(!secPatterns.isEmpty())
            secTable.getSelectionModel().setSelected(secPatterns.get(0), true);

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
