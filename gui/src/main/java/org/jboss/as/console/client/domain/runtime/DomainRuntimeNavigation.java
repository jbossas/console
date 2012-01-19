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
import org.jboss.as.console.client.widgets.nav.Predicate;
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

    private List<Predicate> metricPredicates = new ArrayList<Predicate>();
    private List<Predicate> runtimePredicates = new ArrayList<Predicate>();
    private DefaultTreeItem subsystemRuntime;
    private DefaultTreeItem subsystemMetrics;

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

        servers.setState(true);

        // -------------

        Tree metricTree = new LHSNavTree("domain-runtime");
        subsystemMetrics = new DefaultTreeItem("Subsystem Metrics");
        metricTree.addItem(subsystemMetrics);

        LHSNavTreeItem datasources = new LHSNavTreeItem("Datasources", NameTokens.DataSourceMetricPresenter);
        LHSNavTreeItem jmsQueues = new LHSNavTreeItem("JMS Destinations", NameTokens.JmsMetricPresenter);
        LHSNavTreeItem web = new LHSNavTreeItem("Web", NameTokens.WebMetricPresenter);
        LHSNavTreeItem tx = new LHSNavTreeItem("Transactions", NameTokens.TXMetrics);
        LHSNavTreeItem jpa = new LHSNavTreeItem("JPA", NameTokens.JPAMetricPresenter);

        metricPredicates.add(new Predicate("datasources", datasources));
        metricPredicates.add(new Predicate("messaging", jmsQueues));
        metricPredicates.add(new Predicate("web", web));
        metricPredicates.add(new Predicate("transactions", tx));
        metricPredicates.add(new Predicate("jpa", jpa));

        innerlayout.add(metricTree);

        // ---

        Tree runtimeTree = new LHSNavTree("domain-runtime");
        subsystemRuntime = new DefaultTreeItem("Runtime Operations");
        runtimeTree.addItem(subsystemRuntime);

        LHSNavTreeItem osgi = new LHSNavTreeItem("OSGi", NameTokens.OSGiRuntimePresenter);

        runtimePredicates.add(new Predicate("osgi", osgi));

        innerlayout.add(runtimeTree);

        // ---

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



        return layout;
    }

    public void setHosts(List<Host> hosts) {

        serverPicker.setHosts(hosts);

    }

    public void setServer(List<ServerInstance> server) {

        serverPicker.setServers(server);
    }

    public void setSubsystems(List<SubsystemRecord> subsystems) {


        subsystemMetrics.setState(false);
        subsystemMetrics.removeItems();

        subsystemRuntime.setState(false);
        subsystemRuntime.removeItems();

        // match subsystems
        for(SubsystemRecord subsys : subsystems)
        {

            //System.out.println(subsys.getKey());

            for(Predicate predicate : metricPredicates)
            {
                if(predicate.matches(subsys.getKey()))
                    subsystemMetrics.addItem(predicate.getNavItem());
            }

            for(Predicate predicate : runtimePredicates)
            {
                if(predicate.matches(subsys.getKey()))
                    subsystemRuntime.addItem(predicate.getNavItem());
            }
        }

        subsystemMetrics.setVisible(subsystemMetrics.getChildCount()>0);
        subsystemRuntime.setVisible(subsystemRuntime.getChildCount()>0);

        subsystemMetrics.setState(true);
        subsystemRuntime.setState(true);


    }



}
