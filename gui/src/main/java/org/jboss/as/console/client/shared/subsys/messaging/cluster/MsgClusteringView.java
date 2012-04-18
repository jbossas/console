package org.jboss.as.console.client.shared.subsys.messaging.cluster;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.subsys.messaging.ProviderList;
import org.jboss.as.console.client.shared.subsys.messaging.model.BroadcastGroup;
import org.jboss.as.console.client.shared.subsys.messaging.model.DiscoveryGroup;
import org.jboss.as.console.client.widgets.pages.PagedView;
import org.jboss.ballroom.client.widgets.tabs.FakeTabPanel;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 4/18/12
 */
public class MsgClusteringView extends SuspendableViewImpl implements MsgClusteringPresenter.MyView {
    private MsgClusteringPresenter presenter;
    private PagedView panel;
    private ProviderList providerList;
    private BroadcastGroupList broadcastGroupList;
    private DiscoveryGroupList discoveryGroupList;

    @Override
    public void setPresenter(MsgClusteringPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {
        LayoutPanel layout = new LayoutPanel();

        FakeTabPanel titleBar = new FakeTabPanel("Messaging Clustering");
        layout.add(titleBar);

        panel = new PagedView();

        providerList = new ProviderList(presenter, NameTokens.MsgClusteringPresenter);
        broadcastGroupList = new BroadcastGroupList(presenter);
        discoveryGroupList = new DiscoveryGroupList(presenter);

        panel.addPage(Console.CONSTANTS.common_label_back(), providerList.asWidget());
        panel.addPage("Broadcast", broadcastGroupList.asWidget()) ;
        panel.addPage("Discovery", discoveryGroupList.asWidget()) ;
        panel.addPage("Connections", new HTML()) ;


        // default page
        panel.showPage(0);


        Widget panelWidget = panel.asWidget();
        layout.add(panelWidget);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 40, Style.Unit.PX);
        layout.setWidgetTopHeight(panelWidget, 40, Style.Unit.PX, 100, Style.Unit.PCT);

        return layout;
    }

    @Override
    public void setSelectedProvider(String selectedProvider) {


        if(null==selectedProvider)
        {
            panel.showPage(0);
        }
        else{

            presenter.loadDetails(selectedProvider);

            // move to first page if still showing overview
            if(0==panel.getPage())
                panel.showPage(1);
        }
    }

    @Override
    public void setProvider(List<String> provider) {
        providerList.setProvider(provider);
    }

    @Override
    public void setBroadcastGroups(List<BroadcastGroup> groups) {
        broadcastGroupList.setBroadcastGroups(groups);
    }

    @Override
    public void setDiscoveryGroups(List<DiscoveryGroup> groups) {
        discoveryGroupList.setDiscoveryGroups(groups);
    }
}
