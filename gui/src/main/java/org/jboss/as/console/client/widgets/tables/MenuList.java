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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 4/27/11
 */
public class MenuList extends CellList implements PopupCell.PopupCellDelegate{

    private NamedCommand[] commands;
    private PopupPanel popup = null;

    private int selectedRow = -1;

    // cell list customization
    public interface Resources extends CellList.Resources { 		 		
        @Source("org/jboss/as/console/client/widgets/tables/CellList.css") 		
        CellList.Style cellListStyle(); 	
    }
 	
    
    MenuList(NamedCommand... commands)
    {
        this();
        setCommands(commands);
    }

    MenuList() {

        super(new MenuCell(), (Resources) GWT.create(Resources.class));

        final SingleSelectionModel<String> selectionModel = new SingleSelectionModel<String>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
               for(final NamedCommand cmd : commands){

                   String cmdName = selectionModel.getSelectedObject();
                   if(cmd.name.equals(cmdName))
                   {
                       if(popup!=null) popup.hide();

                       selectionModel.setSelected(cmdName, false);

                       Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand()
                       {
                           @Override
                           public void execute() {
                               cmd.execute(selectedRow);
                           }
                       });

                       break;
                   }
               }
            }
        });

        setSelectionModel(selectionModel);
    }

    @Override
    public void onRowSelection(int rownum) {
       selectedRow = rownum;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    public void setCommands(NamedCommand... commands) {

        this.commands = commands;
        List<String> cmdNames= new ArrayList<String>(commands.length);
        for(NamedCommand cmd : commands)
            cmdNames.add(cmd.name);

        setRowData(cmdNames);
    }

    public void setPopup(PopupPanel popup) {
        this.popup = popup;
    }
}
