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

import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
import org.jboss.as.console.client.widgets.icons.ConsoleIcons;
import org.jboss.ballroom.client.widgets.icons.Icons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Heiko Braun
 * @date 1/31/11
 */
public class DomainOverview
        extends SuspendableViewImpl implements DomainOverviewPresenter.MyView {

    private DomainOverviewPresenter presenter;
    private CellList<ProfileRecord> profileList;
    private CellList<ServerGroupRecord> groupList;

    ListDataProvider<ProfileRecord> profileProvider;
    ListDataProvider<ServerGroupRecord> groupProvider;
    private TabPanel container;

    //private CellTable<DeploymentRecord> deploymentTable;

    private static String[] colors = {
            "#A0C55F", "#C2CBCE", "#81A8B8",
            "#E8F3F8", "#AAB3AB", "#E8CAAF", "#91A398", "#ED834E",
            "#EBCC6E", "#F06B50", "#E4D829",

    };

    private static TreeMap<String, String> group2Color = new TreeMap<String,String>();

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

    public void updateProfiles(List<ProfileRecord> profiles)
    {
        profileProvider.setList(profiles);
    }

    public void updateGroups(List<ServerGroupRecord> groups)
    {
        groupProvider.setList(groups);
    }


    private SafeHtmlBuilder createContainerTable() {
        SafeHtmlBuilder containerTable= new SafeHtmlBuilder();

        containerTable.appendHtmlConstant("<table cellpadding='5' width='100%' id='host-overview'>");
        containerTable.appendHtmlConstant("<tr id='hosts-row' valign='top'>");
        containerTable.appendHtmlConstant("</tr>");
        containerTable.appendHtmlConstant("</table>");

        return containerTable;
    }

    public void updateHosts(List<HostInfo> hosts) {

        System.out.println("Hosts: "+hosts.size());

        // clear view
        container.clear();
        group2Color.clear();


        // the known server groups and their colors
        List<String> groups = deriveGroups(hosts);

        int itemsPerPage = 5;
        int lastPageSize = hosts.size()%itemsPerPage;
        int numberOfPages = ((hosts.size()-lastPageSize) / itemsPerPage)+1;

        for(int page=0; page<numberOfPages; page++)
        {

            // generate wrapper table
            SafeHtmlBuilder containerTable = createContainerTable();
            HTMLPanel htmlPanel = new HTMLPanel(containerTable.toSafeHtml());

            // host data
            List<SafeHtmlBuilder> hostColumns = new ArrayList<SafeHtmlBuilder>(hosts.size());

            for(int item=0; item<itemsPerPage; item++)
            {

                int index = item+(itemsPerPage*page);
                if(index>=hosts.size()) break;

                HostInfo host = hosts.get(index);

                SafeHtmlBuilder html = new SafeHtmlBuilder();
                String id = "h_" + host.getName();

                // host
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

                    String color = pickColor(groups, server);
                    ImageResource status = server.isRunning() ? Icons.INSTANCE.status_good() : Icons.INSTANCE.status_bad();
                    String statusImgUrl = new Image(status).getUrl();

                    html.appendHtmlConstant("<tr>");
                    html.appendHtmlConstant("<td class='domain-serverinfo domain-servercontainer' style='background:" + color + "'>");

                    // server
                    html.appendEscaped("Server: "+server.getName()).appendHtmlConstant("&nbsp;");
                    html.appendHtmlConstant("<img src='"+statusImgUrl+"' width=16 height=16 align=right>");
                    html.appendHtmlConstant("<br/>");
                    html.appendEscaped("Group: "+server.getGroup()).appendHtmlConstant("<br/>");

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

            // created one page
            String pageName = "Page " + (page + 1);
            System.out.println(pageName + " > "+hostColumns.size());
            container.add(htmlPanel, pageName);

        }

        container.selectTab(0);

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

        //deploymentTable.setRowData(0, deploymentRecords);
    }
}
