package org.jboss.as.console.client.shared.subsys.jms;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.subsys.jms.model.JMSEndpoint;
import org.jboss.as.console.client.widgets.ContentGroupLabel;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.RHSContentPanel;
import org.jboss.as.console.client.widgets.icons.Icons;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/29/11
 */
public class JMSView extends DisposableViewImpl implements JMSPresenter.MyView{

    private JMSPresenter presenter;

    private DefaultCellTable<JMSEndpoint> endpointTable;

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new RHSContentPanel("JMS Management");

        // ---

        HorizontalPanel horzPanel = new HorizontalPanel();
        horzPanel.getElement().setAttribute("style", "width:100%;");
        Image image = new Image(Icons.INSTANCE.messaging());
        horzPanel.add(image);
        horzPanel.add(new ContentHeaderLabel("JMS Endpoints"));
        image.getElement().getParentElement().setAttribute("width", "25");

        layout.add(horzPanel);

        // ----

        layout.add(new ContentGroupLabel("Queues & Topics"));
        endpointTable = new EndpointTable();
        layout.add(endpointTable);

        return layout;
    }

    @Override
    public void setPresenter(JMSPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateEndpoints(List<JMSEndpoint> endpoints) {
        endpointTable.setRowCount(endpoints.size());
        endpointTable.setRowData(0, endpoints);
    }
}
