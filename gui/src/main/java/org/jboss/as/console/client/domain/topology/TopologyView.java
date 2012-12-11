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

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.HasRows;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.RowCountChangeEvent;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.domain.model.impl.LifecycleOperation;
import org.jboss.as.console.client.shared.runtime.ext.Extension;
import org.jboss.as.console.client.shared.runtime.ext.ExtensionView;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
import org.jboss.as.console.client.widgets.Hint;
import org.jboss.as.console.client.widgets.tabs.DefaultTabLayoutPanel;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;

import java.util.List;
import java.util.SortedSet;

import static com.google.gwt.user.client.Event.ONCLICK;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.jboss.as.console.client.domain.model.impl.LifecycleOperation.*;
import static org.jboss.as.console.client.domain.topology.HtmlGenerator.*;

/**
 * @author Harald Pehl
 * @date 10/09/12
 */
public class TopologyView extends SuspendableViewImpl implements TopologyPresenter.MyView
{
    static final String SERVER_GROUP_CLASS = "serverGroup";
    static final int TABLE_WIDTH = 100; // percent
    static final int SERVER_GROUPS_COLUMN = 15; // percent
    static final int HOSTS_COLUMNS = TABLE_WIDTH - SERVER_GROUPS_COLUMN;
    static final int SERVER_GROUP_COLORS = 5; // must match the '.serverGroupX' css class names

    private int hostIndex = 0; // the index of the current visible host
    private int visibleHosts = TopologyPresenter.VISIBLE_HOSTS_COLUMNS;
    private int hostSize = 0;
    private TopologyPresenter presenter;
    private HostsDisplay display;
    private LifecycleLinkListener lifecycleLinkListener;
    private FlowPanel container;
    private ExtensionView extensions;
    private HostsPager pager;

