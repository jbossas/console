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
import org.jboss.as.console.client.shared.subsys.messaging.model.AddressingPattern;
import org.jboss.as.console.client.shared.subsys.messaging.model.MessagingProvider;
import org.jboss.as.console.client.shared.subsys.messaging.model.SecurityPattern;
import org.jboss.as.console.client.widgets.forms.CheckBoxItem;
import org.jboss.as.console.client.widgets.forms.DisclosureGroupRenderer;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.NumberBoxItem;
import org.jboss.as.console.client.widgets.forms.TextBoxItem;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;
import org.jboss.as.console.client.widgets.tools.ToolButton;
import org.jboss.as.console.client.widgets.tools.ToolStrip;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 5/10/11
 */
public class AddressingDetails {

    private MessagingPresenter presenter;
    private Form<AddressingPattern> form;
    private MessagingProvider providerEntity;
    private DefaultCellTable<SecurityPattern> addrTable;

    public AddressingDetails(MessagingPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {

        VerticalPanel layout = new VerticalPanel();

        ToolStrip toolStrip = new ToolStrip();
        toolStrip.getElement().setAttribute("style", "margin-bottom:10px;");

        toolStrip.addToolButton(new ToolButton("Edit", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        }));

        toolStrip.addToolButton(new ToolButton("Delete", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        }));

         toolStrip.addToolButtonRight(new ToolButton("Add", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        }));

        layout.add(toolStrip);

        // ----

        addrTable = new DefaultCellTable<SecurityPattern>(10);

        Column<AddressingPattern, String> patternColumn = new Column<AddressingPattern, String>(new TextCell()) {
            @Override
            public String getValue(AddressingPattern object) {
                return object.getPattern();
            }
        };

        addrTable.addColumn(patternColumn, "Pattern");

        layout.add(addrTable);


        // ---

        form = new Form<AddressingPattern>(AddressingPattern.class);
        form.setNumColumns(2);
        form.bind(addrTable);

        TextBoxItem dlQ = new TextBoxItem("deadLetterQueue", "Dead Letter Queue");
        TextBoxItem expQ= new TextBoxItem("expiryQueue", "Expiry Queue");
        NumberBoxItem redelivery = new NumberBoxItem("redeliveryDelay", "Redelivery Delay");

        form.setFields(dlQ, expQ, redelivery);

        layout.add(form.asWidget());
        return layout;
    }

    void setProvider(MessagingProvider provider)
    {
        this.providerEntity = provider;

        List<AddressingPattern> addrPatterns = provider.getAddressPatterns();
        addrTable.setRowData(0, addrPatterns);
        if(!addrPatterns.isEmpty())
            addrTable.getSelectionModel().setSelected(addrPatterns.get(0), true);

        form.setEnabled(false);
    }
}
