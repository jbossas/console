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

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.deployment.DeploymentCommand;
import org.jboss.as.console.client.shared.deployment.DeploymentCommandColumn;
import org.jboss.as.console.client.shared.deployment.TitleColumn;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.as.console.client.widgets.tabs.DefaultTabLayoutPanel;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextAreaItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @author Stan Silvert <ssilvert@redhat.com> (C) 2011 Red Hat Inc.
 * @date 3/1/11
 */
public class DeploymentsOverview extends SuspendableViewImpl implements DeploymentsPresenter.MyView {

    private DeploymentsPresenter presenter;

    private ListDataProvider<DeploymentRecord> domainDeploymentProvider = new ListDataProvider<DeploymentRecord>();

    private GroupDeploymentsOverview groupOverview;
    private Map<String, List<DeploymentRecord>> serverGroupDeployments = new HashMap<String,List<DeploymentRecord>>();

    @Override
    public void setPresenter(DeploymentsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {
        DefaultTabLayoutPanel tabLayoutPanel = new DefaultTabLayoutPanel(40, Style.Unit.PX);
        tabLayoutPanel.addStyleName("default-tabpanel");

        groupOverview = new GroupDeploymentsOverview(presenter);

        tabLayoutPanel.add(makeDeploymentsPanel(), Console.CONSTANTS.common_label_deploymentContent(), true);
        tabLayoutPanel.add(groupOverview.asWidget(), Console.CONSTANTS.common_label_serverGroupDeployments(), true);

        return tabLayoutPanel;
    }

    private Widget makeDeploymentsPanel() {

        String[] columnHeaders = new String[]{Console.CONSTANTS.common_label_name(),
                Console.CONSTANTS.common_label_runtimeName(),
                Console.CONSTANTS.common_label_addToGroups(),
                Console.CONSTANTS.common_label_updateContent(),
                Console.CONSTANTS.common_label_remove()};
        List<Column> columns = makeNameAndRuntimeColumns();
        columns.add(new DeploymentCommandColumn(this.presenter, DeploymentCommand.ADD_TO_GROUP));
        columns.add(new DeploymentCommandColumn(this.presenter, DeploymentCommand.UPDATE_CONTENT));
        columns.add(new DeploymentCommandColumn(this.presenter, DeploymentCommand.REMOVE_FROM_DOMAIN));

        DefaultCellTable contentTable = new DefaultCellTable<DeploymentRecord>(8, new ProvidesKey<DeploymentRecord>() {
            @Override
            public Object getKey(DeploymentRecord deploymentRecord) {
                return deploymentRecord.getName();
            }
        });
        contentTable.setSelectionModel(new SingleSelectionModel());

        domainDeploymentProvider.addDataDisplay(contentTable);

        for (int i = 0; i < columnHeaders.length; i++) {
            contentTable.addColumn(columns.get(i), columnHeaders[i]);
        }

        Form<DeploymentRecord> form = new Form<DeploymentRecord>(DeploymentRecord.class);
        form.setNumColumns(2);
        form.setEnabled(true);
        TextAreaItem name = new TextAreaItem("name", "Name");
        TextAreaItem runtimeName = new TextAreaItem("runtimeName", "Runtime Name");
        form.setFields(name,runtimeName);

        form.bind(contentTable);

        MultipleToOneLayout layout = new MultipleToOneLayout()
                .setPlain(true)
                .setHeadline(Console.CONSTANTS.common_label_contentRepository())
                .setMaster(Console.MESSAGES.available("Deployment Content"), contentTable)
                .setMasterTools(makeAddContentButton())
                .setDescription("The content repository contains all deployed content. Contents need to be assigned to sever groups in order to become effective.")
                .addDetail(Console.CONSTANTS.common_label_selection(), form.asWidget());


        return layout.build();
    }

    private void refreshServerGroupDeploymentsTable(ServerGroupRecord selectedServerGroup) {
        /*List<DeploymentRecord> deployments = serverGroupDeployments.get(selectedServerGroup.getGroupName());
        serverGroupDeploymentsDataProvider.setList(deployments);
        this.selectedServerGroupLabel.setHTML(Console.MESSAGES.deploymentsFor(selectedServerGroup.getGroupName()));*/
    }

    @Override
    public void updateDeploymentInfo(DomainDeploymentInfo domainDeploymentInfo, DeploymentRecord... targets) {
        List<ServerGroupRecord> serverGroups = this.presenter.getServerGroups();

        ServerGroupRecord serverGroupTarget = findSingleTarget(serverGroups, targets);

        this.groupOverview.setGroups(serverGroups);

        // Set the backing data for domain tables
        domainDeploymentProvider.setList(domainDeploymentInfo.getDomainDeployments());

        this.serverGroupDeployments = domainDeploymentInfo.getServerGroupDeployments();

        setServerGroupTableSelection(serverGroupTarget);
    }

    private void setServerGroupTableSelection(ServerGroupRecord serverGroupTarget) {
        /*if (this.serverGroupTable.isEmpty()) return;

        List<ServerGroupRecord> serverGroups = this.presenter.getServerGroups();
        ServerGroupRecord previouslySelectedServerGroup = findPreviouslySelectedGroup();
        if (previouslySelectedServerGroup == null) {
            serverGroupTableSelectionModel.setSelected(serverGroups.get(0), true);
        } else if (serverGroupTarget != null) {
            serverGroupTableSelectionModel.setSelected(serverGroupTarget, true);
        } else {
            serverGroupTableSelectionModel.setSelected(previouslySelectedServerGroup, true);
        }   */
    }

    private ServerGroupRecord findPreviouslySelectedGroup() {
        /*ServerGroupRecord previouslySelected = serverGroupTableSelectionModel.getSelectedObject();
        if (previouslySelected == null) return null;
        for (ServerGroupRecord serverGroup : this.presenter.getServerGroups()) {
            if (serverGroup.getGroupName().equals(previouslySelected.getGroupName())) return serverGroup;
        }

        return null; // group not found. deleted?*/
        return null;
    }

    // find the server group, if any, that was last the serverGroupTarget of a remove or enable/disable on one of its deployments
    private ServerGroupRecord findSingleTarget(List<ServerGroupRecord> serverGroups, DeploymentRecord... targets) {
        if (targets.length != 1) return null;
        DeploymentRecord singleTarget = targets[0];

        for (ServerGroupRecord serverGroup : serverGroups) {
            if (singleTarget.getServerGroup().equals(serverGroup.getGroupName())) return serverGroup;
        }

        return null;
    }


    private ToolStrip makeAddContentButton() {
        final ToolStrip toolStrip = new ToolStrip();
        ToolButton addContentBtn = new ToolButton(Console.CONSTANTS.common_label_addContent(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewDeploymentDialoge(null, false);
            }
        });
        addContentBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_addContent_deploymentsOverview());
        toolStrip.addToolButtonRight(addContentBtn);
        return toolStrip;
    }

    private List<Column> makeNameAndRuntimeColumns() {
        List<Column> columns = new ArrayList<Column>(2);

        columns.add(new TitleColumn());

        TextColumn<DeploymentRecord> dplRuntimeColumn = new TextColumn<DeploymentRecord>() {
            @Override
            public String getValue(DeploymentRecord record) {
                String title = null;
                if(record.getRuntimeName().length()>27)
                    title = record.getRuntimeName().substring(0,26)+"...";
                else
                    title = record.getRuntimeName();
                return title;
            }
        };

        columns.add(dplRuntimeColumn);

        return columns;
    }

}