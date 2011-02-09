package org.jboss.as.console.client.server.deployments;

import com.gwtplatform.mvp.client.ViewImpl;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.VLayout;
import org.jboss.as.console.client.components.TitleBar;

/**
 * @author Heiko Braun
 * @date 1/31/11
 */
public class DeploymentToolViewImpl
        extends ViewImpl implements DeploymentToolPresenter.DeploymentToolView {

    private DeploymentToolPresenter presenter;

    @Override
    public void setPresenter(DeploymentToolPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Canvas asWidget() {
        VLayout layout = new VLayout();
        layout.setWidth100();
        layout.setHeight100();

        TitleBar titleBar = new TitleBar("Deployments");
        layout.addMember(titleBar);

        final ListGrid deploymentGrid = new ListGrid();
        deploymentGrid.setWidth100();
        deploymentGrid.setHeight100();
        deploymentGrid.setShowAllRecords(true);

        ListGridField keyField = new ListGridField("key", "Key", 40);
        keyField.setAlign(Alignment.CENTER);
        keyField.setType(ListGridFieldType.TEXT);

        ListGridField nameField = new ListGridField("deploymentName", "Name");
        ListGridField dateField = new ListGridField("since", "Deployed Since");

        deploymentGrid.setFields(keyField, nameField, dateField);
        deploymentGrid.setCanResizeFields(true);

        deploymentGrid.setData(presenter.getRecords());

        layout.addMember(deploymentGrid);

        return layout;
    }
}
