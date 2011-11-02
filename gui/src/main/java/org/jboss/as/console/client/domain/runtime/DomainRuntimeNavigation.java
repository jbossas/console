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
import org.jboss.ballroom.client.layout.LHSNavTree;
import org.jboss.ballroom.client.layout.LHSNavTreeItem;
import org.jboss.ballroom.client.widgets.stack.DisclosureStackPanel;

/**
 * @author Heiko Braun
 * @date 11/2/11
 */
public class DomainRuntimeNavigation {

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


        subsysTree = new LHSNavTree("domain-runtime");


        // ----------------------------------------------------
        Tree instanceTree = new LHSNavTree("domain-runtime");

        LHSNavTreeItem serverInstances= new LHSNavTreeItem(Console.CONSTANTS.common_label_serverInstances(), new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event) {
                Console.MODULES.getPlaceManager().revealPlace(
                        new PlaceRequest(NameTokens.InstancesPresenter)
                );
            }
        });

        instanceTree.addItem(serverInstances);
        DisclosurePanel instancePanel  = new DisclosureStackPanel("Server").asWidget();
        instancePanel.setContent(instanceTree);

        stack.add(instancePanel);

        // ----------------------------------------------------

        Tree statusTree = new LHSNavTree("domain-runtime");
        LHSNavTreeItem jvmItem = new LHSNavTreeItem("JVM Status", NameTokens.HostVMMetricPresenter);
        statusTree.addItem(jvmItem);


        //LHSNavTreeItem metrics = new LHSNavTreeItem("Subsystem Metrics", "metrics");

        LHSNavTreeItem datasources = new LHSNavTreeItem("Datasources", "ds-metrics");
        LHSNavTreeItem jmsQueues = new LHSNavTreeItem("JMS Destinations", "jms-metrics");
        LHSNavTreeItem web = new LHSNavTreeItem("Web", "web-metrics");
        LHSNavTreeItem tx = new LHSNavTreeItem("Transactions", "tx-metrics");

        statusTree.addItem(datasources);
        statusTree.addItem(jmsQueues);
        statusTree.addItem(web);
        statusTree.addItem(tx);


        DisclosurePanel serverPanel  = new DisclosureStackPanel("Status").asWidget();
        serverPanel.setContent(statusTree);

        stack.add(serverPanel);

        // ----------------------------------------------------

        Tree deploymentTree = new LHSNavTree("domain-runtime");
        DisclosurePanel deploymentPanel  = new DisclosureStackPanel("Deployments").asWidget();
        deploymentPanel.setContent(deploymentTree);

        deploymentTree.addItem(new LHSNavTreeItem("Manage Deployments", NameTokens.DeploymentsPresenter));

        stack.add(deploymentPanel);

        layout.add(stack);

        return layout;
    }
}
