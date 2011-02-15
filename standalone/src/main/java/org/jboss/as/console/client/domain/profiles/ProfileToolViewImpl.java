package org.jboss.as.console.client.domain.profiles;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import org.jboss.as.console.client.components.SuspendableViewImpl;
import org.jboss.as.console.client.components.sgwt.ContentGroupLabel;
import org.jboss.as.console.client.components.sgwt.DescriptionLabel;
import org.jboss.as.console.client.components.sgwt.TitleBar;

/**
 * @author Heiko Braun
 * @date 1/31/11
 */
public class ProfileToolViewImpl
        extends SuspendableViewImpl implements ProfileToolPresenter.MyView{

    private ProfileToolPresenter presenter;
    private ListGrid profileGrid;

    @Override
    public void setPresenter(ProfileToolPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        final VLayout layout = new VLayout();
        layout.setWidth100();
        layout.setHeight100();

        TitleBar titleBar = new TitleBar("Domain Overview");
        layout.addMember(titleBar);

        HLayout hlayout = new HLayout();

        VLayout vlayoutLeft = new VLayout();
        vlayoutLeft.setMargin(15);

        profileGrid = new ListGrid();
        profileGrid.setHeight(150);
        profileGrid.setShowHeader(false);
        profileGrid.setShowAllRecords(true);

        ListGridField nameField = new ListGridField("profile-name", "Name");
        nameField.setType(ListGridFieldType.TEXT);

        //ListGridField inclField = new ListGridField("includes", "Includes");

        profileGrid.setFields(nameField);
        profileGrid.setCanResizeFields(true);

        profileGrid.setData(presenter.getRecords());
        profileGrid.setMargin(5);

        Label leftLabel = new ContentGroupLabel("Available Profiles");
        vlayoutLeft.addMember(leftLabel);
        vlayoutLeft.addMember(profileGrid);

        vlayoutLeft.addMember(new HTML("<ul><li><a href=''>Add new Profile</a></ul>"));

        // --------------------------------------

        VLayout vlayoutRight = new VLayout();
        vlayoutRight.setMargin(15);
        Label rightLabel = new ContentGroupLabel("Server Groups");
        vlayoutRight.addMember(rightLabel);

        hlayout.addMember(vlayoutLeft);
        hlayout.addMember(vlayoutRight);

        layout.addMember(hlayout);

        return layout;
    }


    @Override
    public void onResume() {
        super.onResume();
        profileGrid.setData(presenter.getRecords());
    }
}
