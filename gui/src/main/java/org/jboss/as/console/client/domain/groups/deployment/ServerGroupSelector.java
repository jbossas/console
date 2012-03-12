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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * In its constructor, this class displays a dialog of server groups that the user can choose
 * to assign a deployment to.  When the user submits the request it calls back into the
 * Deployments Presenter.
 * 
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class ServerGroupSelector {
    
    private List<ServerGroupSelection> selections;
    private DeploymentsPresenter presenter;
    private DeploymentRecord deployment;
    
    /**
     * Create a new dialog and let the user choose server groups for a deployment.
     * 
     * @param presenter The presenter that will get the request for server group assignment.
     * @param deployment The deployment to be assigned to one or more server groups.
     */
    public ServerGroupSelector(DeploymentsPresenter presenter, DeploymentRecord deployment) {
        this.presenter = presenter;
        this.deployment = deployment;
        
        List<ServerGroupRecord> serverGroups = presenter.getPossibleGroupAssignments(deployment);
        if (serverGroups.isEmpty()) {
            Feedback.alert(Console.MESSAGES.selectServerGroups(), 
                           Console.MESSAGES.alreadyAssignedToAllGroups(deployment.getName()));
            return;
        }
        
        selections = new ArrayList<ServerGroupSelection>(serverGroups.size());
        for (ServerGroupRecord group : serverGroups) {
            selections.add(new ServerGroupSelection(group));
        }
        
        ListDataProvider<ServerGroupSelection> dataProvider = new ListDataProvider<ServerGroupSelection>();
        dataProvider.setList(selections);
        
        DefaultWindow window = makeWindow(deployment, dataProvider);
        window.center();
    }
    
    private DefaultWindow makeWindow(DeploymentRecord deployment, ListDataProvider<ServerGroupSelection> dataProvider) {
        DefaultWindow window = new DefaultWindow(Console.MESSAGES.selectServerGroups());
        window.setWidth(520);
        window.setHeight(380);
        window.setGlassEnabled(true);
        
        window.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
            }
        });
        
        VerticalPanel layout = new VerticalPanel();
        layout.getElement().setAttribute("style", "width:95%, margin:15px;");

        layout.add(new HTML("<h3>" + Console.MESSAGES.selectServerGroupsFor(deployment.getName()) + "</h3>"));
        
        Widget table = makeSelectionTable(dataProvider);
        table.setWidth("470px");
        
        layout.add(table);
        
        CheckBox enableBox = new CheckBox(Console.CONSTANTS.common_label_enable() + " " + deployment.getName());
        enableBox.setValue(Boolean.TRUE);
        layout.add(enableBox);
        
        DialogueOptions options = new DialogueOptions(new GroupSelectSubmitHandler(this.deployment, window, enableBox), new CancelHandler(window));
        Widget content = new WindowContentBuilder(layout, options).build();
        window.trapWidget(content);
        return window;
    }
    
    private Widget makeSelectionTable(ListDataProvider<ServerGroupSelection> dataProvider) {
        VerticalPanel vpanel = new VerticalPanel();
        vpanel.setStyleName("fill-layout-width");
        vpanel.getElement().setAttribute("style", "padding-top:5px;");
        
        DefaultCellTable<ServerGroupSelection> selectionTable = new DefaultCellTable<ServerGroupSelection>(5);
        dataProvider.addDataDisplay(selectionTable);
        
        Column nameColumn = new TextColumn<ServerGroupSelection>() {
            @Override
            public String getValue(ServerGroupSelection serverGroup) {
                return serverGroup.getName();
            }
        };
        
        Column profileColumn = new TextColumn<ServerGroupSelection>() {
            @Override
            public String getValue(ServerGroupSelection serverGroup) {
                return serverGroup.getProfileName();
            }
        };
        
        Column selectedColumn = new CheckboxColumn<ServerGroupSelection>() {
            @Override
            public Boolean getValue(ServerGroupSelection serverGroup) {
                return serverGroup.isSelected();
            }
        };
        
        selectionTable.addColumn(nameColumn, Console.CONSTANTS.common_label_serverGroup());
        selectionTable.addColumn(profileColumn, Console.CONSTANTS.common_label_profile());
        selectionTable.addColumn(selectedColumn, Console.CONSTANTS.common_label_addToGroup());
        
        vpanel.add(selectionTable);
        
        DefaultPager pager = new DefaultPager();
        pager.setDisplay(selectionTable);
        vpanel.add(pager);
        
        return vpanel;
    }
    
    private class CancelHandler implements ClickHandler {
        private DefaultWindow window;
        CancelHandler(DefaultWindow window) {
            this.window = window;
        }

        @Override
        public void onClick(ClickEvent event) {
            window.hide();
        }
    }
    
    private class GroupSelectSubmitHandler implements ClickHandler {
        private DefaultWindow window;
        private DeploymentRecord deployment;
        private CheckBox enableBox;
        
        GroupSelectSubmitHandler(DeploymentRecord deployment, DefaultWindow window, CheckBox enableBox) {
            this.window = window;
            this.deployment = deployment;
            this.enableBox = enableBox;
        }

        @Override
        public void onClick(ClickEvent event) {
            ArrayList<String> groupsSelected = new ArrayList<String>();
            for (ServerGroupSelection selection : ServerGroupSelector.this.selections) {
                if (selection.isSelected()) groupsSelected.add(selection.getName());
            }
            
            if (groupsSelected.isEmpty()) {
                Feedback.alert(Console.MESSAGES.selectServerGroups(), Console.MESSAGES.noServerGroupsSelected());
                return;
            }
            
            String[] selected = groupsSelected.toArray(new String[groupsSelected.size()]);
            ServerGroupSelector.this.presenter.addToServerGroup(deployment, enableBox.getValue(), selected);
            window.hide();
        }
    }
    
    // class used as a backing class for the table
    private static class ServerGroupSelection implements CheckboxColumn.Selectable {
        private ServerGroupRecord group;
        private boolean isSelected = false;
        
        ServerGroupSelection(ServerGroupRecord group) {
            this.group = group;
        }
        
        public String getName() {
            return group.getGroupName();
        }
        
        public String getProfileName() {
            return group.getProfileName();
        }
        
        @Override
        public boolean isSelected() {
            return this.isSelected;
        }
        
        @Override
        public void setSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }
    }
}
