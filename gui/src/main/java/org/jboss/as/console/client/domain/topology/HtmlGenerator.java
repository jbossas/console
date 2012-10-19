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

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Image;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.widgets.icons.ConsoleIcons;
import org.jboss.ballroom.client.widgets.icons.Icons;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.jboss.as.console.client.domain.model.ServerFlag.RELOAD_REQUIRED;
import static org.jboss.as.console.client.domain.model.ServerFlag.RESTART_REQUIRED;

/**
 * Contains most of the html generator code used in {@link TopologyView}. The generated html contains several <a
 * href="http://dev.w3.org/html5/spec/global-attributes.html#embedding-custom-non-visible-data-with-the-data-*-attributes">HTML5
 * data attributes</a> to mark special tags:
 * <ul>
 *     <li>data-group: Marks a &lt;tr&gt; element as start of a new server group</li>
 *     <li>data-group-name: The name of a server group. Used for lifecycle links.</li>
 *     <li>data-host-name: The name of the host. Used for lifecycle links.</li>
 *     <li>data-server-name: The name of a server instance. Used for lifecycle links.</li>
 * </ul>
 *
 * @author Harald Pehl
 * @date 10/15/12
 */
final class HtmlGenerator
{
    static final String SERVER_GROUP_CLASS = "serverGroup";
    static final String HOST_COLGROUP_ID = "hostColumns";
    static final String HIDDEN_HOSTS_ID = "hiddenHosts";
    static final String HIDDEN_SERVERS_ID = "hiddenServers";
    static final String VISIBLE_HOSTS_ID = "visibleHost";
    static final String VISIBLE_SERVERS_ID = "visibleServers";
    static final String SERVER_GROUP_START_DATA = "serverGroup";

    final SafeHtmlBuilder html;
    final List<String> clickIds;


    HtmlGenerator()
    {
        this.html = new SafeHtmlBuilder();
        this.clickIds = new ArrayList<String>();
    }


    // ------------------------------------------------------ custom methods

    HtmlGenerator root()
    {
        html.appendHtmlConstant("<table cellspacing='0' class='default-cell-table'>");
        html.appendHtmlConstant("<colgroup id='" + HOST_COLGROUP_ID + "'></colgroup>");
        html.appendHtmlConstant("<thead id='" + VISIBLE_HOSTS_ID + "'><tr>" +
                "<th class='cellTableHeader'>Hosts&nbsp;&rarr;<br/>Groups&nbsp;&darr;</th></tr></thead>");
        html.appendHtmlConstant("<tbody id='" + VISIBLE_SERVERS_ID + "'/>");
        html.appendHtmlConstant("</table>");

        html.appendHtmlConstant("<table style='display:none;'><thead id='" + HIDDEN_HOSTS_ID + "'/><tbody id='" +
                HIDDEN_SERVERS_ID + "'/></table>");
        return this;
    }

    HtmlGenerator appendHost(final HostInfo host)
    {
        html.appendHtmlConstant("<th class='cellTableHeader'>");
        if (host.isController())
        {
            appendIcon(ConsoleIcons.INSTANCE.star());
        }
        startLine().appendText(host.getName()).endLine();
        startLine().appendText("Domain: ").appendText(host.isController() ? "Controller" : "Member").endLine();
        html.appendHtmlConstant("</th>");
        return this;
    }

    HtmlGenerator appendServer(final ServerGroup group, final String host, final ServerInstance server)
    {
        String tooltip = "";
        ImageResource icon  = null;
        if (server.isRunning())
        {
            if (server.getFlag() != null)
            {
                icon = Icons.INSTANCE.status_warn();
                if (server.getFlag() == RELOAD_REQUIRED)
                {
                    tooltip = "Server has to be reloaded";
                }
                else if (server.getFlag() == RESTART_REQUIRED)
                {
                    tooltip = "Server has to be restarted";
                }
            }
            else
            {
                tooltip = "Server is up and running";
                icon = Icons.INSTANCE.status_good();
            }
        }
        else
        {
            tooltip = "Server is stopped";
            icon = Icons.INSTANCE.status_bad();
        }
        html.appendHtmlConstant("<td class='cellTableCell domainOverviewCell " + group.cssClassname + "_light" +
                "' title='" + tooltip + "'>");

        startLine().appendIcon(icon).appendText(server.getName()).endLine();
        if (server.getSocketBindings().size() > 0)
        {
            Set<String> sockets = server.getSocketBindings().keySet();
            String first = sockets.iterator().next();
            startLine().appendText("Socket Binding: ").appendText(first).endLine();
            startLine().appendText("Ports: +").appendText(server.getSocketBindings().get(first)).endLine();
        }

        startLinks();
        String startStop = server.isRunning() ? "stop" : "start";
        String startStopId = startStop + "_server_" + server.getName();
        String text = startStop.substring(0, 1).toUpperCase() + startStop.substring(1) + " Server";
        appendLifecycleLink(startStopId, null, host, server.getName(), text);
        if (server.isRunning() && server.getFlag() == RELOAD_REQUIRED)
        {
            String reloadId = "reload_server_" + server.getName();
            html.appendHtmlConstant("<br/>");
            appendLifecycleLink(reloadId, null, host, server.getName(), "Reload Server");
        }
        endLine();
        html.appendHtmlConstant("</td>");
        return this;
    }

