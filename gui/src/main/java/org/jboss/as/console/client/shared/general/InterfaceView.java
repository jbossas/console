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

package org.jboss.as.console.client.shared.general;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.ballroom.client.layout.RHSContentPanel;
import org.jboss.as.console.client.shared.general.model.Interface;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 5/17/11
 */
public class InterfaceView extends DisposableViewImpl implements InterfacePresenter.MyView{

    private InterfacePresenter presenter;
    private CellTable<Interface> table;

    @Override
    public Widget createWidget() {
        LayoutPanel layout = new RHSContentPanel("Interfaces");

        layout.add(new ContentHeaderLabel("Interface Declarations"));

        //HTML description = new HTML("A named network interface, but without any criteria for determining the IP address to associate with that interface. Acts as a placeholder in the model (e.g. at the domain level) until a fully specified interface definition is applied at a lower level (e.g. at the server level, where available addresses are known.)");
        //description.getElement().setAttribute("style", "margin-bottom:15px;");

        //layout.add(description);

        table = new DefaultCellTable<Interface>(10);

        TextColumn<Interface> nameColumn = new TextColumn<Interface>() {
            @Override
            public String getValue(Interface record) {
                return record.getName();
            }
        };

         TextColumn<Interface> criteriaColumn = new TextColumn<Interface>() {
            @Override
            public String getValue(Interface record) {
                return record.getCriteria();
            }
        };

        table.addColumn(nameColumn, "Name");
        table.addColumn(criteriaColumn, "Criteria");

        layout.add(table);

        return layout;
    }

    @Override
    public void setPresenter(InterfacePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setInterfaces(List<Interface> interfaces) {
        table.setRowCount(interfaces.size(), true);
        table.setRowData(interfaces);
    }
}
