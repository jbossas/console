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

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.deployment.DeploymentStore;
import org.jboss.as.console.client.shared.deployment.model.ContentRepository;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
import org.jboss.as.console.client.widgets.pages.PagedView;
import org.jboss.as.console.client.widgets.tables.ViewLinkCell;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

/**
 * Shows the server groups with a link to ServerGroupDeploymentView
 * @author Harald Pehl
 * @date 12/12/2012
 */
public class ServerGroupDeploymentPanel implements IsWidget
{
    private final Widget widget;
    private final DomainDeploymentPresenter presenter;
    private final DeploymentStore deploymentStore;
    private PagedView pagedView;
    private ListDataProvider<ServerGroupRecord> serverGroupData;
    private ServerGroupDeploymentBrowser groupDeploymentBrowser;
    private ContentRepository contentRepository;


    public ServerGroupDeploymentPanel(DomainDeploymentPresenter presenter, DeploymentStore deploymentStore)
    {
        this.presenter = presenter;
        this.deploymentStore = deploymentStore;
        this.widget = initUI();
    }

    @SuppressWarnings("unchecked")
    private Widget initUI()
    {
        pagedView = new PagedView();

        ProvidesKey<ServerGroupRecord> keyProvider = new ProvidesKey<ServerGroupRecord>()
        {
            @Override
            public Object getKey(ServerGroupRecord serverGroupRecord)
            {
                return serverGroupRecord.getName();
            }
        };
        DefaultCellTable<ServerGroupRecord> serverGroups = new DefaultCellTable<ServerGroupRecord>(8,
                keyProvider);
        serverGroupData = new ListDataProvider<ServerGroupRecord>();
        this.serverGroupData.addDataDisplay(serverGroups);

        final SingleSelectionModel<ServerGroupRecord> selectionModel = new SingleSelectionModel<ServerGroupRecord>(keyProvider);
        serverGroups.setSelectionModel(selectionModel);

        Column nameColumn = new TextColumn<ServerGroupRecord>() {
            @Override
            public String getValue(ServerGroupRecord serverGroup) {
                return serverGroup.getName();
            }
        };
        Column profileColumn = new TextColumn<ServerGroupRecord>() {
            @Override
            public String getValue(ServerGroupRecord serverGroup) {
                return serverGroup.getProfileName();
            }
        };
        serverGroups.addColumn(nameColumn, Console.CONSTANTS.common_label_serverGroup());
        serverGroups.addColumn(profileColumn, Console.CONSTANTS.common_label_profile());

        Column<ServerGroupRecord, ServerGroupRecord> option =
                new Column<ServerGroupRecord, ServerGroupRecord>(
                    new ViewLinkCell<ServerGroupRecord>(Console.CONSTANTS.common_label_view(),
                    new ActionCell.Delegate<ServerGroupRecord>()
                    {
                        @Override
                        public void execute(ServerGroupRecord selection)
                        {
                            groupDeploymentBrowser.setGroup(selection);
                            groupDeploymentBrowser.setDeployments(contentRepository.getDeployments(selection));
                            pagedView.showPage(1);
                        }
                    }))
                {
                    @Override
                    public ServerGroupRecord getValue(ServerGroupRecord manager)
                    {
                        return manager;
                    }
                };
        serverGroups.addColumn(option, Console.CONSTANTS.common_label_option());

        SimpleLayout overviewPanel = new SimpleLayout()
                .setPlain(true)
                .setHeadline("Server Groups")
                .setDescription("Please chose a server group to assign deployment contents.")
                .addContent("Available Groups", serverGroups.asWidget());

        groupDeploymentBrowser = new ServerGroupDeploymentBrowser(presenter, deploymentStore);
        pagedView.addPage(Console.CONSTANTS.common_label_back(), overviewPanel.build());
        pagedView.addPage("Group Deployments", groupDeploymentBrowser.asWidget());
        pagedView.showPage(0);

        LayoutPanel layout = new LayoutPanel();
        layout.add(pagedView.asWidget());
        return layout;
    }

    @Override
    public Widget asWidget()
    {
        return widget;
    }

    void updateContentRepository(final ContentRepository contentRepository)
    {
        this.contentRepository = contentRepository;
        serverGroupData.setList(contentRepository.getServerGroups());
    }
}
