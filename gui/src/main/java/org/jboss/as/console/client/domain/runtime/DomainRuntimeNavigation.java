package org.jboss.as.console.client.domain.runtime;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.hosts.ServerPicker;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.plugins.RuntimeExtensionMetaData;
import org.jboss.as.console.client.plugins.RuntimeExtensionRegistry;
import org.jboss.as.console.client.plugins.RuntimeGroup;
import org.jboss.as.console.client.shared.model.SubsystemRecord;
import org.jboss.as.console.client.shared.state.CurrentServerSelection;
import org.jboss.as.console.client.shared.state.ServerSelectionEvent;
import org.jboss.as.console.client.widgets.nav.Predicate;
import org.jboss.as.console.client.widgets.tree.GroupItem;
import org.jboss.ballroom.client.layout.LHSNavTree;
import org.jboss.ballroom.client.layout.LHSNavTreeItem;
import org.jboss.ballroom.client.layout.LHSTreeSection;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/2/11
 */
class DomainRuntimeNavigation implements ServerSelectionEvent.ServerSelectionListener {

    private VerticalPanel stack;
    private VerticalPanel layout;

    private ServerPicker serverPicker;

    private List<Predicate> metricPredicates = new ArrayList<Predicate>();
    private List<Predicate> runtimePredicates = new ArrayList<Predicate>();

    private ScrollPanel scroll;
    private LHSNavTree navigation;
    private LHSTreeSection metricLeaf;
    private LHSTreeSection runtimeLeaf;

    private CurrentServerSelection serverSelection;

    public DomainRuntimeNavigation(CurrentServerSelection serverSelection) {
        this.serverSelection = serverSelection;
    }

    public Widget asWidget()
    {
        layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        stack = new VerticalPanel();
        stack.setStyleName("fill-layout-width");


        // ----------------------------------------------------

        LHSNavTree domainNavigation = new LHSNavTree("domain");
        domainNavigation.getElement().setAttribute("aria-label", "Domain Tasks");

        LHSTreeSection domainLeaf = new LHSTreeSection("Domain", true);
        domainLeaf.addItem(new LHSNavTreeItem("Topology", NameTokens.Topology));
        domainLeaf.addItem(new LHSNavTreeItem("Manage Deployments", NameTokens.DeploymentsPresenter));

        domainNavigation.addItem(domainLeaf);
        domainNavigation.expandTopLevel();

        stack.add(domainNavigation);
        domainNavigation.getElement().getParentElement().setAttribute("style", "border-bottom: 1px solid #BEBEBE;");

        // ----------------------------------------------------

        serverPicker = new ServerPicker();
        stack.add(serverPicker.asWidget());

        // ----------------------------------------------------

        navigation = new LHSNavTree("domain-runtime");
        navigation.getElement().setAttribute("aria-label", "Runtime Tasks");

        // -------------

        metricLeaf = new LHSTreeSection("Server Status");
        navigation.addItem(metricLeaf);

        LHSNavTreeItem datasources = new LHSNavTreeItem("Datasources", NameTokens.DataSourceMetricPresenter);
        LHSNavTreeItem jmsQueues = new LHSNavTreeItem("JMS Destinations", NameTokens.JmsMetricPresenter);
        LHSNavTreeItem web = new LHSNavTreeItem("Web", NameTokens.WebMetricPresenter);
        LHSNavTreeItem jpa = new LHSNavTreeItem("JPA", NameTokens.JPAMetricPresenter);
        LHSNavTreeItem naming = new LHSNavTreeItem("JNDI View", NameTokens.JndiPresenter);


        metricPredicates.add(new Predicate("datasources", datasources));
        metricPredicates.add(new Predicate("messaging", jmsQueues));
        metricPredicates.add(new Predicate("web", web));
        metricPredicates.add(new Predicate("jpa", jpa));
        metricPredicates.add(new Predicate("naming", naming));


        // Extension based additions
        RuntimeExtensionRegistry registry = Console.getRuntimeLHSItemExtensionRegistry();
        List<RuntimeExtensionMetaData> menuExtensions = registry.getExtensions();
        for (RuntimeExtensionMetaData ext : menuExtensions) {

            if(RuntimeGroup.METRICS.equals(ext.getGroup()))
            {
                metricPredicates.add(
                        new Predicate(
                                ext.getKey(), new LHSNavTreeItem(ext.getName(), ext.getToken())
                        )
                );
            }
            else if(RuntimeGroup.OPERATiONS.equals(ext.getGroup()))
            {
                runtimePredicates.add(
                        new Predicate(
                                ext.getKey(), new LHSNavTreeItem(ext.getName(), ext.getToken())
                        )
                );
            }
            else
            {
                Log.warn("Invalid runtime group for extension: " + ext.getGroup());
            }
        }

        // ---

        runtimeLeaf = new LHSTreeSection("Runtime Operations");
        navigation.addItem(runtimeLeaf);

        LHSNavTreeItem osgi = new LHSNavTreeItem("OSGi", NameTokens.OSGiRuntimePresenter);
        runtimePredicates.add(new Predicate("osgi", osgi));

        // ----------------------------------------------------

        navigation.expandTopLevel();

        stack.add(navigation);

        layout.add(stack);

        scroll = new ScrollPanel(layout);

        return scroll;
    }

    public void setHosts(List<Host> hosts) {

        serverPicker.setHosts(hosts);

    }

    public void setSubsystems(List<SubsystemRecord> subsystems) {

        metricLeaf.removeItems();
        runtimeLeaf.removeItems();

        final GroupItem platformGroup = new GroupItem("Platform");

        platformGroup.addItem(new LHSNavTreeItem("JVM", NameTokens.HostVMMetricPresenter));
        platformGroup.addItem(new LHSNavTreeItem("Environment", NameTokens.EnvironmentPresenter));

        metricLeaf.addItem(platformGroup);
        //platformGroup.setState(true);

        final GroupItem subsystemGroup = new GroupItem("Subsystems");
        // match subsystems
        for(SubsystemRecord subsys : subsystems)
        {

            for(Predicate predicate : metricPredicates)
            {
                if(predicate.matches(subsys.getKey()))
                    subsystemGroup.addItem(predicate.getNavItem());
            }

            for(Predicate predicate : runtimePredicates)
            {
                if(predicate.matches(subsys.getKey()))
                    runtimeLeaf.addItem(predicate.getNavItem());
            }
        }

        final LHSNavTreeItem webservices = new LHSNavTreeItem("Webservices", NameTokens.WebServiceRuntimePresenter);
        subsystemGroup.addItem(webservices);

        metricLeaf.addItem(subsystemGroup);
        subsystemGroup.setState(true);
        platformGroup.setState(true);

        navigation.expandTopLevel();

    }

    @Override
    public void onServerSelection(String hostName, ServerInstance server, ServerSelectionEvent.Source source) {

        if(!source.equals(ServerSelectionEvent.Source.Picker))
        {
            // triggered external to this view
            serverPicker.setPreselection(hostName, server);
        }
    }
}
