package org.jboss.as.console.client.domain.runtime;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.hosts.ServerPicker;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.widgets.nav.DefaultTreeItem;
import org.jboss.ballroom.client.layout.LHSNavTree;
import org.jboss.ballroom.client.layout.LHSNavTreeItem;
import org.jboss.ballroom.client.widgets.stack.DisclosureStackPanel;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/2/11
 */
class DomainRuntimeNavigation {

    private VerticalPanel stack;
    private VerticalPanel layout;

    private ServerPicker serverPicker;

    public Widget asWidget()
    {
        layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        stack = new VerticalPanel();
        stack.setStyleName("fill-layout-width");


        // ----------------------------------------------------


        VerticalPanel innerlayout = new VerticalPanel();
        innerlayout.setStyleName("fill-layout-width");

        serverPicker = new ServerPicker();
        innerlayout.add(serverPicker.asWidget());

        Tree statusTree = new LHSNavTree("domain-runtime");
        TreeItem servers = new DefaultTreeItem("Domain Status");
        statusTree.addItem(servers);

        LHSNavTreeItem serverInstances= new LHSNavTreeItem(Console.CONSTANTS.common_label_serverInstances(), new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event) {
                Console.MODULES.getPlaceManager().revealPlace(
                        new PlaceRequest(NameTokens.InstancesPresenter)
                );
            }
        });


        LHSNavTreeItem jvm = new LHSNavTreeItem("JVM Status", NameTokens.HostVMMetricPresenter);

        servers.addItem(serverInstances);
        servers.addItem(jvm);

        innerlayout.add(statusTree);


        // -------------

        Tree metricTree = new LHSNavTree("domain-runtime");

        TreeItem subsystems = new DefaultTreeItem("Subsystem Metrics");

        LHSNavTreeItem datasources = new LHSNavTreeItem("Datasources", "ds-metrics");
        LHSNavTreeItem jmsQueues = new LHSNavTreeItem("JMS Destinations", "jms-metrics");
        LHSNavTreeItem web = new LHSNavTreeItem("Web", "web-metrics");
        LHSNavTreeItem tx = new LHSNavTreeItem("Transactions", "tx-metrics");

        subsystems.addItem(datasources);
        subsystems.addItem(jmsQueues);
        subsystems.addItem(web);
        subsystems.addItem(tx);

        metricTree.addItem(subsystems);
        subsystems.setState(true); // open by default
        servers.setState(true);

        innerlayout.add(metricTree);


        DisclosurePanel statusPanel  = new DisclosureStackPanel("Status", true).asWidget();
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
        serverPicker.setHosts(hosts);

    }

    public void setServer(List<ServerInstance> server) {

        serverPicker.setServers(server);
    }
}
