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
package org.jboss.as.console.client.domain.topology;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import static com.google.gwt.user.client.Event.ONCLICK;
import static java.lang.Math.min;
import static org.jboss.as.console.client.domain.topology.HtmlGenerator.*;

/**
 * @author Harald Pehl
 * @dat 10/09/12
 */
public class TopologyView extends SuspendableViewImpl implements TopologyPresenter.MyView
{
    static final int TABLE_WIDTH = 100; // percent
    static final int VISIBLE_HOSTS_COLUMNS = 3;
    static final int ONE_COLUMN = TABLE_WIDTH / (VISIBLE_HOSTS_COLUMNS + 1); // one column for the groups
    static final int HOSTS_COLUMNS = TABLE_WIDTH - ONE_COLUMN;
    static final int SERVER_GROUP_COLORS = 5; // must match the '.serverGroupX' css class names

    private TopologyPresenter presenter;
    private HTMLPanel root;
    private int hostIndex = 0; // the index of the current visible host
    private int hostSize = 0;


    @Override
    public Widget createWidget()
    {
        SimpleLayout layout = new SimpleLayout()
                .setTitle("Topology")
                .setHeadline("Hosts, groups and server instances")
                .setDescription("An overview of all hosts, groups and server instances of the domain.");
        root = new HTMLPanel(new HtmlGenerator().root().toSafeHtml().asString());
        layout.addContent("domain", root);
        return layout.build();
    }

    @Override
    public void setPresenter(final TopologyPresenter presenter)
    {
        this.presenter = presenter;
    }

    @Override
    public void updateHosts(final List<HostInfo> hosts)
    {
        // initialize
        hostIndex = 0;
        hostSize = hosts.size();
        HtmlGenerator hiddenHosts = new HtmlGenerator();
        HtmlGenerator hiddenServers = new HtmlGenerator();
        HtmlGenerator visible = new HtmlGenerator();
        HtmlGenerator columns = new HtmlGenerator();
        HtmlGenerator navigation = new HtmlGenerator();

        // get groups
        SortedSet<ServerGroup> groups = deriveGroups(hosts);
        for (ServerGroup group : groups)
        {
            group.fill(hosts);
        }
        Set<HostInfo> orderedHosts = groups.first().getHosts();

        // fill the hidden table
        // first row: hosts
        hiddenHosts.startRow();
        for (HostInfo host : orderedHosts)
        {
            hiddenHosts.appendHost(host);
        }
        hiddenHosts.endRow();
        com.google.gwt.user.client.Element hiddenHead = root.getElementById(HIDDEN_HOSTS_ID);
        hiddenHead.setInnerHTML(hiddenHosts.toSafeHtml().asString());

        // remaining rows: servers
        for (ServerGroup group : groups)
        {
            for (int serverIndex = 0; serverIndex < group.maxServersPerHost; serverIndex++)
            {
                hiddenServers.startRow();
                for (HostInfo host : group.getHosts())
                {
                    List<ServerInstance> servers = group.serversPerHost.get(host);
                    if (servers.isEmpty() || serverIndex >= servers.size())
                    {
                        hiddenServers.emptyCell();
                    }
                    else
                    {
                        // Generate td for one server instance
                        hiddenServers.appendServer(group, host.getName(), servers.get(serverIndex));
                    }
                }
                hiddenServers.endRow();
            }
        }
        com.google.gwt.user.client.Element hiddenBody = root.getElementById(HIDDEN_SERVERS_ID);
        hiddenBody.setInnerHTML(hiddenServers.toSafeHtml().asString());

        // fill the visible table
        // Adjust columns
        int hostColumns = min(VISIBLE_HOSTS_COLUMNS, hostSize);
        int columnWidth = HOSTS_COLUMNS / hostColumns;
        columns.appendColumn(ONE_COLUMN);
        for (int i = 0; i < hostColumns; i++)
        {
            columns.appendColumn(columnWidth);
        }
        com.google.gwt.user.client.Element hostColGroup = root.getElementById(HOST_COLGROUP_ID);
        hostColGroup.setInnerHTML(columns.toSafeHtml().asString());

        // create tr/td for server groups
        for (ServerGroup group : groups)
        {
            visible.appendServerGroup(group);

        }
        com.google.gwt.user.client.Element visibleHostsBody = root.getElementById(VISIBLE_SERVERS_ID);
        visibleHostsBody.setInnerHTML(visible.toSafeHtml().asString());
        registerEvents(visible.getClickIds());

        // add navigation
        if (hostSize > VISIBLE_HOSTS_COLUMNS)
        {
            navigation.appendNavigation();
        }
        com.google.gwt.user.client.Element navigationFooter = root.getElementById(NAVIGATION_ID);
        navigationFooter.setInnerHTML(navigation.toSafeHtml().asString());
        registerEvents(navigation.getClickIds());

        // "scroll" to the first host (copy the DOM nodes from hidden hosts table to visible table)
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
            group.cssClassname = SERVER_GROUP_CLASS + (index % SERVER_GROUP_COLORS);
            index++;
        }
        return orderedGroups;
    }

    private void registerEvents(List<String> ids)
    {
        ClickListener clickListener = new ClickListener();
        for (String id : ids)
        {
            com.google.gwt.user.client.Element element = root.getElementById(id);
            if (element != null)
            {
                DOM.setEventListener(element, clickListener);
                DOM.sinkEvents(element, ONCLICK);
            }
        }
    }

    private void scrollTo(final int index)
    {
        // validation
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
            Node hiddenHostTd = hiddenHostsTds.getItem(hostIndex + i);
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
            if (SERVER_GROUP_START_DATA.equals(visibleServerTr.getAttribute("data-group")))
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
                Element hiddenServerTd = hiddenServerTds.getItem(hostIndex + j).cast();
                Element visibleServerTd = hiddenServerTd.cloneNode(true).cast();
                visibleServerTr.appendChild(visibleServerTd);
            }
        }

        // Register click handlers for lifecycle links
        List<String> ids = new ArrayList<String>();
        NodeList<Element> elements = Document.get().getElementsByTagName("a");
        for (int i = 0; i < elements.getLength(); i++)
        {
            Element element = elements.getItem(i);
            if ("lifecycleLink".equals(element.getClassName()))
            {
                ids.add(element.getId());
            }
        }
        registerEvents(ids);

        // update visibility of navigation
        com.google.gwt.user.client.Element prev = root.getElementById(PREV_HOST_ID);
        com.google.gwt.user.client.Element next = root.getElementById(NEXT_HOST_ID);
        if (prev != null && next != null)
        {
            String prevVisibility = hostIndex - VISIBLE_HOSTS_COLUMNS < 0 ? "hidden" : "visible";
            String nextVisibility = hostIndex + VISIBLE_HOSTS_COLUMNS > hostSize ? "hidden" : "visible";
            DOM.setStyleAttribute(prev, "visibility", prevVisibility);
            DOM.setStyleAttribute(next, "visibility", nextVisibility);
        }
    }

    private class ClickListener implements EventListener
    {
        @Override
        public void onBrowserEvent(final Event event)
        {
            if (event.getTypeInt() == ONCLICK)
            {
                Element element = event.getEventTarget().cast();
                String id = element.getId();
                if (PREV_HOST_ID.equals(id))
                {
                    scrollTo(hostIndex - VISIBLE_HOSTS_COLUMNS);
                }
                else if (NEXT_HOST_ID.equals(id))
                {
                    scrollTo(hostIndex + VISIBLE_HOSTS_COLUMNS);
                }
                else
                {
                    Window.alert("Click handler for " + id + " not yet implemented");
                }
            }
        }
    }
}
