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

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.deployment.DeploymentCommand;
import org.jboss.as.console.client.shared.deployment.DeploymentCommandColumn;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.as.console.client.widgets.tabs.DefaultTabLayoutPanel;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.icons.Icons;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

import java.util.ArrayList;
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
    private ListDataProvider<ServerGroupRecord> serverGroupDeploymentProvider = new ListDataProvider<ServerGroupRecord>();
    private ListDataProvider<DeploymentRecord> serverGroupDeploymentsDataProvider = new ListDataProvider<DeploymentRecord>();

    private DefaultCellTable<ServerGroupRecord> serverGroupTable;
    private SingleSelectionModel<ServerGroupRecord> serverGroupTableSelectionModel;

    private DefaultCellTable<DeploymentRecord> serverGroupDeploymentTable;

    private Map<String, List<DeploymentRecord>> serverGroupDeployments;

    private ContentGroupLabel selectedServerGroupLabel = new ContentGroupLabel("");


    @Override
    public void setPresenter(DeploymentsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {
        DefaultTabLayoutPanel tabLayoutPanel = new DefaultTabLayoutPanel(40, Style.Unit.PX);
        tabLayoutPanel.addStyleName("default-tabpanel");

        tabLayoutPanel.add(makeDeploymentsPanel(), Console.CONSTANTS.common_label_deploymentContent(), true);
        tabLayoutPanel.add(makeServerGroupDeploymentsPanel(), Console.CONSTANTS.common_label_serverGroupDeployments(), true);

        return tabLayoutPanel;
    }

    private Widget makeDeploymentsPanel() {
        LayoutPanel layout = new LayoutPanel();

        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("rhs-content-panel");

        String[] columnHeaders = new String[]{Console.CONSTANTS.common_label_name(),
                Console.CONSTANTS.common_label_runtimeName(),
                Console.CONSTANTS.common_label_addToGroups(),
                Console.CONSTANTS.common_label_updateContent(),
                Console.CONSTANTS.common_label_remove()};
        List<Column> columns = makeNameAndRuntimeColumns();
        columns.add(new DeploymentCommandColumn(this.presenter, DeploymentCommand.ADD_TO_GROUP));
        columns.add(new DeploymentCommandColumn(this.presenter, DeploymentCommand.UPDATE_CONTENT));
        columns.add(new DeploymentCommandColumn(this.presenter, DeploymentCommand.REMOVE_FROM_DOMAIN));

        ContentGroupLabel repositoryLabel = new ContentGroupLabel(Console.CONSTANTS.common_label_contentRepository());

        Widget domainDeploymentTable = makeDeploymentTable(repositoryLabel, domainDeploymentProvider, columns, columnHeaders);

        panel.add(domainDeploymentTable);

        ScrollPanel scroll = new ScrollPanel(panel);
        layout.add(scroll);

        layout.setWidgetTopHeight(scroll, 0, Style.Unit.PX, 100, Style.Unit.PCT);

        return layout;
    }

    private Widget makeServerGroupDeploymentsPanel() {
        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("rhs-content-panel");

        panel.add(new ContentHeaderLabel(Console.CONSTANTS.common_label_serverGroups()));
        panel.add(makeServerGroupTable());
        panel.add(this.selectedServerGroupLabel);
        panel.add(this.makeServerGroupDeploymentsTable());

        wireTablesTogether();

        ScrollPanel scroll = new ScrollPanel(panel);
        return scroll;
    }

    private void wireTablesTogether() {
        final SingleSelectionModel<ServerGroupRecord> selectionModel = this.serverGroupTableSelectionModel;
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                ServerGroupRecord selectedServerGroup = selectionModel.getSelectedObject();
                refreshServerGroupDeploymentsTable(selectedServerGroup);
            }
        });
    }

    private void refreshServerGroupDeploymentsTable(ServerGroupRecord selectedServerGroup) {
        List<DeploymentRecord> deployments = serverGroupDeployments.get(selectedServerGroup.getGroupName());
        serverGroupDeploymentsDataProvider.setList(deployments);
        this.selectedServerGroupLabel.setHTML(Console.MESSAGES.deploymentsFor(selectedServerGroup.getGroupName()));
    }

    private Widget makeServerGroupTable() {
        VerticalPanel vpanel = new VerticalPanel();
        vpanel.setStyleName("fill-layout-width");
        vpanel.getElement().setAttribute("style", "padding-top:5px;");

        serverGroupTable = new DefaultCellTable<ServerGroupRecord>(5);
        this.serverGroupDeploymentProvider.addDataDisplay(serverGroupTable);
        this.serverGroupTableSelectionModel = new SingleSelectionModel<ServerGroupRecord>();
        serverGroupTable.setSelectionModel(serverGroupTableSelectionModel);

        Column nameColumn = new TextColumn<ServerGroupRecord>() {
            @Override
            public String getValue(ServerGroupRecord serverGroup) {
                return serverGroup.getGroupName();
            }
        };

        Column profileColumn = new TextColumn<ServerGroupRecord>() {
            @Override
            public String getValue(ServerGroupRecord serverGroup) {
                return serverGroup.getProfileName();
            }
        };

        serverGroupTable.addColumn(nameColumn, Console.CONSTANTS.common_label_serverGroup());
        serverGroupTable.addColumn(profileColumn, Console.CONSTANTS.common_label_profile());

        vpanel.add(serverGroupTable);

        DefaultPager pager = new DefaultPager();
        pager.setDisplay(serverGroupTable);
        vpanel.add(pager);

        return vpanel;
    }

    private Widget makeServerGroupDeploymentsTable() {
        VerticalPanel vpanel = new VerticalPanel();
        vpanel.setStyleName("fill-layout-width");
        vpanel.getElement().setAttribute("style", "padding-top:5px;");

        String[] columnHeaders = new String[]{Console.CONSTANTS.common_label_name(),
                Console.CONSTANTS.common_label_runtimeName(),
                Console.CONSTANTS.common_label_enabled(),
                Console.CONSTANTS.common_label_enOrDisable(),
                Console.CONSTANTS.common_label_remove()};
        List<Column> columns = makeNameAndRuntimeColumns();
        columns.add(makeEnabledColumn());
        columns.add(new DeploymentCommandColumn(this.presenter, DeploymentCommand.ENABLE_DISABLE));
        columns.add(new DeploymentCommandColumn(this.presenter, DeploymentCommand.REMOVE_FROM_GROUP));

        vpanel.add(makeDeploymentTable(this.selectedServerGroupLabel, serverGroupDeploymentsDataProvider, columns, columnHeaders));

        return vpanel;
    }

    @Override
    public void updateDeploymentInfo(DomainDeploymentInfo domainDeploymentInfo, DeploymentRecord... targets) {
        List<ServerGroupRecord> serverGroups = this.presenter.getServerGroups();

        ServerGroupRecord serverGroupTarget = findSingleTarget(serverGroups, targets);

        this.serverGroupDeploymentProvider.setList(serverGroups);

        // Set the backing data for domain tables
        domainDeploymentProvider.setList(domainDeploymentInfo.getDomainDeployments());

        this.serverGroupDeployments = domainDeploymentInfo.getServerGroupDeployments();

        setServerGroupTableSelection(serverGroupTarget);
    }

    private void setServerGroupTableSelection(ServerGroupRecord serverGroupTarget) {
        if (this.serverGroupTable.isEmpty()) return;

        List<ServerGroupRecord> serverGroups = this.presenter.getServerGroups();
        ServerGroupRecord previouslySelectedServerGroup = findPreviouslySelectedGroup();
        if (previouslySelectedServerGroup == null) {
            serverGroupTableSelectionModel.setSelected(serverGroups.get(0), true);
        } else if (serverGroupTarget != null) {
            serverGroupTableSelectionModel.setSelected(serverGroupTarget, true);
        } else {
            serverGroupTableSelectionModel.setSelected(previouslySelectedServerGroup, true);
        }
    }

    private ServerGroupRecord findPreviouslySelectedGroup() {
        ServerGroupRecord previouslySelected = serverGroupTableSelectionModel.getSelectedObject();
        if (previouslySelected == null) return null;
        for (ServerGroupRecord serverGroup : this.presenter.getServerGroups()) {
            if (serverGroup.getGroupName().equals(previouslySelected.getGroupName())) return serverGroup;
        }

        return null; // group not found. deleted?
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

    private Widget makeDeploymentTable(
            ContentGroupLabel tableLabel,
            ListDataProvider<DeploymentRecord> dataProvider,
            List<Column> columns,
            String[] columnHeaders) {

        VerticalPanel vpanel = new VerticalPanel();
        vpanel.setStyleName("fill-layout-width");
        vpanel.getElement().setAttribute("style", "padding-top:5px;");

        vpanel.add(tableLabel);
        this.serverGroupDeploymentTable = new DefaultCellTable<DeploymentRecord>(10);
        this.serverGroupDeploymentTable.setSelectionModel(new SingleSelectionModel());

        dataProvider.addDataDisplay(serverGroupDeploymentTable);

        for (int i = 0; i < columnHeaders.length; i++) {
            serverGroupDeploymentTable.addColumn(columns.get(i), columnHeaders[i]);
        }

        final ToolStrip toolStrip = new ToolStrip();
        ToolButton addContentBtn = new ToolButton(Console.CONSTANTS.common_label_addContent(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewDeploymentDialoge(null, false);
            }
        });
        addContentBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_addContent_deploymentsOverview());
        toolStrip.addToolButtonRight(addContentBtn);

        vpanel.add(toolStrip.asWidget());
        vpanel.add(serverGroupDeploymentTable);

        DefaultPager pager = new DefaultPager();
        pager.setDisplay(serverGroupDeploymentTable);

        vpanel.add(pager);

        return vpanel;
    }

    private List<Column> makeNameAndRuntimeColumns() {
        List<Column> columns = new ArrayList<Column>(2);

        columns.add(new TextColumn<DeploymentRecord>() {

            @Override
            public String getValue(DeploymentRecord record) {
                return record.getName();
            }
        });

        columns.add(new TextColumn<DeploymentRecord>() {

            @Override
            public String getValue(DeploymentRecord record) {
                return record.getRuntimeName();
            }
        });

        return columns;
    }

    private Column makeEnabledColumn() {
        return new Column<DeploymentRecord, ImageResource>(new ImageResourceCell()) {

            @Override
            public ImageResource getValue(DeploymentRecord deployment) {

                ImageResource res = null;

                if (deployment.isEnabled()) {
                    res = Icons.INSTANCE.status_good();
                } else {
                    res = Icons.INSTANCE.status_bad();
                }

                return res;
            }

        };
    }

}