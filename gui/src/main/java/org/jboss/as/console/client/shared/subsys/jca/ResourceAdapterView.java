package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.subsys.jca.model.PoolConfig;
import org.jboss.as.console.client.shared.subsys.jca.model.ResourceAdapter;
import org.jboss.as.console.client.widgets.pages.PagedView;
import org.jboss.ballroom.client.widgets.tabs.FakeTabPanel;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 7/19/11
 */
public class ResourceAdapterView extends SuspendableViewImpl implements ResourceAdapterPresenter.MyView {

    private ResourceAdapterPresenter presenter;
    private PagedView panel;
    private AdapterList adapterList;
    private List<ResourceAdapter> adapters;
    private ConnectionList connectionList;

    @Override
    public void setPresenter(ResourceAdapterPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new LayoutPanel();

        FakeTabPanel titleBar = new FakeTabPanel("Resource Adapter");
        layout.add(titleBar);

        panel = new PagedView();

        this.adapterList = new AdapterList(presenter);
        this.connectionList = new ConnectionList(presenter);

        panel.addPage("&larr; Back to Overview", adapterList.asWidget());
        panel.addPage("Connection Definitions", connectionList.asWidget());

        // default page
        panel.showPage(0);

        Widget panelWidget = panel.asWidget();
        layout.add(panelWidget);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 28, Style.Unit.PX);
        layout.setWidgetTopHeight(panelWidget, 28, Style.Unit.PX, 100, Style.Unit.PCT);

        return layout;
    }

    @Override
    public void setSelectedAdapter(String selectedAdapter) {

        if(null==selectedAdapter)
        {
            panel.showPage(0);
        }
        else{
            for(ResourceAdapter adapter : adapters)
            {
                if(adapter.getName().equals(selectedAdapter))
                {
                    connectionList.setAdapter(adapter);
                    break;
                }
            }

             panel.showPage(1);
        }
    }

    @Override
    public void setAdapters(List<ResourceAdapter> adapters) {
        this.adapters = adapters;
        adapterList.setAdapters(adapters);

    }
}
