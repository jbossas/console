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

package org.jboss.as.console.client.domain.overview;

import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.domain.groups.ServerGroupCell;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.profiles.ProfileCell;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.ballroom.client.layout.RHSContentPanel;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 1/31/11
 */
public class DomainOverview
        extends SuspendableViewImpl implements DomainOverviewPresenter.MyView {

    private DomainOverviewPresenter presenter;
    private CellList<ProfileRecord> profileList;
    private CellList<ServerGroupRecord> groupList;

    ListDataProvider<ProfileRecord> profileProvider;
    ListDataProvider<ServerGroupRecord> groupProvider;

    //private CellTable<DeploymentRecord> deploymentTable;

    @Override
    public void setPresenter(DomainOverviewPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new RHSContentPanel("Domain Overview");

        HorizontalPanel hlayout = new HorizontalPanel();
        hlayout.setStyleName("fill-layout-width");
        hlayout.getElement().setAttribute("cellpadding", "10");

        VerticalPanel vlayoutLeft = new VerticalPanel();
        vlayoutLeft.setStyleName("fill-layout-width");

        profileList = new CellList<ProfileRecord>(new ProfileCell());
        profileList.setPageSize(25);

        ContentGroupLabel leftLabel = new ContentGroupLabel("Available Profiles");
        leftLabel.setIcon("common/profile.png");
        vlayoutLeft.add(leftLabel);
        vlayoutLeft.add(profileList);

        // --------------------------------------

        VerticalPanel vlayoutRight = new VerticalPanel();
        vlayoutRight.setStyleName("fill-layout-width");

        ContentGroupLabel rightLabel = new ContentGroupLabel("Server Groups");
        rightLabel.setIcon("common/server_group.png");

        vlayoutRight.add(rightLabel);

        ServerGroupCell groupCell = new ServerGroupCell();
        groupList = new CellList<ServerGroupRecord>(groupCell);
        groupList.setPageSize(25);


        groupProvider = new ListDataProvider<ServerGroupRecord>();
        profileProvider = new ListDataProvider<ProfileRecord>();


        groupProvider.addDataDisplay(groupList);
        profileProvider.addDataDisplay(profileList);

        final SingleSelectionModel<ServerGroupRecord> selectionModel = new SingleSelectionModel<ServerGroupRecord>();
        groupList.setSelectionModel(selectionModel);
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            public void onSelectionChange(SelectionChangeEvent event) {
                ServerGroupRecord selectedRecord = selectionModel.getSelectedObject();
                final String groupName = selectedRecord.getGroupName();

                Console.getPlaceManager().revealPlaceHierarchy(
                        new ArrayList<PlaceRequest>() {{
                            add(new PlaceRequest("domain"));
                            add(new PlaceRequest(NameTokens.ServerGroupPresenter).with("name", groupName));
                        }}
                );
            }
        });


        vlayoutRight.add(groupList);
        // --------------------------------------

        hlayout.add(vlayoutLeft);
        hlayout.add(vlayoutRight);

        layout.add(hlayout);

        // --------------------------------------

        //layout.add(new ContentGroupLabel("Domain Level Deployments"));

        //deploymentTable = new DeploymentTable();
        //layout.add(deploymentTable);

        return layout;
    }

    public void updateProfiles(List<ProfileRecord> profiles)
    {
        profileProvider.setList(profiles);
    }

    public void updateGroups(List<ServerGroupRecord> groups)
    {
        groupProvider.setList(groups);
    }

    public void updateDeployments(List<DeploymentRecord> deploymentRecords) {

        //deploymentTable.setRowData(0, deploymentRecords);
    }
}
