package org.jboss.as.console.client.domain.profiles;

import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.NameTokens;
import org.jboss.as.console.client.components.SuspendableViewImpl;
import org.jboss.as.console.client.components.sgwt.ContentGroupLabel;
import org.jboss.as.console.client.components.sgwt.TitleBar;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;

import java.util.ArrayList;

/**
 * @author Heiko Braun
 * @date 1/31/11
 */
public class ProfileOverview
        extends SuspendableViewImpl implements ProfileOverviewPresenter.MyView {

    private ProfileOverviewPresenter presenter;
    private ListGrid profileGrid;
    private ListGrid groupGrid;

    @Override
    public void setPresenter(ProfileOverviewPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        final VLayout layout = new VLayout();
        layout.setWidth100();
        layout.setHeight100();

        TitleBar titleBar = new TitleBar("Domain Overview");
        layout.addMember(titleBar);
        //layout.addMember(new DescriptionLabel("Available Profiles and Server Groups."));

        HLayout hlayout = new HLayout();

        VLayout vlayoutLeft = new VLayout();
        vlayoutLeft.setMargin(15);

        profileGrid = new ListGrid();
        profileGrid.setHeight(150);
        profileGrid.setShowHeader(false);
        profileGrid.setShowAllRecords(true);

        ListGridField nameField = new ListGridField("profile-name", "Name");
        profileGrid.setFields(nameField);
        profileGrid.setMargin(5);

        ContentGroupLabel leftLabel = new ContentGroupLabel("Available Profiles");
        leftLabel.setIcon("common/profile.png");
        vlayoutLeft.addMember(leftLabel);
        vlayoutLeft.addMember(profileGrid);

        // --------------------------------------

        VLayout vlayoutRight = new VLayout();
        vlayoutRight.setMargin(15);
        ContentGroupLabel rightLabel = new ContentGroupLabel("Server Groups");
        rightLabel.setIcon("common/server_group.png");

        vlayoutRight.addMember(rightLabel);

        groupGrid = new ListGrid();
        groupGrid.setHeight(150);
        groupGrid.setShowHeader(false);
        groupGrid.setShowAllRecords(true);

        ListGridField groupNameField = new ListGridField("group-name", "Server Group");
        ListGridField profileNameField = new ListGridField("profile-name", "Profile");

        groupGrid.setFields(groupNameField, profileNameField);
        groupGrid.setMargin(5);

        groupGrid.addRecordClickHandler(new RecordClickHandler()
        {
            @Override
            public void onRecordClick(RecordClickEvent recordClickEvent) {
                ServerGroupRecord selectedRecord = (ServerGroupRecord)groupGrid.getSelectedRecord();
                final String groupName = selectedRecord.getAttribute("group-name");

                Console.MODULES.getPlaceManager().revealPlaceHierarchy(
                        new ArrayList<PlaceRequest>() {{
                            add(new PlaceRequest("domain"));
                            add(new PlaceRequest(NameTokens.ServerGroupPresenter).with("name", groupName));
                        }}
                );
            }
        });

        vlayoutRight.addMember(groupGrid);
        // --------------------------------------

        hlayout.addMember(vlayoutLeft);
        hlayout.addMember(vlayoutRight);

        layout.addMember(hlayout);

        // --------------------------------------

        ContentGroupLabel deploymentLabel = new ContentGroupLabel("Domain Level Deployments");
        deploymentLabel.setMargin(15);

        layout.addMember(deploymentLabel);


        ListGrid deploymentGrid = new ListGrid();
        deploymentGrid.setMargin(15);
        deploymentGrid.setWidth100();
        deploymentGrid.setHeight100();
        deploymentGrid.setShowAllRecords(true);

        ListGridField dplNameField = new ListGridField("name", "Name");
        ListGridField dplRtField = new ListGridField("runtime-name", "Runtime Name");
        deploymentGrid.setFields(dplNameField, dplRtField);
        deploymentGrid.setData(presenter.getDeploymentRecords());

        layout.addMember(deploymentGrid);

        refresh();

        return layout;
    }


    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        profileGrid.setData(presenter.getProfileRecords());
        groupGrid.setData(presenter.getServerGroupRecords());
    }
}
