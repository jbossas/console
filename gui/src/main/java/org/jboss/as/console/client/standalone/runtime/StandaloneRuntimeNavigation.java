package org.jboss.as.console.client.standalone.runtime;

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.widgets.nav.Predicate;
import org.jboss.as.console.client.shared.model.SubsystemRecord;
import org.jboss.as.console.client.widgets.nav.DefaultTreeItem;
import org.jboss.ballroom.client.layout.LHSNavTree;
import org.jboss.ballroom.client.layout.LHSNavTreeItem;
import org.jboss.ballroom.client.widgets.stack.DisclosureStackPanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 11/2/11
 */
public class StandaloneRuntimeNavigation {

    private VerticalPanel stack;
    private VerticalPanel layout;
    private LHSNavTree subsysTree;
    private List<SubsystemRecord> subsystems;


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

        subsysTree = new LHSNavTree("standalone-runtime");


        // ----------------------------------------------------

        LHSNavTree statusTree = new LHSNavTree("standalone-runtime");
        TreeItem serverContents = new DefaultTreeItem("Server");
        statusTree.addItem(serverContents);

        innerlayout.add(statusTree);


        LHSNavTreeItem server = new LHSNavTreeItem("Configuration", NameTokens.StandaloneServerPresenter);
        LHSNavTreeItem jvmItem = new LHSNavTreeItem("JVM", NameTokens.VirtualMachine);
        serverContents.addItem(server);
        serverContents.addItem(jvmItem);


        // -------------

        Tree metricTree = new LHSNavTree("standalone-runtime");
        subsystemMetrics = new DefaultTreeItem("Subsystem Metrics");
        metricTree.addItem(subsystemMetrics);

        LHSNavTreeItem datasources = new LHSNavTreeItem("Datasources", "ds-metrics");
        LHSNavTreeItem jmsQueues = new LHSNavTreeItem("JMS Destinations", "jms-metrics");
        LHSNavTreeItem web = new LHSNavTreeItem("Web", "web-metrics");
        LHSNavTreeItem tx = new LHSNavTreeItem("Transactions", "tx-metrics");
        LHSNavTreeItem jpa = new LHSNavTreeItem("JPA", NameTokens.JPAMetricPresenter);

        metricPredicates.add(new Predicate("datasources", datasources));
        metricPredicates.add(new Predicate("messaging", jmsQueues));
        metricPredicates.add(new Predicate("web", web));
        metricPredicates.add(new Predicate("transactions", tx));
        metricPredicates.add(new Predicate("jpa", jpa));

        innerlayout.add(metricTree);

        // ---

        Tree runtimeTree = new LHSNavTree("standalone-runtime");
        subsystemRuntime = new DefaultTreeItem("Runtime Operations");
        runtimeTree.addItem(subsystemRuntime);

        LHSNavTreeItem osgi = new LHSNavTreeItem("OSGi", NameTokens.OSGiRuntimePresenter);

        runtimePredicates.add(new Predicate("osgi", osgi));

        innerlayout.add(runtimeTree);

        // ---


        DisclosurePanel serverPanel  = new DisclosureStackPanel("Runtime", true).asWidget();
        serverPanel.setContent(innerlayout);

        // open by default
        serverContents.setState(true);


        stack.add(serverPanel);

        // ----------------------------------------------------

        Tree deploymentTree = new LHSNavTree("standalone-runtime");
        DisclosurePanel deploymentPanel  = new DisclosureStackPanel("Deployments").asWidget();
        deploymentPanel.setContent(deploymentTree);

        deploymentTree.addItem(new LHSNavTreeItem("Manage Deployments", NameTokens.DeploymentListPresenter));

        stack.add(deploymentPanel);

        // ---

        layout.add(stack);

        return layout;
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
