package org.jboss.as.console.client.domain.runtime;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.hosts.ServerPicker;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.shared.model.SubsystemRecord;
import org.jboss.as.console.client.widgets.nav.Predicate;
import org.jboss.ballroom.client.layout.LHSNavTree;
import org.jboss.ballroom.client.layout.LHSNavTreeItem;
import org.jboss.ballroom.client.layout.LHSTreeSection;

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

    private ScrollPanel scroll;
    private LHSNavTree navigation;
    private LHSTreeSection metricLeaf;
    private LHSTreeSection runtimeLeaf;

    public Widget asWidget()
    {
        layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        stack = new VerticalPanel();
        stack.setStyleName("fill-layout-width");


        // ----------------------------------------------------

        serverPicker = new ServerPicker();
        stack.add(serverPicker.asWidget());

        // ----------------------------------------------------

        navigation = new LHSNavTree("domain-runtime");
        navigation.getElement().setAttribute("aria-label", "Profile Tasks");

        //Tree statusTree = new LHSNavTree("domain-runtime");

        LHSTreeSection statusLeaf = new LHSTreeSection("Domain Status");
        navigation.addItem(statusLeaf);

        LHSNavTreeItem serverInstances= new LHSNavTreeItem(Console.CONSTANTS.common_label_serverInstances(), NameTokens.InstancesPresenter);
        LHSNavTreeItem jvm = new LHSNavTreeItem("JVM Status", NameTokens.HostVMMetricPresenter);

        statusLeaf.addItem(serverInstances);
        statusLeaf.addItem(jvm);

        //DisclosurePanel statusPanel  = new DisclosureStackPanel("Domain Status").asWidget();
        //statusPanel.setContent(statusTree);
        //stack.add(statusPanel);

        // -------------

        metricLeaf = new LHSTreeSection("Subsystem Metrics");
        navigation.addItem(metricLeaf);

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

        // ---

        runtimeLeaf = new LHSTreeSection("Runtime Operations");
        navigation.addItem(runtimeLeaf);

        LHSNavTreeItem osgi = new LHSNavTreeItem("OSGi", NameTokens.OSGiRuntimePresenter);
        runtimePredicates.add(new Predicate("osgi", osgi));

        // ----------------------------------------------------

        LHSTreeSection deploymentLeaf = new LHSTreeSection("Deployments");
        navigation.addItem(deploymentLeaf);

        deploymentLeaf.addItem(new LHSNavTreeItem("Manage Deployments", NameTokens.DeploymentsPresenter));
        deploymentLeaf.addItem(new LHSNavTreeItem("Webservices", NameTokens.WebServiceRuntimePresenter));

        navigation.expandTopLevel();

        stack.add(navigation);

        layout.add(stack);

        scroll = new ScrollPanel(layout);

        return scroll;
    }

    public void setHosts(List<Host> hosts) {

        serverPicker.setHosts(hosts);

    }

    public void setServer(List<ServerInstance> server) {

        serverPicker.setServers(server);
    }

    public void setSubsystems(List<SubsystemRecord> subsystems) {


        metricLeaf.removeItems();
        runtimeLeaf.removeItems();

        // match subsystems
        for(SubsystemRecord subsys : subsystems)
        {
            for(Predicate predicate : metricPredicates)
            {
                if(predicate.matches(subsys.getKey()))
                    metricLeaf.addItem(predicate.getNavItem());
            }

            for(Predicate predicate : runtimePredicates)
            {
                if(predicate.matches(subsys.getKey()))
                    runtimeLeaf.addItem(predicate.getNavItem());
            }
        }


        navigation.expandTopLevel();

    }


    public void clearSelection() {
        serverPicker.clearSelection();
    }

    public void setSelectedServer(String hostName, ServerInstance server) {
        serverPicker.setSelected(server, true);
    }
}
