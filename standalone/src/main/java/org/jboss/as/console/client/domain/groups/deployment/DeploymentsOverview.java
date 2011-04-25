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
package org.jboss.as.console.client.domain.groups.deployment;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.core.message.Message;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.Feedback;
import org.jboss.as.console.client.widgets.RHSHeader;
import org.jboss.as.console.client.widgets.icons.Icons;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;

import java.util.ArrayList;
import java.util.List;
import org.jboss.as.console.client.widgets.tables.HyperlinkCell;
import org.jboss.as.console.client.widgets.tools.ToolButton;
import org.jboss.as.console.client.widgets.tools.ToolStrip;

/**
 * @author Heiko Braun
 * @author Stan Silvert <ssilvert@redhat.com> (C) 2011 Red Hat Inc.
 * @date 3/1/11
 */
public class DeploymentsOverview extends SuspendableViewImpl implements DeploymentsPresenter.MyView {

  private DeploymentsPresenter presenter;
  private ListDataProvider<DeploymentRecord> domainDeploymentProvider = new ListDataProvider<DeploymentRecord>();
  
  private TabLayoutPanel tabLayoutpanel = new TabLayoutPanel(25, Style.Unit.PX);
  private List<String> serverGroupNames;
  private Map<String, Widget> serverGroupTabsAdded = new HashMap<String, Widget>();
  private Map<String, ListDataProvider<DeploymentRecord>> serverGroupDeploymentProviders = new HashMap<String, ListDataProvider<DeploymentRecord>>();

  @Override
  public void setPresenter(DeploymentsPresenter presenter) {
    this.presenter = presenter;
  }
  
  @Override
  public String getSelectedServerGroup() {
    int selected = this.tabLayoutpanel.getSelectedIndex();
    Label label = (Label)this.tabLayoutpanel.getTabWidget(selected);
    return label.getText();
  }

