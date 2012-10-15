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

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
import org.jboss.as.console.client.widgets.icons.ConsoleIcons;
import org.jboss.ballroom.client.widgets.icons.Icons;

import java.util.*;

import static com.google.gwt.user.client.Event.ONMOUSEOUT;
import static com.google.gwt.user.client.Event.ONMOUSEOVER;

/**
 * @author Harald Pehl
 * @dat 10/09/12
 */
public class DomainOverview extends SuspendableViewImpl implements DomainOverviewPresenter.MyView
{
    private static final int VISIBLE_HOSTS_COLUMNS = 3;
    private static final int SERVER_GROUP_COLORS = 5; // must match the '.serverGroupX' css class names
    private static final String SERVER_GROUP_CLASS = "serverGroup";
    private static final String HIDDEN_HOSTS_ID = "hiddenHosts";
    private static final String HIDDEN_SERVERS_ID = "hiddenServers";
    private static final String VISIBLE_HOSTS_ID = "visibleHost";
    private static final String VISIBLE_SERVERS_ID = "visibleServers";
    private static final String SERVER_GROUP_START = "serverGroup";

    private DomainOverviewPresenter presenter;
    private HTMLPanel root;
    private int hostIndex = 0; // the index of the current visible host
    private int hostSize = 0;


    @Override
    public Widget createWidget()
    {
        SimpleLayout layout = new SimpleLayout()
                .setTitle("Domain Overview")
                .setHeadline("Host & Servers")
                .setDescription("");
        root = createRoot();
        layout.addContent("domain", root);
        return layout.build();
    }

    private HTMLPanel createRoot()
    {
        SafeHtmlBuilder html = new SafeHtmlBuilder();
        html.appendHtmlConstant("<table style='width:100%;'>");
        html.appendHtmlConstant("<colgroup><col width='15%'></colgroup>");
        html.appendHtmlConstant("<thead id='" + VISIBLE_HOSTS_ID + "'><tr><th " +
                "class='domainOverviewHeader'>Hosts&nbsp;&rarr;<br/>Groups&nbsp;&darr;</th></tr></thead>");
        html.appendHtmlConstant("<tfoot><tr>");
        html.appendHtmlConstant("<td>&nbsp;</td>");
        html.appendHtmlConstant("<td id='prevHost' class='hostNavigation'>&larr; Previous Host</td>");
        html.appendHtmlConstant("<td>&nbsp;</td>");
        html.appendHtmlConstant("<td id='nextHost'class='hostNavigation' style='text-align:right;'>Next Host &rarr;" +
                "</td>");
        html.appendHtmlConstant("</tr></tfoot>");
        html.appendHtmlConstant("<tbody id='" + VISIBLE_SERVERS_ID + "'/>");
        html.appendHtmlConstant("</table>");

        html.appendHtmlConstant("<table style='display:none;'><thead id='" + HIDDEN_HOSTS_ID + "'/><tbody " +
                "id='" + HIDDEN_SERVERS_ID + "'/></table>");
        return new HTMLPanel(html.toSafeHtml().asString());
    }

    @Override
    public void setPresenter(final DomainOverviewPresenter presenter)
    {
        this.presenter = presenter;
    }

