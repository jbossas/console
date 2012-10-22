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
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.HasRows;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.RowCountChangeEvent;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.domain.model.impl.LifecycleOperation;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;
import org.jboss.ballroom.client.widgets.window.Feedback;

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
import static org.jboss.as.console.client.domain.model.impl.LifecycleOperation.*;

/**
 * @author Harald Pehl
 * @dat 10/09/12
 */
public class TopologyView extends SuspendableViewImpl implements TopologyPresenter.MyView
{
    static final int TABLE_WIDTH = 100; // percent
    static final int SERVER_GROUPS_COLUMN = 15; // percent
    static final int HOSTS_COLUMNS = TABLE_WIDTH - SERVER_GROUPS_COLUMN;
    static final int VISIBLE_HOSTS_COLUMNS = 3;
    static final int SERVER_GROUP_COLORS = 5; // must match the '.serverGroupX' css class names

    private TopologyPresenter presenter;
    private HTMLPanel root;
    private int hostIndex = 0; // the index of the current visible host
    private int visibleHosts;
    private int hostSize = 0;
    private HostsPager pager;
    private HostsDisplay display;
    private LifecycleLinkListener lifecycleLinkListener;


    @Override
    public Widget createWidget()
    {
        lifecycleLinkListener = new LifecycleLinkListener();

        SimpleLayout layout = new SimpleLayout()
                .setTitle("Topology")
                .setHeadline("Hosts, groups and server instances")
                .setDescription("An overview of all hosts, groups and server instances in the domain.");
        root = new HTMLPanel(new HtmlGenerator().root().toSafeHtml().asString());
        display = new HostsDisplay();
        pager = new HostsPager();
        pager.setDisplay(display);
        pager.setPageSize(VISIBLE_HOSTS_COLUMNS);
        root.add(pager);
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
        visibleHosts = min(VISIBLE_HOSTS_COLUMNS, hostSize);
        HtmlGenerator hiddenHosts = new HtmlGenerator();
        HtmlGenerator hiddenServers = new HtmlGenerator();
        HtmlGenerator visible = new HtmlGenerator();
        HtmlGenerator columns = new HtmlGenerator();

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
                boolean endOfServerGroup = serverIndex == group.maxServersPerHost - 1;
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
        int columnWidth = HOSTS_COLUMNS / visibleHosts;
        columns.appendColumn(SERVER_GROUPS_COLUMN);
        for (int i = 0; i < visibleHosts; i++)
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

        // update navigation
        RowCountChangeEvent.fire(display, hostSize, true);
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
        for (String id : ids)
        {
            com.google.gwt.user.client.Element element = root.getElementById(id);
            if (element != null)
            {
                DOM.setEventListener(element, lifecycleLinkListener);
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
        int columnsToCopy = VISIBLE_HOSTS_COLUMNS;
        if (hostIndex + columnsToCopy > hostSize)
        {
            columnsToCopy = hostSize - hostIndex;
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
        for (int i = 0; i < columnsToCopy; i++)
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
            for (int j = 0; j < columnsToCopy; j++)
            {
                Element hiddenServerTd = hiddenServerTds.getItem(hostIndex + j).cast();
                Element visibleServerTd = hiddenServerTd.cloneNode(true).cast();
                visibleServerTr.appendChild(visibleServerTd);
                if (j == columnsToCopy - 1)
                {
                    visibleServerTd.addClassName("cellTableLastColumn");
                }
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

        // update navigation
        RangeChangeEvent.fire(display, new Range(hostIndex, columnsToCopy));
    }


    /**
     * Listener for lifecycle links (start, stop, reload (server) groups. The clicked element contains
     * "data"- attributes which carry the relevant (server) group and host.
     */
    private class LifecycleLinkListener implements EventListener
    {
        @Override
        public void onBrowserEvent(final Event event)
        {
            if (event.getTypeInt() == ONCLICK)
            {
                Element element = event.getEventTarget().cast();
                final String id = element.getId();
                if (id != null)
                {
                    if (id.contains("_server_"))
                    {
                        final String server = element.getAttribute(DATA_SERVER_NAME);
                        final String host = element.getAttribute(DATA_HOST_NAME);
                        final LifecycleOperation op = getLifecycleOperation(id);
                        if (op != null)
                        {
                            Feedback.confirm("Modify Server",
                                    "Do really want to " + op.name().toLowerCase() + " server " + server + "?",
                                    new Feedback.ConfirmationHandler()
                                    {
                                        @Override
                                        public void onConfirmation(boolean isConfirmed)
                                        {
                                            if (isConfirmed)
                                            {
                                                presenter.onServerInstanceLifecycle(host, server, op);
                                            }
                                        }
                                    });
                        }
                    }
                    else if (id.contains("_group_"))
                    {
                        final String group = element.getAttribute(DATA_GROUP_NAME);
                        final LifecycleOperation op = getLifecycleOperation(id);
                        if (op != null)
                        {
                            Feedback.confirm("Modify Server", "Do really want to " + op.name().toLowerCase() + " all servers in group " + group + "?",
                                    new Feedback.ConfirmationHandler()
                                    {
                                        @Override
                                        public void onConfirmation(boolean isConfirmed)
                                        {
                                            if (isConfirmed)
                                            {
                                                presenter.onGroupLifecycle(group, op);
                                            }
                                        }
                                    });
                        }
                    }
                }
            }
        }

        private LifecycleOperation getLifecycleOperation(final String id)
        {
            LifecycleOperation op = null;
            if (id.startsWith(START_SERVER_ID))
            {
                op = START;
            }
            else if (id.startsWith(STOP_SERVER_ID))
            {
                op = STOP;
            }
            else if (id.startsWith(RELOAD_SERVER_ID))
            {
                op = RELOAD;
            }
            return op;
        }
    }


    /**
     * Pager which delegates to {@link TopologyView#scrollTo(int)}.
     */
    private class HostsPager extends DefaultPager
    {
        @Override
        public void firstPage()
        {
            scrollTo(0);
        }

        @Override
        public void lastPage()
        {
            scrollTo((getPageCount() - 1) * VISIBLE_HOSTS_COLUMNS);
        }

        @Override
        public void nextPage()
        {
            scrollTo(getPageStart() + VISIBLE_HOSTS_COLUMNS);
        }

        @Override
        public void previousPage()
        {
            scrollTo(getPageStart() - VISIBLE_HOSTS_COLUMNS);
        }
    }


    /**
     * An implementation for the topology tabel pagers display. Although this class implements
     * Has<em>Rows</em> the paging is over <em>columns</em>.
     */
    private class HostsDisplay implements HasRows
    {
        @Override
        public HandlerRegistration addRangeChangeHandler(final RangeChangeEvent.Handler handler)
        {
            return root.addHandler(handler, RangeChangeEvent.getType());
        }

        @Override
        public HandlerRegistration addRowCountChangeHandler(final RowCountChangeEvent.Handler handler)
        {
            return root.addHandler(handler, RowCountChangeEvent.getType());
        }

        @Override
        public int getRowCount()
        {
            return hostSize;
        }

        @Override
        public Range getVisibleRange()
        {
            return new Range(hostIndex, visibleHosts);
        }

        @Override
        public boolean isRowCountExact()
        {
            return true;
        }

        @Override
        public void setRowCount(final int count)
        {
        }

        @Override
        public void setRowCount(final int count, final boolean isExact)
        {
        }

        @Override
        public void setVisibleRange(final int start, final int length)
        {
        }

        @Override
        public void setVisibleRange(final Range range)
        {
        }

        @Override
        public void fireEvent(final GwtEvent<?> event)
        {
            root.fireEvent(event);
        }
    }
}
