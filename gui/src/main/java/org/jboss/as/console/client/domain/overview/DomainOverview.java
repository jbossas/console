/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jboss.as.console.client.domain.overview;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
import org.jboss.as.console.client.widgets.icons.ConsoleIcons;
import org.jboss.ballroom.client.widgets.InlineLink;
import org.jboss.ballroom.client.widgets.icons.Icons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Heiko Braun
 * @date 1/31/11
 */
public class DomainOverview
        extends SuspendableViewImpl implements DomainOverviewPresenter.MyView {

    private DomainOverviewPresenter presenter;
    private TabPanel container;

    //private CellTable<DeploymentRecord> deploymentTable;

    private static String[] colors = {
            "#A0C55F", "#C2CBCE", "#81A8B8",
            "#E8F3F8", "#AAB3AB", "#E8CAAF", "#91A398", "#ED834E",
            "#EBCC6E", "#F06B50", "#E4D829",

    };

    private static TreeMap<String, String> group2Color = new TreeMap<String,String>();
    private ServerPanelReference prevSelection = null;
    private List<HTMLPanel> hostPanels = new ArrayList<HTMLPanel>();

    @Override
    public void setPresenter(DomainOverviewPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        SimpleLayout layout = new SimpleLayout()
                .setTitle("Domain Overview")
                .setHeadline("Host & Servers")
                .setDescription("");

        container = new TabPanel();
        container.setStyleName("default-tabpanel");
        layout.addContent("Hosts", container);
        return layout.build();
    }

    private SafeHtmlBuilder createContainerTable() {
        SafeHtmlBuilder containerTable= new SafeHtmlBuilder();

        containerTable.appendHtmlConstant("<table cellpadding='5' width='100%' id='host-overview'>");
        containerTable.appendHtmlConstant("<tr id='hosts-row' valign='top'>");
        containerTable.appendHtmlConstant("</tr>");
        containerTable.appendHtmlConstant("</table>");

        return containerTable;
    }

    public void updateHosts(List<HostInfo> hosts, final ServerPanelReference preselectedServer) {

        // clear view
        container.clear();
        group2Color.clear();
        prevSelection = null;
        hostPanels.clear();

        // the known server groups and their colors
        List<String> groups = deriveGroups(hosts);

        int itemsPerPage = 5;
        int lastPageSize = hosts.size()%itemsPerPage;
        int numberOfPages = ((hosts.size()-lastPageSize) / itemsPerPage)+1;

        for(int page=0; page<numberOfPages; page++)
        {

            Map<String, ServerPanelReference> pendingTools = new HashMap<String,ServerPanelReference>();

            // generate wrapper table
            SafeHtmlBuilder containerTable = createContainerTable();
            final HTMLPanel htmlPanel = new HTMLPanel(containerTable.toSafeHtml());

            // host data
            List<SafeHtmlBuilder> hostColumns = new ArrayList<SafeHtmlBuilder>(hosts.size());

            for(int item=0; item<itemsPerPage; item++)
            {

                int index = item+(itemsPerPage*page);
                if(index>=hosts.size()) break;

                HostInfo host = hosts.get(index);

                SafeHtmlBuilder html = new SafeHtmlBuilder();
                String id = "h_" + host.getName();

                // ----------------------
                // host
                // ----------------------

                String domainType = host.isController ? "Controller" : "Member";
                String ctrlCss = host.isController ? "domain-controller" : "domain-member";


                html.appendHtmlConstant("<td class='domain-hostcontainer' id='" + id + "'>")
                        .appendHtmlConstant("<div class='domain-hostinfo " + ctrlCss + "'>")
                        .appendEscaped("Host: " + host.getName()).appendHtmlConstant("<br/>")
                        .appendEscaped("Domain: " + domainType).appendHtmlConstant("&nbsp;");

                if(host.isController())
                {
                    ImageResource star = ConsoleIcons.INSTANCE.star();
                    String imgUrl = new Image(star).getUrl();
                    html.appendHtmlConstant("<img src='"+imgUrl+"' width=16 height=16 align=right>");
                }

                html.appendHtmlConstant("<br/>");
                html.appendHtmlConstant("</div>");

                html.appendHtmlConstant("<table width='100%'>");
                for(ServerInstance server : host.getServerInstances())
                {

                    // ----------------------
                    // server
                    // ----------------------

                    String color = pickColor(groups, server);
                    ImageResource status = server.isRunning() ? Icons.INSTANCE.status_good() : Icons.INSTANCE.status_bad();
                    String statusImgUrl = new Image(status).getUrl();

                    html.appendHtmlConstant("<tr>");

                    String serverPanelId = "sp_"+host.getName()+"_"+server.getName();
                    html.appendHtmlConstant("<td id='"+serverPanelId+"' class='domain-serverinfo domain-servercontainer' style='background:" + color + "'>");

                    html.appendEscaped("Server: "+server.getName()).appendHtmlConstant("&nbsp;");
                    html.appendHtmlConstant("<img src='" + statusImgUrl + "' width=16 height=16 align=right>");
                    html.appendHtmlConstant("<br/>");
                    html.appendEscaped("Group: "+server.getGroup()).appendHtmlConstant("<br/>");

                    // toolbox layout

                    String toolboxId = "tb_"+host.getName()+"_"+server.getName();
                    html.appendHtmlConstant("<div id='"+toolboxId+"' class='server-tools'/>");
                    pendingTools.put(toolboxId, new ServerPanelReference(host.getName(), server, toolboxId, serverPanelId));


                    // try to match the preselection
                    if(preselectedServer!=null
                            && preselectedServer.getHostName().equals(host.getName())
                            && preselectedServer.getServer().getName().equals(server.getName()))
                    {
                        // matched, update dom element references
                        preselectedServer.updateDomReferences(serverPanelId, toolboxId);
                    }

                    html.appendHtmlConstant("</td>");
                    html.appendHtmlConstant("</tr>");

                }
                html.appendHtmlConstant("</table>");

                html.appendHtmlConstant("</td>"); // end host

                hostColumns.add(html);

            }


            if(hostColumns.isEmpty())  // skip empty pages
                break;

            // update the tr table
            Element tr = htmlPanel.getElementById("hosts-row");
            for(SafeHtmlBuilder builder : hostColumns)
            {
                Element td = DOM.createTD();
                td.setClassName("domain-hostcontainer");
                td.setInnerHTML(builder.toSafeHtml().asString());
                tr.appendChild(td);
            }

            // ----------------------
            // bind tools
            // ----------------------

            for(String elementId : pendingTools.keySet())
            {
                final VerticalPanel tools = new VerticalPanel();
                tools.setStyleName("fill-layout");

                final ServerPanelReference serverPanelReference = pendingTools.get(elementId);


                // -------------------------------------
                // process click events on server panel
                // -------------------------------------

                Element serverPanelElement = htmlPanel.getElementById(serverPanelReference.serverPanelId);
                DOM.setEventListener(serverPanelElement, new EventListener() {
                    @Override
                    public void onBrowserEvent(Event event) {
                        if(event.getTypeInt()==Event.ONCLICK)
                        {

                            if(prevSelection!=null)
                            {
                                deactivate(htmlPanel, prevSelection);
                            }

                            activate(htmlPanel, serverPanelReference);

                        }
                    }
                });

                DOM.sinkEvents(serverPanelElement, Event.ONCLICK);

                VerticalPanel toolContent = new VerticalPanel();
                toolContent.addStyleName("fill-layout");

                InlineLink startStop = new InlineLink("Start/Stop Server<br/>");
                startStop.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        presenter.onStartStopServer(serverPanelReference.getHostName(), serverPanelReference.getServer());
                    }
                });


                InlineLink startGroup = new InlineLink("Start Group<br/>");
                startGroup.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        presenter.onStartStopGroup(
                                serverPanelReference.getHostName(),
                                serverPanelReference.getServer().getGroup(),
                                true
                        );
                    }
                });

                InlineLink stopGroup = new InlineLink("Stop Group<br/>");
                stopGroup.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        presenter.onStartStopGroup(
                                serverPanelReference.getHostName(),
                                serverPanelReference.getServer().getGroup(),
                                false
                        );
                    }
                });

                toolContent.add(startStop);
                toolContent.add(startGroup);
                toolContent.add(stopGroup);

                tools.add(toolContent);

                htmlPanel.add(tools, elementId);
            }

            // created one page
            String pageName = "Page " + (page + 1);
            container.add(htmlPanel, pageName);

            // for matching against preselection
            hostPanels.add(htmlPanel);


        }

        container.selectTab(0);

        // toggle preselection

        if(preselectedServer!=null && preselectedServer.hasDomReferences())
        {
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    activate(preselectedServer);
                }
            });
        }

    }

    private void activate(ServerPanelReference preselectedServer) {
        for(HTMLPanel panel : hostPanels)
        {
            Element serverPanel = panel.getElementById(preselectedServer.getServerPanelId());
            if(serverPanel!=null) // exists
            {
                activate(panel, preselectedServer);
                break;
            }
        }
    }

    private void deactivate(HTMLPanel htmlPanel, ServerPanelReference prevSelection) {
        Element serverPanel = htmlPanel.getElementById(prevSelection.serverPanelId);
        assert serverPanel!=null : "server panel cannot be found "+prevSelection.serverPanelId;

        serverPanel.removeClassName("domain-serverinfo-active");
        htmlPanel.getElementById(prevSelection.getToolBoxId()).removeClassName("is-visible");
    }

    private void activate(HTMLPanel htmlPanel, ServerPanelReference currentSelection) {


        htmlPanel.getElementById(currentSelection.serverPanelId).addClassName("domain-serverinfo-active");
        htmlPanel.getElementById(currentSelection.getToolBoxId()).addClassName("is-visible");

        prevSelection = currentSelection;

        presenter.onSelectServer(currentSelection);
    }


    private List<String> deriveGroups(List<HostInfo> hosts) {

        List<String> groups = new LinkedList<String>();

        for(HostInfo host : hosts)
        {
            List<ServerInstance> serverInstances = host.getServerInstances();
            for(ServerInstance server : serverInstances)
            {
                if(!groups.contains(server.getGroup()))
                    groups.add(server.getGroup());
            }
        }

        Collections.sort(groups);
        return groups;
    }

    private static String pickColor(List<String> groups, ServerInstance server) {

        String color = null;
        if(group2Color.containsKey(server.getGroup()))
        {
            color = group2Color.get(server.getGroup());
        }
        else
        {
            int index = 0;
            for(String group : groups)
            {

                if(group.equals(server.getGroup()))
                {
                    break;
                }
                index++;
            }

            if(index>colors.length-1)
                color = "#ffffff"; // fallback if number of groups too large
            else
                color = colors[index];

            group2Color.put(server.getGroup(), color);
        }

        return color;
    }

    public void updateDeployments(List<DeploymentRecord> deploymentRecords) {


    }
}
