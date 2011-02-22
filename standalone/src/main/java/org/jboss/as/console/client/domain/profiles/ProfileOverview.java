package org.jboss.as.console.client.domain.profiles;

import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.NameTokens;
import org.jboss.as.console.client.components.RHSContentPanel;
import org.jboss.as.console.client.components.SuspendableViewImpl;
import org.jboss.as.console.client.components.sgwt.ContentGroupLabel;
import org.jboss.as.console.client.domain.groups.ServerGroupCell;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.DeploymentRecord;
import org.jboss.as.console.client.shared.tables.DefaultCellTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 1/31/11
 */
public class ProfileOverview
        extends SuspendableViewImpl implements ProfileOverviewPresenter.MyView {

    private ProfileOverviewPresenter presenter;
    private CellList<ProfileRecord> profileList;
    private CellList<ServerGroupRecord> groupList;
    private CellTable<DeploymentRecord> deploymentTable;

    @Override
    public void setPresenter(ProfileOverviewPresenter presenter) {
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

        final SingleSelectionModel<ServerGroupRecord> selectionModel = new SingleSelectionModel<ServerGroupRecord>();
        groupList.setSelectionModel(selectionModel);
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            public void onSelectionChange(SelectionChangeEvent event) {
                ServerGroupRecord selectedRecord = selectionModel.getSelectedObject();
                final String groupName = selectedRecord.getGroupName();

                Console.MODULES.getPlaceManager().revealPlaceHierarchy(
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

        ContentGroupLabel deploymentLabel = new ContentGroupLabel("Domain Level Deployments");
        layout.add(deploymentLabel);

        deploymentTable = new DefaultCellTable<DeploymentRecord>(10);

        TextColumn<DeploymentRecord> dplNameColumn = new TextColumn<DeploymentRecord>() {
            @Override
            public String getValue(DeploymentRecord record) {
                return record.getName();
            }
        };

        TextColumn<DeploymentRecord> dplRuntimeColumn = new TextColumn<DeploymentRecord>() {
            @Override
            public String getValue(DeploymentRecord record) {
                return record.getRuntimeName();
            }
        };

        TextColumn<DeploymentRecord> dplShaColumn = new TextColumn<DeploymentRecord>() {
            @Override
            public String getValue(DeploymentRecord record) {
                return record.getSha();
            }
        };

        deploymentTable.addColumn(dplNameColumn, "Name");
        deploymentTable.addColumn(dplRuntimeColumn, "Runtime Name");
        deploymentTable.addColumn(dplShaColumn, "Sha");

        layout.add(deploymentTable);

        refresh();

        return layout;
    }


    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        List<ProfileRecord> profiles = Arrays.asList(presenter.getProfileRecords());
        List<ServerGroupRecord> groups = Arrays.asList(presenter.getServerGroupRecords());

        profileList.setRowData(0, profiles);
        groupList.setRowData(0, groups);
        deploymentTable.setRowData(0, presenter.getDeploymentRecords());
    }
}
