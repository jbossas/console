package org.jboss.as.console.client.server.deployments;

import com.google.gwt.user.client.ui.Widget;
import com.smartgwt.client.types.Alignment;
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
public class DeploymentToolViewImpl
        extends SuspendableViewImpl implements DeploymentToolPresenter.DeploymentToolView {

    private DeploymentToolPresenter presenter;
    private ListGrid deploymentGrid;

    @Override
    public void setPresenter(DeploymentToolPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        final VLayout layout = new VLayout();
        layout.setWidth100();
        layout.setHeight100();

        TitleBar titleBar = new TitleBar("Deployments");
        layout.addMember(titleBar);

        layout.addMember(new DescriptionLabel("Application deployments"));

        deploymentGrid = new ListGrid();
        deploymentGrid.setWidth100();
        deploymentGrid.setHeight100();
        deploymentGrid.setShowAllRecords(true);

        ListGridField keyField = new ListGridField("name", "Name", 40);
        keyField.setAlign(Alignment.CENTER);
        keyField.setType(ListGridFieldType.TEXT);

        ListGridField nameField = new ListGridField("runtime-name", "Runtime Name");
        ListGridField dateField = new ListGridField("sha", "sha");

        deploymentGrid.setFields(keyField, nameField, dateField);
        deploymentGrid.setCanResizeFields(true);

        deploymentGrid.setData(presenter.getRecords());

        layout.addMember(deploymentGrid);

        return layout;
    }


    @Override
    public void onResume() {
        super.onResume();
        deploymentGrid.setData(presenter.getRecords());
    }
}
