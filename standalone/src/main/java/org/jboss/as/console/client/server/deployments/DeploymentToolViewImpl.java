package org.jboss.as.console.client.server.deployments;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.components.RHSContentPanel;
import org.jboss.as.console.client.components.SuspendableViewImpl;
import org.jboss.as.console.client.shared.DeploymentRecord;
import org.jboss.as.console.client.shared.DeploymentTable;

/**
 * @author Heiko Braun
 * @date 1/31/11
 */
public class DeploymentToolViewImpl
        extends SuspendableViewImpl implements DeploymentToolPresenter.DeploymentToolView {

    private DeploymentToolPresenter presenter;
    private CellTable<DeploymentRecord> deploymentTable;

    @Override
    public void setPresenter(DeploymentToolPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        final LayoutPanel layout = new RHSContentPanel("Deployments");
        deploymentTable = new DeploymentTable();
        layout.add(deploymentTable);

        return layout;
    }


    @Override
    public void onResume() {
        super.onResume();
        deploymentTable.setRowData(0, presenter.getRecords());
    }
}
