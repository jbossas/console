package org.jboss.as.console.client.shared.subsys.messaging.connections;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.subsys.messaging.ProviderList;
import org.jboss.as.console.client.shared.subsys.messaging.model.Acceptor;
import org.jboss.as.console.client.shared.subsys.messaging.model.Connector;
import org.jboss.as.console.client.shared.subsys.messaging.model.ConnectorService;
import org.jboss.as.console.client.widgets.pages.PagedView;
import org.jboss.ballroom.client.widgets.tabs.FakeTabPanel;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 4/2/12
 */
public class MsgConnectionsView extends SuspendableViewImpl implements MsgConnectionsPresenter.MyView {

    private PagedView panel;
    private MsgConnectionsPresenter presenter;
    private ProviderList providerList;
    private AcceptorOverview acceptorOverview;
    private ConnectorOverview connectorOverview;
    private ConnectorServiceList connectorServiceList;


    @Override
    public Widget createWidget() {

        LayoutPanel layout = new LayoutPanel();

        FakeTabPanel titleBar = new FakeTabPanel("Messaging Connections");
        layout.add(titleBar);

        panel = new PagedView();

        providerList = new ProviderList(presenter, NameTokens.MsgConnectionsPresenter);
        acceptorOverview = new AcceptorOverview(presenter);
        connectorOverview = new ConnectorOverview(presenter);
        connectorServiceList = new ConnectorServiceList(presenter);

        panel.addPage(Console.CONSTANTS.common_label_back(), providerList.asWidget());
        panel.addPage("Acceptor", acceptorOverview.asWidget()) ;
        panel.addPage("Connector", connectorOverview.asWidget()) ;
        panel.addPage("Connector Services", connectorServiceList.asWidget()) ;
        panel.addPage("Bridges", new HTML()) ;

        // default page
        panel.showPage(0);


        Widget panelWidget = panel.asWidget();
        layout.add(panelWidget);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 40, Style.Unit.PX);
        layout.setWidgetTopHeight(panelWidget, 40, Style.Unit.PX, 100, Style.Unit.PCT);

        return layout;
    }

    @Override
    public void setPresenter(MsgConnectionsPresenter presenter) {
        this.presenter = presenter;
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
    public void setGenericAcceptors(List<Acceptor> genericAcceptors) {
        acceptorOverview.setGenericAcceptors(genericAcceptors);
    }

    @Override
    public void setRemoteAcceptors(List<Acceptor> remote) {
        acceptorOverview.setRemoteAcceptors(remote);
    }

    @Override
    public void setInvmAcceptors(List<Acceptor> invm) {
        acceptorOverview.setInvmAcceptors(invm);
    }


    @Override
    public void setGenericConnectors(List<Connector> generic) {
        connectorOverview.setGenericConnectors(generic);
    }

    @Override
    public void setRemoteConnectors(List<Connector> remote) {
        connectorOverview.setRemoteConnectors(remote);
    }

    @Override
    public void setInvmConnectors(List<Connector> invm) {
        connectorOverview.setInvmConnectors(invm);
    }

    @Override
    public void setConnetorServices(List<ConnectorService> services) {
        connectorServiceList.setConnectorServices(services);
    }
}
