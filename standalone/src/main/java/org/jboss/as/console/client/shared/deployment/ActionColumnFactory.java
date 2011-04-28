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

package org.jboss.as.console.client.shared.deployment;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.view.client.ListDataProvider;
import java.util.ArrayList;
import java.util.List;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.as.console.client.widgets.tables.HyperlinkCell;

/**
 * This static factory creates columns that can trigger a deployment action
 * against the selected row.
 *
 * @author Stan Silvert <ssilvert@redhat.com> (C) 2011 Red Hat Inc.
 *
 */
public class ActionColumnFactory {
  
  // Static factory.  Don't allow instances.
  private ActionColumnFactory() {}

  public static List<Column> makeActionColumns(DeployCommandDelegate delegate,
                                               ListDataProvider<DeploymentRecord> dataProvider, 
                                               DeploymentCommand... commands) {
    List<Column> columns = new ArrayList<Column>(commands.length);
    
    for (int i = 0; i < commands.length; i++) {
      HyperlinkCell hyperlinkCell = new HyperlinkCell(commands[i].getLabel(), 
              new DeploymentCommandActionCellDelegate(delegate, commands[i], dataProvider));

        Column<DeploymentRecord, String> hyperlinkColumn = new Column<DeploymentRecord, String>(hyperlinkCell) {
            @Override
            public String getValue(DeploymentRecord object) {
                return "";
            }

        };
        
        columns.add(hyperlinkColumn);
    }
    
    return columns;
  }
  
  private static class DeploymentCommandActionCellDelegate<String> implements ActionCell.Delegate<String> {
    private DeployCommandDelegate delegate;
    private DeploymentCommand command;
    private ListDataProvider<DeploymentRecord> deploymentRecords;
    
    DeploymentCommandActionCellDelegate(DeployCommandDelegate delegate, 
                                        DeploymentCommand command, 
                                        ListDataProvider<DeploymentRecord> deploymentRecords) {
      this.delegate = delegate;
      this.command = command;
      this.deploymentRecords = deploymentRecords;
    }
    
    @Override
    public void execute(String rowNum) {
      int row = -1;
      try {
        row = Integer.parseInt(rowNum.toString());
      } catch (NumberFormatException e) {
        Log.error("Returned invalid row=" + rowNum, e);
      }
      command.execute(delegate, deploymentRecords.getList().get(row));
    }
  }
}
