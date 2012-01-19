package org.jboss.as.console.client.domain.runtime;

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.hosts.ServerPicker;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.shared.model.SubsystemRecord;
import org.jboss.as.console.client.widgets.nav.DefaultTreeItem;
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

    private ServerPicker serverPicker;
    private DefaultTreeItem subsystemTree;

    private List<Predicate> potentialItems = new ArrayList<Predicate>();
    private LHSNavTree runtimeOpsTree;

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

        LHSNavTreeItem serverInstances=
                new LHSNavTreeItem(Console.CONSTANTS.common_label_serverInstances(), NameTokens.InstancesPresenter);


        LHSNavTreeItem jvm = new LHSNavTreeItem("JVM Status", NameTokens.HostVMMetricPresenter);

        servers.addItem(serverInstances);
        servers.addItem(jvm);

        innerlayout.add(statusTree);


        // -------------

        Tree metricTree = new LHSNavTree("domain-runtime");

        subsystemTree = new DefaultTreeItem("Subsystem Metrics");


        LHSNavTreeItem datasources = new LHSNavTreeItem("Datasources", "ds-metrics");
        LHSNavTreeItem jmsQueues = new LHSNavTreeItem("JMS Destinations", "jms-metrics");
        LHSNavTreeItem web = new LHSNavTreeItem("Web", "web-metrics");
        LHSNavTreeItem tx = new LHSNavTreeItem("Transactions", "tx-metrics");

        potentialItems.add(new Predicate("datasources", datasources));
        potentialItems.add(new Predicate("messaging", jmsQueues));
        potentialItems.add(new Predicate("web", web));
        potentialItems.add(new Predicate("transactions", tx));

        metricTree.addItem(subsystemTree);
        //subsystemTree.setState(true); // open by default
        servers.setState(true);

        innerlayout.add(metricTree);


        DisclosurePanel statusPanel  = new DisclosureStackPanel("Runtime", true).asWidget();
        statusPanel.setContent(innerlayout);

        stack.add(statusPanel);

        // ----------------------------------------------------

        Tree deploymentTree = new LHSNavTree("domain-runtime");
        DisclosurePanel deploymentPanel  = new DisclosureStackPanel("Deployments").asWidget();
        deploymentPanel.setContent(deploymentTree);

        deploymentTree.addItem(new LHSNavTreeItem("Manage Deployments", NameTokens.DeploymentsPresenter));

        stack.add(deploymentPanel);

        layout.add(stack);


        // ----


        runtimeOpsTree = new LHSNavTree("standalone-runtime");
        DisclosurePanel runtimePanel  = new DisclosureStackPanel("Runtime Operations").asWidget();
        runtimePanel.setContent(runtimeOpsTree);

        LHSNavTreeItem osgi = new LHSNavTreeItem("OSGi", NameTokens.OSGiRuntimePresenter);
        runtimeOpsTree.addItem(osgi);

        stack.add(runtimePanel);

        return layout;
    }

    public void setHosts(List<Host> hosts) {

        serverPicker.setHosts(hosts);

    }

    public void setServer(List<ServerInstance> server) {

        serverPicker.setServers(server);
    }

    public void setSubsystems(List<SubsystemRecord> subsystems) {


        subsystemTree.setState(false);
        subsystemTree.removeItems();

        // match subystems
        for(SubsystemRecord subsys : subsystems)
        {
            for(Predicate predicate : potentialItems)
            {
                if(predicate.matches(subsys.getKey()))
                    subsystemTree.addItem(predicate.getNavItem());
            }
        }

        subsystemTree.setState(true);
    }

    final class Predicate {
        private String subsysName;
        private LHSNavTreeItem navItem;

        Predicate(String subsysName, LHSNavTreeItem navItem) {
            this.subsysName = subsysName;
            this.navItem = navItem;
        }

        public boolean matches(String current) {
                return current.equals(subsysName);
        }

        public LHSNavTreeItem getNavItem() {
            return navItem;
        }
    }

}
