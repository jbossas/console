package org.jboss.as.console.client.domain.runtime;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.hosts.HostSelector;
import org.jboss.as.console.client.domain.hosts.ServerSelector;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.ballroom.client.layout.LHSNavTree;
import org.jboss.ballroom.client.layout.LHSNavTreeItem;
import org.jboss.ballroom.client.widgets.stack.DisclosureStackPanel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/2/11
 */
class DomainRuntimeNavigation {

    private VerticalPanel stack;
    private VerticalPanel layout;

    private HostSelector hostSelector;
    private ServerSelector serverSelector;

    public Widget asWidget()
    {
        layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        stack = new VerticalPanel();
        stack.setStyleName("fill-layout-width");


        // ----------------------------------------------------


        VerticalPanel innerlayout = new VerticalPanel();
        innerlayout.setStyleName("fill-layout-width");

        hostSelector = new HostSelector();
        innerlayout.add(hostSelector.asWidget());

        Tree statusTree = new LHSNavTree("domain-runtime");

        LHSNavTreeItem serverInstances= new LHSNavTreeItem(Console.CONSTANTS.common_label_serverInstances(), new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event) {
                Console.MODULES.getPlaceManager().revealPlace(
                        new PlaceRequest(NameTokens.InstancesPresenter)
                );
            }
        });


        statusTree.addItem(serverInstances);
        innerlayout.add(statusTree);


        // -------------

        serverSelector= new ServerSelector();
        Widget serverSelectorWidget = serverSelector.asWidget();
        serverSelectorWidget.getElement().setAttribute("style", "padding-top:10px; padding-left:4px;padding-right:4px");
        innerlayout.add(serverSelectorWidget);

        Tree metricTree = new LHSNavTree("domain-runtime");

        LHSNavTreeItem datasources = new LHSNavTreeItem("Datasources", "ds-metrics");
        LHSNavTreeItem jmsQueues = new LHSNavTreeItem("JMS Destinations", "jms-metrics");
        LHSNavTreeItem web = new LHSNavTreeItem("Web", "web-metrics");
        LHSNavTreeItem tx = new LHSNavTreeItem("Transactions", "tx-metrics");

        metricTree.addItem(datasources);
        metricTree.addItem(jmsQueues);
        metricTree.addItem(web);
        metricTree.addItem(tx);

        innerlayout.add(metricTree);


        DisclosurePanel statusPanel  = new DisclosureStackPanel("Status").asWidget();
        statusPanel.setContent(innerlayout);

        stack.add(statusPanel);

        // ----------------------------------------------------

        Tree deploymentTree = new LHSNavTree("domain-runtime");
        DisclosurePanel deploymentPanel  = new DisclosureStackPanel("Deployments").asWidget();
        deploymentPanel.setContent(deploymentTree);

        deploymentTree.addItem(new LHSNavTreeItem("Manage Deployments", NameTokens.DeploymentsPresenter));

        stack.add(deploymentPanel);

        layout.add(stack);

        return layout;
    }

    public void setHosts(List<Host> hosts) {
        List<String> hostNames = new ArrayList<String>(hosts.size());
        for(Host h : hosts)
        {
            hostNames.add(h.getName());
        }

        hostSelector.setHosts(hostNames);

    }

     public void setServer(List<Server> server) {
        List<String> serverNames = new ArrayList<String>(server.size());
        for(Server s : server)
        {
            serverNames.add(s.getName());
        }

         serverSelector.setServer(serverNames);
    }
}
