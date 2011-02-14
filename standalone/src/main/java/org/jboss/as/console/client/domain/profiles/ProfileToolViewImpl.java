package org.jboss.as.console.client.domain.profiles;

import com.google.gwt.user.client.ui.Widget;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.VLayout;
import org.jboss.as.console.client.components.SuspendableViewImpl;
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

        TitleBar titleBar = new TitleBar("Server Configuration Profiles");
        layout.addMember(titleBar);

        layout.addMember(new DescriptionLabel("Manage profiles and subsystems."));

        profileGrid = new ListGrid();
        profileGrid.setWidth100();
        profileGrid.setHeight100();
        profileGrid.setShowAllRecords(true);

        ListGridField nameField = new ListGridField("profile-name", "Name");
        nameField.setType(ListGridFieldType.TEXT);

        //ListGridField inclField = new ListGridField("includes", "Includes");

        profileGrid.setFields(nameField);
        profileGrid.setCanResizeFields(true);

        profileGrid.setData(presenter.getRecords());

        layout.addMember(profileGrid);

        return layout;
    }


    @Override
    public void onResume() {
        super.onResume();
        profileGrid.setData(presenter.getRecords());
    }
}
