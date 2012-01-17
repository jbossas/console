package org.jboss.as.console.client.standalone.runtime;

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.shared.model.SubsystemRecord;
import org.jboss.as.console.client.widgets.nav.DefaultTreeItem;
import org.jboss.ballroom.client.layout.LHSNavTree;
import org.jboss.ballroom.client.layout.LHSNavTreeItem;
import org.jboss.ballroom.client.widgets.stack.DisclosureStackPanel;

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

    private Map<String, LHSNavTreeItem> mapping = new HashMap<String,LHSNavTreeItem>();
    private Tree statusTree;

    public Widget asWidget()
    {
        layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        stack = new VerticalPanel();
        stack.setStyleName("fill-layout-width");

        // ----------------------------------------------------


        subsysTree = new LHSNavTree("standalone-runtime");


        // ----------------------------------------------------

        statusTree = new LHSNavTree("standalone-runtime");
        TreeItem serverContents = new DefaultTreeItem("Server");

        statusTree.addItem(serverContents);


        LHSNavTreeItem server = new LHSNavTreeItem("Configuration", NameTokens.StandaloneServerPresenter);
        LHSNavTreeItem jvmItem = new LHSNavTreeItem("JVM", NameTokens.VirtualMachine);
        serverContents.addItem(server);
        serverContents.addItem(jvmItem);


        //LHSNavTreeItem metrics = new LHSNavTreeItem("Subsystem Metrics", "metrics");

        LHSNavTreeItem datasources = new LHSNavTreeItem("Datasources", "ds-metrics");
        LHSNavTreeItem jmsQueues = new LHSNavTreeItem("JMS Destinations", "jms-metrics");
        LHSNavTreeItem web = new LHSNavTreeItem("Web", "web-metrics");
        LHSNavTreeItem tx = new LHSNavTreeItem("Transactions", NameTokens.TXMetrics);

        /*subsysContents.addItem(datasources);
        subsysContents.addItem(jmsQueues);
        subsysContents.addItem(web);
        subsysContents.addItem(tx);*/


        mapping.put(NameTokens.DataSourcePresenter, datasources);
        mapping.put(NameTokens.MessagingPresenter, jmsQueues);
        mapping.put(NameTokens.WebPresenter, web);
        mapping.put(NameTokens.TransactionPresenter, tx);


        DisclosurePanel serverPanel  = new DisclosureStackPanel("Status", true).asWidget();
        serverPanel.setContent(statusTree);

        // open by default
        serverContents.setState(true);


        stack.add(serverPanel);

        // ----------------------------------------------------

        Tree deploymentTree = new LHSNavTree("standalone-runtime");
        DisclosurePanel deploymentPanel  = new DisclosureStackPanel("Deployments").asWidget();
        deploymentPanel.setContent(deploymentTree);

        deploymentTree.addItem(new LHSNavTreeItem("Manage Deployments", NameTokens.DeploymentListPresenter));

        stack.add(deploymentPanel);


        // ----

        Tree runtimeOpsTree = new LHSNavTree("standalone-runtime");
        DisclosurePanel runtimePanel  = new DisclosureStackPanel("Runtime Operations").asWidget();
        runtimePanel.setContent(runtimeOpsTree);

        LHSNavTreeItem osgi = new LHSNavTreeItem("OSGi", NameTokens.OSGiRuntimePresenter);
        runtimeOpsTree.addItem(osgi);

        stack.add(runtimePanel);

         // ---

        layout.add(stack);

        return layout;
    }

    public void setSubsystems(List<SubsystemRecord> result) {

        TreeItem subsysContents = new DefaultTreeItem("Subsystem Metrics");

        for(SubsystemRecord subsys : result)
        {
            LHSNavTreeItem navEntry = mapping.get(subsys.getKey());
            if(navEntry!=null)
            {
                subsysContents.addItem(navEntry);
            }
        }

        statusTree.addItem(subsysContents);
        subsysContents.setState(true);
    }
}
