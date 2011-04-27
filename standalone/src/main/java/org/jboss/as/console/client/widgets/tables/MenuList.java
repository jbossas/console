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

package org.jboss.as.console.client.widgets.tables;

import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 4/27/11
 */
public class MenuList extends CellList<String> {

    private NamedCommand[] commands;
    private PopupPanel popup = null;

    MenuList(NamedCommand... commands)
    {
        this();
        setCommands(commands);
    }

    MenuList() {
        super(new MenuCell());

        final SingleSelectionModel<String> selectionModel = new SingleSelectionModel<String>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
               for(NamedCommand cmd : commands){

                   String cmdName = selectionModel.getSelectedObject();
                   if(cmd.name.equals(cmdName))
                   {
                       if(popup!=null) popup.hide();

                       selectionModel.setSelected(cmdName, false);
                       cmd.execute();
                       break;
                   }
               }
            }
        });

        setSelectionModel(selectionModel);
    }

    public void setCommands(NamedCommand... commands) {

        this.commands = commands;
        List<String> list = new ArrayList<String>(commands.length);
        for(NamedCommand cmd : commands)
            list.add(cmd.name);

        setRowData(list);
    }

    public void setPopup(PopupPanel popup) {
        this.popup = popup;
    }
}