    @Override
    public Widget createWidget()
    {
        lifecycleLinkListener = new LifecycleLinkListener();

        ToolStrip topLevelTools = new ToolStrip();
        topLevelTools.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_refresh(),
                new ClickHandler()
                {
                    @Override
                    public void onClick(ClickEvent event)
                    {
                        presenter.loadTopology();
                    }
                }));

        SimpleLayout layout = new SimpleLayout()
                .setTopLevelTools(topLevelTools)
                .setTitle("Topology")
                .setPlain(true)
                .setHeadline("Hosts, groups and server instances")
                .setDescription("An overview of all hosts, groups and server instances in the domain.");

        container = new FlowPanel();
        display = new HostsDisplay();
        pager = new HostsPager();
        pager.setDisplay(display);
        pager.setPageSize(TopologyPresenter.VISIBLE_HOSTS_COLUMNS);

        layout.addContent("topology", container);

        // ---------------------

        extensions = new ExtensionView();

        // ---------------------

        DefaultTabLayoutPanel tabLayoutpanel = new DefaultTabLayoutPanel(40, Style.Unit.PX);
        tabLayoutpanel.addStyleName("default-tabpanel");

        tabLayoutpanel.add(layout.build(), "Topology", true);
        tabLayoutpanel.add(extensions.asWidget(), "Extensions", true);

        tabLayoutpanel.selectTab(0);

        return tabLayoutpanel;
    }

    @Override
    public void setPresenter(final TopologyPresenter presenter)
    {
        this.presenter = presenter;
    }

    @Override
    public void updateHosts(SortedSet<ServerGroup> groups, final int index)
    {
        // validation
        HtmlGenerator html = new HtmlGenerator();
        if (groups == null || groups.isEmpty())
        {
            // no server/groups available ...
            Hint blank = new Hint("No server available!");
            container.clear();
            container.insert(blank, 0);
            return;
        }


        // initialization
        assignColors(groups);
        List<HostInfo> hosts = groups.first().getHosts();
        this.hostSize = hosts.size();
        this.visibleHosts = min(TopologyPresenter.VISIBLE_HOSTS_COLUMNS, hostSize);
        this.hostIndex = index;
        this.hostIndex = max(0, this.hostIndex);
        this.hostIndex = min(this.hostIndex, this.hostSize - 1);
        int endIndex = min(this.hostIndex + TopologyPresenter.VISIBLE_HOSTS_COLUMNS, hostSize);

        // start table and add columns
        html.startTable().appendHtmlConstant("<colgroup>");
        int columnWidth = HOSTS_COLUMNS / (endIndex - this.hostIndex);
        html.appendColumn(SERVER_GROUPS_COLUMN);
        for (int i = this.hostIndex; i < endIndex; i++)
        {
            html.appendColumn(columnWidth);
        }
        html.appendHtmlConstant("</colgroup>");

        // first row contains host names
        html.appendHtmlConstant("<thead><tr><th class='cellTableHeader'>Hosts&nbsp;&rarr;<br/>Groups&nbsp;&darr;</th>");
        for (int i = this.hostIndex; i < endIndex; i++)
        {
            HostInfo host = hosts.get(i);
            html.appendHost(host);
        }
        html.appendHtmlConstant("</tr></thead>");

        // remaining rows contain server groups and server instances
        html.appendHtmlConstant("<tbody>");
        for (ServerGroup group : groups)
        {
            for (int serverIndex = 0; serverIndex < group.maxServersPerHost; serverIndex++)
            {
                html.appendHtmlConstant("<tr>");
                if (serverIndex == 0)
                {
                    html.appendServerGroup(group);
                }
                for (int i = this.hostIndex; i < endIndex; i++)
                {
                    HostInfo host = hosts.get(i);
                    List<ServerInstance> servers = group.serversPerHost.get(host);
                    if (servers.isEmpty() || serverIndex >= servers.size())
                    {
                        html.emptyCell();
                    }
                    else
                    {
                        html.appendServer(group, host.getName(), servers.get(serverIndex));
                    }
                }
                html.appendHtmlConstant("</tr>");
            }
        }
        html.appendHtmlConstant("</tbody>").endTable();

        // create html panel and register events
        HTMLPanel panel = html.createPanel();
        for (String id : html.getLifecycleIds())
        {
            com.google.gwt.user.client.Element element = panel.getElementById(id);
            if (element != null)
            {
                DOM.setEventListener(element, lifecycleLinkListener);
                DOM.sinkEvents(element, ONCLICK);
            }
        }
        if (container.getWidgetCount() == 2)
        {
            container.remove(0);
        }
        {
            container.clear();
        }

        container.insert(panel, 0);

        container.add(pager);

        // update navigation
        RowCountChangeEvent.fire(display, hostSize, true);
    }

    private void assignColors(SortedSet<ServerGroup> serverGroups)
    {
        int index = 0;
        for (ServerGroup group : serverGroups)
        {
            group.cssClassname = SERVER_GROUP_CLASS + (index % SERVER_GROUP_COLORS);
            index++;
        }
    }

    // ------------------------------------------------------ inner classes

    /**
     * Listener for lifecycle links (start, stop, reload (server) groups. The clicked element contains
     * "data"- attributes which carry the relevant (server) group and host informations.
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
            if (id.startsWith("start_"))
            {
                op = START;
            }
            else if (id.startsWith("stop_"))
            {
                op = STOP;
            }
            else if (id.startsWith("reload_"))
            {
                op = RELOAD;
            }
            else if (id.startsWith("restart_"))
            {
                op = RESTART;
            }
            return op;
        }
    }


    /**
     * Pager which delegates to {@link TopologyPresenter#requestHostIndex(int)}
     */
    private class HostsPager extends DefaultPager
    {
        @Override
        public void firstPage()
        {
            presenter.requestHostIndex(0);
        }

        @Override
        public void lastPage()
        {
            presenter.requestHostIndex((getPageCount() - 1) * TopologyPresenter.VISIBLE_HOSTS_COLUMNS);
        }

        @Override
        public void nextPage()
        {
            presenter.requestHostIndex(getPageStart() + TopologyPresenter.VISIBLE_HOSTS_COLUMNS);
        }

        @Override
        public void previousPage()
        {
            presenter.requestHostIndex(getPageStart() - TopologyPresenter.VISIBLE_HOSTS_COLUMNS);
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
            return container.addHandler(handler, RangeChangeEvent.getType());
        }

        @Override
        public HandlerRegistration addRowCountChangeHandler(final RowCountChangeEvent.Handler handler)
        {
            return container.addHandler(handler, RowCountChangeEvent.getType());
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
            container.fireEvent(event);
        }
    }

    @Override
    public void setExtensions(List<Extension> result) {
        extensions.setExtensions(result);
    }
}