    @Override
    public void updateHosts(final List<HostInfo> hosts, final ServerPanelReference preselectedServer)
    {
        // initialize
        hostIndex = 0;
        hostSize = hosts.size();

        // get groups
        SortedSet<ServerGroup> groups = deriveGroups(hosts);
        for (ServerGroup group : groups)
        {
            group.fill(hosts);
        }
        Set<HostInfo> orderedHosts = groups.first().getHosts();

        // fill the hidden table
        // first row: hosts
        SafeHtmlBuilder hiddenHostsHtml = new SafeHtmlBuilder();
        hiddenHostsHtml.appendHtmlConstant("<tr>");
        for (HostInfo host : orderedHosts)
        {
            hiddenHostsHtml.appendHtmlConstant("<th class='domainOverviewHeader'><span>Host: ")
                    .appendEscaped(host.getName()).appendHtmlConstant("<br/>Domain: ");
            hiddenHostsHtml.appendHtmlConstant(host.isController() ? "Controller" : "Member");
            hiddenHostsHtml.appendHtmlConstant("</span>");
            if (host.isController())
            {
                ImageResource star = ConsoleIcons.INSTANCE.star();
                hiddenHostsHtml.appendHtmlConstant("<span style='float:right;'><img src='" + new Image(star).getUrl()
                        + "' width='16' " + "height='16'/></span>");
            }
        }
        hiddenHostsHtml.appendHtmlConstant("</tr>");
        com.google.gwt.user.client.Element hiddenHead = root.getElementById(HIDDEN_HOSTS_ID);
        hiddenHead.setInnerHTML(hiddenHostsHtml.toSafeHtml().asString());

        // remaining rows: servers
        SafeHtmlBuilder hiddenServersHtml = new SafeHtmlBuilder();
        for (ServerGroup group : groups)
        {
            for (int serverIndex = 0; serverIndex < group.maxServersPerHost; serverIndex++)
            {
                hiddenServersHtml.appendHtmlConstant("<tr>");
                for (HostInfo host : group.getHosts())
                {
                    List<ServerInstance> servers = group.serversPerHost.get(host);
                    if (servers.isEmpty() || serverIndex >= servers.size())
                    {
                        hiddenServersHtml.appendHtmlConstant("<td>&nbsp;</td>");
                    }
                    else
                    {
                        // Generate table data for one server instance
                        ServerInstance server = servers.get(serverIndex);
                        hiddenServersHtml.appendHtmlConstant("<td class='domainOverviewCell " + group.index +
                                "_light' data-member-of-group='" + group.id + "' data-group-index='" + group.index + "'>");
                        hiddenServersHtml.appendHtmlConstant("<span>Server: ").appendEscaped(server.getName())
                                .appendHtmlConstant("</span>");
                        ImageResource status = server.isRunning() ? Icons.INSTANCE.status_good() : Icons.INSTANCE
                                .status_bad();
                        if (server.isRunning() && server.getFlag() != null)
                        {
                            status = Icons.INSTANCE.status_warn();
                        }
                        hiddenServersHtml.appendHtmlConstant(
                                "<span style='float:right;'><img src='" + new Image(status).getUrl() + "' width='16' " +
                                        "height='16'/></span>");
                        hiddenServersHtml.appendHtmlConstant("</td>");
                    }
                }
                hiddenServersHtml.appendHtmlConstant("</tr>");
            }
        }
        com.google.gwt.user.client.Element hiddenBody = root.getElementById(HIDDEN_SERVERS_ID);
        hiddenBody.setInnerHTML(hiddenServersHtml.toSafeHtml().asString());

        // fill the visible table
        // Create tr/td for server groups
        SafeHtmlBuilder groupsHtml = new SafeHtmlBuilder();
        for (ServerGroup group : groups)
        {
            // first row contains the group name and is marked with the "data-group" attribute
            groupsHtml.appendHtmlConstant("<tr data-group='" + SERVER_GROUP_START + "'>");
            groupsHtml.appendHtmlConstant("<td id='" + group.id + "' rowspan='" + group.maxServersPerHost +
                    "' class='domainOverviewCell " + group.index + "'>");
            groupsHtml.appendHtmlConstant("Group: ").appendEscaped(group.name);
            if (group.profile != null)
            {
                groupsHtml.appendHtmlConstant("<br/>Profile: ").appendEscaped(group.profile);
            }
            groupsHtml.appendHtmlConstant("</td></tr>");
            if (group.maxServersPerHost > 1)
            {
                // prepare remaining rows (make copy node more easy)
                for (int i = 1; i < group.maxServersPerHost; i++)
                {
                    groupsHtml.appendHtmlConstant("<tr/>");
                }
            }
        }
        com.google.gwt.user.client.Element visibleHostsBody = root.getElementById(VISIBLE_SERVERS_ID);
        visibleHostsBody.setInnerHTML(groupsHtml.toSafeHtml().asString());

        // register hover events over server group td
        for (ServerGroup group : groups)
        {
            com.google.gwt.user.client.Element element = root.getElementById(group.id);
            if (element != null)
            {
                DOM.setEventListener(element, new EventListener()
                {
                    @Override
                    public void onBrowserEvent(final Event event)
                    {
                        if (event.getTypeInt() == ONMOUSEOVER)
                        {
                            Element serverGroupTd = event.getEventTarget().cast();
                            String cssClassname = serverGroupTd.getClassName();
                            List<Element> serverCells = findServerCells(serverGroupTd.getId());
                            for (Element serverCell : serverCells)
                            {
                                String index = serverCell.getAttribute("data-group-index");
                                serverCell.removeClassName(index + "_light");
                                serverCell.addClassName(index);
                            }
                        }
                        else if (event.getTypeInt() == ONMOUSEOUT)
                        {
                            Element serverGroupTd = event.getEventTarget().cast();
                            String cssClassname = serverGroupTd.getClassName();
                            List<Element> serverCells = findServerCells(serverGroupTd.getId());
                            for (Element serverCell : serverCells)
                            {
                                String index = serverCell.getAttribute("data-group-index");
                                serverCell.removeClassName(index);
                                serverCell.addClassName(index + "_light");
                            }
                        }
                    }
                });
                DOM.sinkEvents(element, ONMOUSEOVER | ONMOUSEOUT);
            }
        }

        // "scroll" to the first host (copy the nodes from hidden hosts table to visible table)
        scrollTo(0);
    }

