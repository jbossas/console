package org.jboss.as.console.client.standalone.runtime;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.shared.model.SubsystemRecord;
import org.jboss.as.console.client.widgets.nav.Predicate;
import org.jboss.ballroom.client.layout.LHSNavTree;
import org.jboss.ballroom.client.layout.LHSNavTreeItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/2/11
 */
public class StandaloneRuntimeNavigation {

    private VerticalPanel stack;
    private VerticalPanel layout;

    private List<SubsystemRecord> subsystems;

    private List<Predicate> metricPredicates = new ArrayList<Predicate>();
    private List<Predicate> runtimePredicates = new ArrayList<Predicate>();

    private ScrollPanel scroll;
    private TreeItem metricLeaf;
    private TreeItem runtimeLeaf;
    private LHSNavTree navigation;

    public Widget asWidget()
    {
        layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        stack = new VerticalPanel();
        stack.setStyleName("fill-layout-width");

        // ----------------------------------------------------


        navigation = new LHSNavTree("standalone-runtime");
        navigation.getElement().setAttribute("aria-label", "Runtime Tasks");

        // ----------------------------------------------------

        TreeItem serverLeaf = new TreeSection("Server Status", true);
        serverLeaf.setState(true);

        LHSNavTreeItem server = new LHSNavTreeItem("Configuration", NameTokens.StandaloneServerPresenter);
        LHSNavTreeItem jvmItem = new LHSNavTreeItem("JVM", NameTokens.VirtualMachine);
        serverLeaf.addItem(server);
        serverLeaf.addItem(jvmItem);

        navigation.addItem(serverLeaf);


        // -------------

        metricLeaf = new TreeSection("Subsystem Metrics");


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

        navigation.addItem(metricLeaf);


        // ---

        runtimeLeaf = new TreeSection("Runtime Operations");


        LHSNavTreeItem osgi = new LHSNavTreeItem("OSGi", NameTokens.OSGiRuntimePresenter);

        runtimePredicates.add(new Predicate("osgi", osgi));

        navigation.addItem(runtimeLeaf);

        // ----------------------------------------------------

        TreeItem deploymentLeaf = new TreeSection("Deployments");

        deploymentLeaf.addItem(new LHSNavTreeItem("Manage Deployments", NameTokens.DeploymentListPresenter));
        deploymentLeaf.addItem(new LHSNavTreeItem("Webservices", NameTokens.WebServiceRuntimePresenter));

        navigation.addItem(deploymentLeaf);

        // ---
        stack.add(navigation);
        layout.add(stack);

        scroll = new ScrollPanel(layout);

        expandTopLevel();

        return scroll;
    }

    private void expandTopLevel() {
        for(int i=0; i<navigation.getItemCount(); i++)
        {
            TreeItem item = navigation.getItem(i);
            System.out.println(item.getText());
            item.setState(true);
        }
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

        expandTopLevel();

    }

    public class TreeSection extends TreeItem {


        TreeSection(String title) {
            setText(title);
            addStyleName("tree-section");
        }

        TreeSection(String title, boolean first) {
            setText(title);
            addStyleName("tree-section");
            if(first)
                addStyleName("tree-section-first");
        }

        @Override
        public void setSelected(boolean selected) {
            super.setSelected(selected);
            if(selected)
                addStyleName("tree-section-selected");
            else
                removeStyleName("tree-section-selected");
        }

    }
}