  @Override
  public Widget createWidget() {
    LayoutPanel layout = new LayoutPanel();

    RHSHeader title = new RHSHeader("Manage Deployments");
    layout.add(title);
    layout.setWidgetTopHeight(title, 0, Style.Unit.PX, 28, Style.Unit.PX);

    // --

    final ToolStrip topLevelTools = new ToolStrip();
    final ToolButton newButton = new ToolButton("New Deployment");
    newButton.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent clickEvent) {
        presenter.launchNewDeploymentDialoge();
      }
    });

    topLevelTools.addToolButtonRight(newButton);
    layout.add(topLevelTools);
    layout.setWidgetTopHeight(topLevelTools, 28, Style.Unit.PX, 30, Style.Unit.PX);
    
    SplitLayoutPanel deploymentsPanel = new SplitLayoutPanel(100);
    
    String[] columnHeaders = new String[] {"Name", "Runtime Name", "Action", "Action"};
    List<Column> columns = makeNameAndRuntimeColumns();
    columns.addAll(makeActionColumns(domainDeploymentProvider, DeploymentCommand.ADD_TO_GROUP, DeploymentCommand.REMOVE_CONTENT));
    
    deploymentsPanel.addNorth(makeDeploymentTable("Content Repository", domainDeploymentProvider, columns, columnHeaders), 250);
    
    tabLayoutpanel.addStyleName("default-tabpanel");
    deploymentsPanel.addStyleName("fill-layout-width");
    deploymentsPanel.add(tabLayoutpanel);
    
    layout.add(deploymentsPanel);
    layout.setWidgetTopHeight(deploymentsPanel, 55, Style.Unit.PX, 700, Style.Unit.PX);
    
    return layout;
  }
  
  @Override
  public void updateDeploymentInfo(DomainDeploymentInfo domainDeploymentInfo) {
    serverGroupNames = domainDeploymentInfo.getServerGroupNames();
    
    createAndRemoveTabs();
    
    // Set the backing data for domain tables
    domainDeploymentProvider.setList(domainDeploymentInfo.getDomainDeployments());
    
    // Set the backing data for server group tables
    for(Entry<String, List<DeploymentRecord>> entry : domainDeploymentInfo.getServerGroupDeployments().entrySet()) {
      this.serverGroupDeploymentProviders.get(entry.getKey()).setList(entry.getValue());
    }
    
    //  if (!deploymentRecords.isEmpty()) {
  //    deploymentTable.getSelectionModel().setSelected(deploymentRecords.get(0), true);
  //  }
  }

  private Widget makeDeploymentTable(String headerLabel, 
                                     ListDataProvider<DeploymentRecord> dataProvider,
                                     List<Column> columns,
                                     String[] columnHeaders) {
    VerticalPanel vpanel = new VerticalPanel();
    vpanel.setStyleName("fill-layout-width");
    vpanel.getElement().setAttribute("style", "padding:25px;");

    // -----------

    ContentHeaderLabel nameLabel = new ContentHeaderLabel(headerLabel);

    HorizontalPanel horzPanel = new HorizontalPanel();
    horzPanel.getElement().setAttribute("style", "width:100%;");
    Image image = new Image(Icons.INSTANCE.deployment());
    horzPanel.add(image);
    image.getElement().getParentElement().setAttribute("width", "25");

    horzPanel.add(nameLabel);

    vpanel.add(horzPanel);
    
    DefaultCellTable<DeploymentRecord> deploymentTable = new DefaultCellTable<DeploymentRecord>(20);
    dataProvider.addDataDisplay(deploymentTable);
    
    for (int i = 0; i < columnHeaders.length; i++) {
      deploymentTable.addColumn(columns.get(i), columnHeaders[i]);
    }
    
    ScrollPanel scroller = new ScrollPanel(deploymentTable);
    scroller.setAlwaysShowScrollBars(true);
    vpanel.add(scroller);
    
    return vpanel;
  }
  
  private List<Column> makeNameAndRuntimeColumns() {
    List<Column> columns = new ArrayList<Column>(2);
    
    columns.add(new TextColumn<DeploymentRecord>() {
                  @Override
                  public String getValue(DeploymentRecord record) {
                    return record.getName();
                  } }
    );

    columns.add(new TextColumn<DeploymentRecord>() {
                  @Override
                  public String getValue(DeploymentRecord record) {
                    return record.getRuntimeName();
                  } }
    );
    
    return columns;
  }
  
  private Column makeEnabledColumn() {
    return new TextColumn<DeploymentRecord>() {
             @Override
             public String getValue(DeploymentRecord record) {
               return Boolean.toString(record.isEnabled());
             }
    };
  }
  
  private List<Column> makeActionColumns(ListDataProvider<DeploymentRecord> dataProvider, DeploymentCommand... commands) {
    List<Column> columns = new ArrayList<Column>(commands.length);
    
    for (int i = 0; i < commands.length; i++) {
      HyperlinkCell hyperlinkCell = new HyperlinkCell(commands[i].getLabel(), 
              new DeploymentCommandActionCellDelegate(commands[i], dataProvider));

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
  
  /**
   * Enumeration of commands available from deployment tables.
   */
  enum DeploymentCommand {
    ENABLE_DISABLE("enable/disable", "Enable or Disable", "for"),
    REMOVE_FROM_GROUP("remove from group", "Remove", "from"),
    ADD_TO_GROUP("add to selected server group", "Add", "to"),
    REMOVE_CONTENT("remove content", "Remove", "from");
    
    private String label;
    private String verb;
    private String preposition;
    
    private DeploymentCommand(String label, String verb, String preposition) {
      this.label = label;
      this.verb = verb;
      this.preposition = preposition;
    }
    
    public void execute(DeploymentsPresenter presenter, DeploymentRecord record) {
      String target = record.getServerGroup();
      if (this == ADD_TO_GROUP) target = presenter.getView().getSelectedServerGroup();
      confirm(presenter, record, target);
    }
    
    private void confirm(final DeploymentsPresenter presenter, final DeploymentRecord record, String target) {
      Feedback.confirm("Are you sure?", confirmMessage(record, target), new Feedback.ConfirmationHandler() {
        @Override
        public void onConfirmation(boolean isConfirmed) {
          if (isConfirmed) doCommand(presenter, record);
        }
      });
    }
    
    private String confirmMessage(DeploymentRecord record, String target) {
      String action = verb;
      if (this == ENABLE_DISABLE && record.isEnabled()) action = "Disable";
      if (this == ENABLE_DISABLE && !record.isEnabled()) action = "Enable";
      return action + " " + record.getName() + " " + preposition + " " + target + ".";
    }
    
    private void doCommand(DeploymentsPresenter presenter, DeploymentRecord record) {
      String selectedGroup = presenter.getView().getSelectedServerGroup();
      switch (this) {
        case ENABLE_DISABLE: presenter.enableDisableDeployment(record);       break;
        case REMOVE_FROM_GROUP: presenter.removeDeploymentFromGroup(record);  break;
        case ADD_TO_GROUP: presenter.addToServerGroup(selectedGroup, record); break;
        case REMOVE_CONTENT: presenter.removeContent(record);                 break;
      }
    }
    
    public void displaySuccessMessage(DeploymentRecord record, String target) {
      Console.MODULES.getMessageCenter().notify(
                new Message("Success: " + confirmMessage(record, target), Message.Severity.Info)
        );
    }
    
    public void displayFailureMessage(DeploymentRecord record, String target, Throwable t) {
      Console.MODULES.getMessageCenter().notify(
                new Message("Failure: " + confirmMessage(record, target) + "; " + t.getMessage(), Message.Severity.Error)
        );
    }
    
    public String getLabel() {
      return this.label;
    }
  }
  
  private class DeploymentCommandActionCellDelegate<String> implements ActionCell.Delegate<String> {
    private DeploymentCommand command;
    private ListDataProvider<DeploymentRecord> deploymentRecords;
    
    DeploymentCommandActionCellDelegate(DeploymentCommand command, ListDataProvider<DeploymentRecord> deploymentRecords) {
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
      command.execute(presenter, deploymentRecords.getList().get(row));
    }
  }

  private void createAndRemoveTabs() {
    // add new server groups
    for (String serverGroupName : serverGroupNames) {
      if (this.serverGroupTabsAdded.containsKey(serverGroupName)) {
        continue;
      }
      
      VerticalPanel vPanel = new VerticalPanel();
      this.tabLayoutpanel.add(vPanel, serverGroupName);
      this.serverGroupTabsAdded.put(serverGroupName, vPanel);
      ListDataProvider<DeploymentRecord> serverGroupProvider = new ListDataProvider<DeploymentRecord>();
      this.serverGroupDeploymentProviders.put(serverGroupName, serverGroupProvider);
      
      String[] columnHeaders = new String[] {"Name", "Runtime Name", "Enabled?", "Action", "Action"};
      List<Column> columns = makeNameAndRuntimeColumns();
      columns.add(makeEnabledColumn());
      columns.addAll(makeActionColumns(this.serverGroupDeploymentProviders.get(serverGroupName), DeploymentCommand.ENABLE_DISABLE, DeploymentCommand.REMOVE_FROM_GROUP));
      vPanel.add(makeDeploymentTable(serverGroupName + " Deployments", serverGroupProvider, columns, columnHeaders));
    }

    // find server groups to remove
    List<String> removals = new ArrayList<String>(); // avoid ConcurrentModificationException
    for (Map.Entry<String, Widget> entry : this.serverGroupTabsAdded.entrySet()) {
      String serverGroupName = entry.getKey();
      if (!serverGroupNames.contains(serverGroupName)) {
        removals.add(serverGroupName);
      }
    }

    // remove deleted server groups
    for (String serverGroupName : removals) {
      Widget widget = this.serverGroupTabsAdded.remove(serverGroupName);
      this.tabLayoutpanel.remove(widget);
      this.serverGroupDeploymentProviders.remove(serverGroupName);
    }
  }
  
}