    HtmlGenerator appendServerGroup(final ServerGroup group)
    {
        // first row contains the group name and is marked with the "data-group" attribute
        html.appendHtmlConstant("<tr data-group='" + SERVER_GROUP_START_DATA + "'>");
        if (group.maxServersPerHost > 1)
        {
            html.appendHtmlConstant("<td class='domainOverviewCell cellTableCell endOfServerGroup " +
                    group.cssClassname + "' rowspan='" + group.maxServersPerHost + "'>");
        }
        else
        {
            html.appendHtmlConstant("<td class='domainOverviewCell cellTableCell endOfServerGroup " +
                    group.cssClassname + "'>");
        }
        startLine().appendText(group.name).endLine();
        if (group.profile != null)
        {
            startLine().appendText(group.profile).endLine();
        }

        startLinks();
        String startId = "start_group_" + group.name;
        String stopId = "stop_group_" + group.name;
        appendLifecycleLink(startId, group.name, null, null, "Start Group");
        html.appendHtmlConstant("<br/>");
        appendLifecycleLink(stopId, group.name, null, null, "Stop Group");
        endLine();
        html.appendHtmlConstant("</td></tr>");
        if (group.maxServersPerHost > 1)
        {
            // prepare remaining rows (make copy node more easy)
            for (int i = 1; i < group.maxServersPerHost; i++)
            {
                html.appendHtmlConstant("<tr/>");
            }
        }
        return this;
    }

    HtmlGenerator appendIcon(final ImageResource img)
    {
        html.appendHtmlConstant("<img src='" + new Image(img).getUrl() + "' width='16' " + "height='16' class='statusIcon'/>");
        return this;
    }

    HtmlGenerator appendColumn(final int width)
    {
        html.appendHtmlConstant("<col width='" + width + "%'/>");
        return this;
    }

    HtmlGenerator appendLifecycleLink(String id, String group, String host, String server, String text)
    {
        clickIds.add(id);
        html.appendHtmlConstant("<a id='" + id + "' class='lifecycleLink'" +
                (group != null ? " data-group-name='" + group + "'" : "") +
                (host != null ? " data-host-name='" + host + "'" : "") +
                (server != null ? " data-server-name='" + server + "'" : "") +
                ">").appendEscaped(text).appendHtmlConstant("</a>");
        return this;
    }

    HtmlGenerator appendText(String text)
    {
        html.appendEscaped(text);
        return this;
    }

    HtmlGenerator startRow()
    {
        html.appendHtmlConstant("<tr>");
        return this;
    }


    HtmlGenerator endRow()
    {
        html.appendHtmlConstant("</tr>");
        return this;
    }

    HtmlGenerator emptyCell()
    {
        html.appendHtmlConstant("<td class='cellTableCell domainOverviewCell'>&nbsp;</td>");
        return this;
    }

    HtmlGenerator startLine()
    {
        html.appendHtmlConstant("<div>");
        return this;
    }

    HtmlGenerator endLine()
    {
        html.appendHtmlConstant("</div>");
        return this;
    }

    HtmlGenerator startLinks()
    {
        html.appendHtmlConstant("<div class='lifecycleLinks'>");
        return this;
    }

    List<String> getClickIds()
    {
        return clickIds;
    }


    // ------------------------------------------------------ delegate methods

    public HtmlGenerator appendHtmlConstant(final String text)
    {
        html.appendHtmlConstant(text);
        return this;
    }

    public HtmlGenerator appendEscaped(final String text)
    {
        html.appendEscaped(text);
        return this;
    }

    public SafeHtml toSafeHtml()
    {
        return html.toSafeHtml();
    }
}
