package org.jboss.as.console.client.standalone.runtime;

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import org.jboss.as.console.client.core.NameTokens;
import org.jboss.ballroom.client.layout.LHSNavTree;
import org.jboss.ballroom.client.layout.LHSNavTreeItem;
import org.jboss.ballroom.client.widgets.stack.DisclosureStackPanel;

/**
 * @author Heiko Braun
 * @date 11/2/11
 */
public class StandaloneRuntimeNavigation {

    private VerticalPanel stack;
    private VerticalPanel layout;
    private LHSNavTree subsysTree;

    public Widget asWidget()
    {
        layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        stack = new VerticalPanel();
        stack.setStyleName("fill-layout-width");

        // ----------------------------------------------------


        subsysTree = new LHSNavTree("standalone-runtime");


        // ----------------------------------------------------

        Tree statusTree = new LHSNavTree("standalone-runtime");
        LHSNavTreeItem jvmItem = new LHSNavTreeItem("JVM Status", NameTokens.VirtualMachine);
        statusTree.addItem(jvmItem);


        //LHSNavTreeItem metrics = new LHSNavTreeItem("Subsystem Metrics", "metrics");

        LHSNavTreeItem datasources = new LHSNavTreeItem("Datasources", "ds-metrics");
        LHSNavTreeItem jmsQueues = new LHSNavTreeItem("JMS Destinations", "jms-metrics");
        LHSNavTreeItem web = new LHSNavTreeItem("Web", "web-metrics");
        LHSNavTreeItem tx = new LHSNavTreeItem("Transactions", NameTokens.TXMetrics);
        LHSNavTreeItem osgi = new LHSNavTreeItem("OSGi", NameTokens.OSGiRuntimePresenter);

        statusTree.addItem(datasources);
        statusTree.addItem(jmsQueues);
        statusTree.addItem(web);
        statusTree.addItem(tx);
        statusTree.addItem(osgi);

        DisclosurePanel serverPanel  = new DisclosureStackPanel("Status").asWidget();
        serverPanel.setContent(statusTree);

        stack.add(serverPanel);

        // ----------------------------------------------------

        Tree deploymentTree = new LHSNavTree("standalone-runtime");
        DisclosurePanel deploymentPanel  = new DisclosureStackPanel("Deployments").asWidget();
        deploymentPanel.setContent(deploymentTree);

        deploymentTree.addItem(new LHSNavTreeItem("Manage Deployments", NameTokens.DeploymentListPresenter));

        stack.add(deploymentPanel);

        layout.add(stack);

        return layout;
    }
}