    private SortedSet<ServerGroup> deriveGroups(List<HostInfo> hosts)
    {
        SortedMap<String, ServerGroup> serverGroups = new TreeMap<String, ServerGroup>();
        for (HostInfo host : hosts)
        {
            List<ServerInstance> serverInstances = host.getServerInstances();
            for (ServerInstance server : serverInstances)
            {
                String group = server.getGroup();
                String profile = server.getProfile();
                ServerGroup serverGroup = serverGroups.get(group);
                if (serverGroup == null)
                {
                    serverGroup = new ServerGroup(group, profile);
                    serverGroups.put(group, serverGroup);
                }
            }
        }

        // assign colors *after* the groups were sorted
        int index = 0;
        TreeSet<ServerGroup> orderedGroups = new TreeSet<ServerGroup>(serverGroups.values());
        for (ServerGroup group : orderedGroups)
        {
            group.index = SERVER_GROUP_CLASS + (index % SERVER_GROUP_COLORS);
            index++;
        }
        return orderedGroups;
    }

    private void scrollTo(int index)
    {
        if (index < 0 || index > hostSize - 1)
        {
            return;
        }
        hostIndex = index;
        int rowsToCopy = VISIBLE_HOSTS_COLUMNS;
        if (hostIndex + rowsToCopy > hostSize)
        {
            rowsToCopy = hostSize - hostIndex;
        }

        // 1. Hosts
        Element hiddenHostsTableHead = root.getElementById(HIDDEN_HOSTS_ID);
        Element visibleHostsTableHead = root.getElementById(VISIBLE_HOSTS_ID);

        // 1.1 Clear the visible hosts
        Element visibleHostsTr = visibleHostsTableHead.getFirstChildElement();
        while (visibleHostsTr.getChildNodes().getLength() > 1)
        {
            visibleHostsTr.removeChild(visibleHostsTr.getChild(1));
        }

        // 1.2 Clone and copy hosts from hidden to visible
        Element hiddenHostsTr = hiddenHostsTableHead.getFirstChildElement();
        NodeList<Node> hiddenHostsTds = hiddenHostsTr.getChildNodes();
        for (int i = 0; i < rowsToCopy; i++)
        {
            Node hiddenHostTd = hiddenHostsTds.getItem(i);
            Node visibleHostTd = hiddenHostTd.cloneNode(true);
            visibleHostsTr.appendChild(visibleHostTd);
        }

        // 2. Servers
        Element hiddenServersTableBody = root.getElementById(HIDDEN_SERVERS_ID);
        Element visibleServersTableBody = root.getElementById(VISIBLE_SERVERS_ID);

        // 2.1 Clear the visible servers
        NodeList<Node> visibleServerTrs = visibleServersTableBody.getChildNodes();
        for (int i = 0; i < visibleServerTrs.getLength(); i++)
        {
            int indexAndCount = 0;
            Element visibleServerTr = visibleServerTrs.getItem(i).cast();
            if (SERVER_GROUP_START.equals(visibleServerTr.getAttribute("data-group")))
            {
                // This row contains the server group which must not be removed
                indexAndCount = 1;
            }
            while (visibleServerTr.getChildNodes().getLength() > indexAndCount)
            {
                visibleServerTr.removeChild(visibleServerTr.getChild(indexAndCount));
            }

        }

        // 2.2 Clone and copy servers from hidden to visible
        NodeList<Node> hiddenServerTrs = hiddenServersTableBody.getChildNodes();
        if (hiddenServerTrs.getLength() != visibleServerTrs.getLength())
        {
            // TODO: Something is messed up --> Log error and return?
        }
        for (int i = 0; i < hiddenServerTrs.getLength(); i++)
        {
            Node hiddenServerTr = hiddenServerTrs.getItem(i);
            Node visibleServerTr = visibleServerTrs.getItem(i);
            NodeList<Node> hiddenServerTds = hiddenServerTr.getChildNodes();
            for (int j = 0; j < rowsToCopy; j++)
            {
                Node hiddenServerTd = hiddenServerTds.getItem(j);
                Node visibleServerTd = hiddenServerTd.cloneNode(true);
                visibleServerTr.appendChild(visibleServerTd);
            }
        }
    }

    private List<Element> findServerCells(String groupId)
    {

        List<Element> elements = new ArrayList<Element>();
        Element serversTableBody = root.getElementById(VISIBLE_SERVERS_ID);
        NodeList<Node> serverTrs = serversTableBody.getChildNodes();
        for (int i = 0; i < serverTrs.getLength(); i++)
        {
            Element serverTr = serverTrs.getItem(i).cast();
            NodeList<Node> serverTds = serverTr.getChildNodes();
            for (int j = 0; j < serverTds.getLength(); j++)
            {
                Element serverTd = serverTds.getItem(j).cast();
                if (groupId.equals(serverTd.getAttribute("data-member-of-group")))
                {
                    elements.add(serverTd);
                }
            }
        }
        return elements;
    }
}
