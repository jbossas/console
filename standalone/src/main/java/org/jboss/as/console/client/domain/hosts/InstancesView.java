package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.shared.DeploymentRecord;
import org.jboss.as.console.client.widgets.ContentGroupLabel;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.TitleBar;
import org.jboss.as.console.client.widgets.icons.Icons;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;
import org.jboss.as.console.client.widgets.tables.DefaultEditTextCell;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/8/11
 */
public class InstancesView extends SuspendableViewImpl implements InstancesPresenter.MyView {

    private InstancesPresenter presenter;
    private ContentHeaderLabel nameLabel;
    private ListDataProvider<ServerInstance> instanceProvider;

    @Override
    public void setPresenter(InstancesPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {
        LayoutPanel layout = new LayoutPanel();

        TitleBar titleBar = new TitleBar("Server Instances");
        layout.add(titleBar);

        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("fill-layout-width");
        panel.getElement().setAttribute("style", "padding:15px;");

        layout.add(panel);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 28, Style.Unit.PX);
        layout.setWidgetTopHeight(panel, 35, Style.Unit.PX, 100, Style.Unit.PCT);

        // ----------------------------------------------------------------------

        nameLabel = new ContentHeaderLabel("Server Status");

        HorizontalPanel horzPanel = new HorizontalPanel();
        horzPanel.getElement().setAttribute("style", "width:100%;");
        Image image = new Image(Icons.INSTANCE.serverInstance());
        horzPanel.add(image);
        horzPanel.add(nameLabel);

        image.getElement().getParentElement().setAttribute("width", "25");

        panel.add(horzPanel);

         // ----------------------------------------------------------------------


        CellTable<ServerInstance> instanceTable = new DefaultCellTable<ServerInstance>(10);
        instanceProvider = new ListDataProvider<ServerInstance>();
        instanceProvider.addDataDisplay(instanceTable);

        // Create columns
        Column<ServerInstance, String> nameColumn = new Column<ServerInstance, String>(new DefaultEditTextCell()) {
            @Override
            public String getValue(ServerInstance object) {
                return object.getName();
            }
        };

        Column<ServerInstance, ImageResource> statusColumn =
                new Column<ServerInstance, ImageResource>(new ImageResourceCell()) {
            @Override
            public ImageResource getValue(ServerInstance instance) {

                ImageResource res = null;

                if(instance.isRunning())
                    res = Icons.INSTANCE.statusGreen_small();
                else
                    res = Icons.INSTANCE.statusRed_small();

                return res;
            }
        };

        instanceTable.addColumn(nameColumn, "Instance Name");
        instanceTable.addColumn(statusColumn, "Status");

        panel.add(instanceTable);

        return layout;
    }

    @Override
    public void setSelectedHost(String selectedHost) {
        //nameLabel.setText("Server Status (Host: "+selectedHost+")");
    }

    @Override
    public void updateInstances(List<ServerInstance> instances) {
        instanceProvider.setList(instances);
    }
}
