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

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Image;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.widgets.icons.ConsoleIcons;
import org.jboss.ballroom.client.widgets.icons.Icons;

/**
 * Contains most of the html generator code used in {@link DomainOverview}. The generated html contains several <a
 * href="http://dev.w3.org/html5/spec/global-attributes.html#embedding-custom-non-visible-data-with-the-data-*-attributes">HTML5
 * data attributes</a> to mark special tags:
 * <ul>
 *     <li>data-group: Marks a &lt;tr&gt; element as start of a new server group</li>
 *     <li>data-group-index: Holds the name of the css class for the accompanying server group for one server insance.
 *     Needed for hovering effects.</li>
 *     <li>data-member-of-group: Marks a server instance as part of a distinct server group.</li>
 * </ul>
 *
 * @author Harald Pehl
 * @dat 10/09/12
 */
final class HtmlGenerator
{
    static final String SERVER_GROUP_CLASS = "serverGroup";
    static final String HIDDEN_HOSTS_ID = "hiddenHosts";
    static final String HIDDEN_SERVERS_ID = "hiddenServers";
    static final String VISIBLE_HOSTS_ID = "visibleHost";
    static final String VISIBLE_SERVERS_ID = "visibleServers";
    /**
     * The data flag which marks the start of a new server group. Used as a flag for the &lt;tr/&gt; element which
     * contains the server group.
     */
    static final String SERVER_GROUP_START_DATA = "serverGroup";
    static final String PREV_HOST_ID = "prevHost";
    static final String NEXT_HOST_ID = "nextHost";
    final SafeHtmlBuilder html;


    HtmlGenerator()
    {
        this.html = new SafeHtmlBuilder();
    }


    // ------------------------------------------------------ custom methods

    SafeHtmlBuilder root()
    {
        html.appendHtmlConstant("<table style='width:100%;'>");
        html.appendHtmlConstant("<colgroup><col width='16%'><col width='28%'><col width='28%'><col " +
                "width='28%'></colgroup>");
        html.appendHtmlConstant("<thead id='" + VISIBLE_HOSTS_ID + "'><tr><th " +
                "class='domainOverviewHeader'>Hosts&nbsp;&rarr;<br/>Groups&nbsp;&darr;</th></tr></thead>");
        html.appendHtmlConstant("<tfoot><tr>");
        html.appendHtmlConstant("<td>&nbsp;</td>");
        html.appendHtmlConstant("<td id='" + PREV_HOST_ID + "' class='hostNavigation'>&larr; Previous Host</td>");
        html.appendHtmlConstant("<td>&nbsp;</td>");
        html.appendHtmlConstant(
                "<td id='" + NEXT_HOST_ID + "'class='hostNavigation' style='text-align:right;'>Next Host &rarr;" +
                        "</td>");
        html.appendHtmlConstant("</tr></tfoot>");
        html.appendHtmlConstant("<tbody id='" + VISIBLE_SERVERS_ID + "'/>");
        html.appendHtmlConstant("</table>");

        html.appendHtmlConstant("<table style='display:none;'><thead id='" + HIDDEN_HOSTS_ID + "'/><tbody " +
                "id='" + HIDDEN_SERVERS_ID + "'/></table>");
        return html;
    }

    SafeHtmlBuilder appendHost(final HostInfo host)
    {
        html.appendHtmlConstant("<th class='domainOverviewHeader'><span>Host: ")
                .appendEscaped(host.getName()).appendHtmlConstant("<br/>Domain: ");
        html.appendHtmlConstant(host.isController() ? "Controller" : "Member");
        html.appendHtmlConstant("</span>");
        if (host.isController())
        {
            ImageResource star = ConsoleIcons.INSTANCE.star();
            html.appendHtmlConstant("<span style='float:right;'><img src='" + new Image(star).getUrl()
                    + "' width='16' " + "height='16'/></span>");
        }
        return html;
    }

    SafeHtmlBuilder appendServer(final ServerGroup group,
            final ServerInstance server)
    {
        html.appendHtmlConstant("<td class='domainOverviewCell " + group.cssClassname +
                "_light' data-member-of-group='" + group.id + "' data-group-index='" + group.cssClassname + "'>");
        html.appendHtmlConstant("<span>Server: ").appendEscaped(server.getName())
                .appendHtmlConstant("</span>");
        ImageResource status = server.isRunning() ? Icons.INSTANCE.status_good() : Icons.INSTANCE
                .status_bad();
        if (server.isRunning() && server.getFlag() != null)
        {
            status = Icons.INSTANCE.status_warn();
        }
        html.appendHtmlConstant(
                "<span style='float:right;'><img src='" + new Image(status).getUrl() + "' width='16' " +
                        "height='16'/></span>");
        html.appendHtmlConstant("</td>");
        return html;
    }

    SafeHtmlBuilder appendServerGroup(final ServerGroup group)
    {
        // first row contains the group name and is marked with the "data-group" attribute
        html.appendHtmlConstant("<tr data-group='" + SERVER_GROUP_START_DATA + "'>");
        html.appendHtmlConstant("<td id='" + group.id + "' rowspan='" + group.maxServersPerHost +
                "' class='domainOverviewCell " + group.cssClassname + "'>");
        html.appendHtmlConstant("Group: ").appendEscaped(group.name);
        if (group.profile != null)
        {
            html.appendHtmlConstant("<br/>Profile: ").appendEscaped(group.profile);
        }
        html.appendHtmlConstant("</td></tr>");
        if (group.maxServersPerHost > 1)
        {
            // prepare remaining rows (make copy node more easy)
            for (int i = 1; i < group.maxServersPerHost; i++)
            {
                html.appendHtmlConstant("<tr/>");
            }
        }
        return html;
    }

    // ------------------------------------------------------ delegate methods

    public HtmlGenerator appendHtmlConstant(final String html)
    {
        this.html.appendHtmlConstant(html);
        return this;
    }

    public HtmlGenerator appendEscaped(final String text)
    {
        this.html.appendEscaped(text);
        return this;
    }

    public SafeHtml toSafeHtml()
    {
        return html.toSafeHtml();
    }
}
