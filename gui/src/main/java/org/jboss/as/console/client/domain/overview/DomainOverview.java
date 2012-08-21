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

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;

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
    private HTMLPanel htmlPanel;

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

        SafeHtmlBuilder html= new SafeHtmlBuilder();

        html.appendHtmlConstant("<table style='border:1px solid #cccccc' cellpadding='5' width='100%' id='host-overview'>");
        html.appendHtmlConstant("<tr id='hosts-row'>");
        html.appendHtmlConstant("</tr>");
        html.appendHtmlConstant("</table>");

        htmlPanel = new HTMLPanel(html.toSafeHtml());
        layout.addContent("Hosts", htmlPanel);
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

    public void updateHosts(List<HostInfo> hosts) {

        // the known server groups and their colors
        group2Color.clear();
        List<String> groups = deriveGroups(hosts);


        for(HostInfo host : hosts)
        {
            SafeHtmlBuilder html = new SafeHtmlBuilder();
            String id = "h_" + host.getName();

            String ctrl = host.isController ? " * " : "";

            // host
            html.appendHtmlConstant("<td id='" + id + "'>")
                    .appendHtmlConstant("<h3>")
                    .appendEscaped("Host: " + host.getName())
                    .appendEscaped(ctrl)
                    .appendHtmlConstant("</h3>");


            html.appendHtmlConstant("<table width='100%'>");
            for(ServerInstance server : host.getServerInstances())
            {

                String color = pickColor(groups, server);

                html.appendHtmlConstant("<tr>");
                html.appendHtmlConstant("<td style='background:"+color+"'>");

                // server
                html.appendEscaped("Server: "+server.getName()).appendHtmlConstant("<br/>");
                html.appendEscaped("Group: "+server.getGroup()).appendHtmlConstant("<br/>");
                html.appendEscaped("Active: "+server.isRunning());

                html.appendHtmlConstant("</td>");
                html.appendHtmlConstant("</tr>");

                // blank
                html.appendHtmlConstant("<tr><td>&nbsp;</td></tr>");
            }
            html.appendHtmlConstant("</table>");

            html.appendHtmlConstant("</td>");

            Element td = DOM.createTD();
            td.setInnerHTML(html.toSafeHtml().asString());
            htmlPanel.getElementById("hosts-row").appendChild(td);
        }
    }

    private List<String> deriveGroups(List<HostInfo> hosts) {

        List<String> groups = new LinkedList<String>();

        for(HostInfo host : hosts)
        {
            List<ServerInstance> serverInstances = host.getServerInstances();
            for(ServerInstance server : serverInstances)
            {
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

            if(index>colors.length)
                color = "#cccccc"; // fallback if number of groups too large
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
