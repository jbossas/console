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

package org.jboss.as.console.client.core;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.message.MessageBar;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.profiles.ProfileSelector;
import org.jboss.as.console.client.domain.runtime.HostSelector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Top level header, gives access to main applications.
 *
 * @author Heiko Braun
 * @date 1/28/11
 */
public class Header implements ValueChangeHandler<String> {

    private HTMLPanel linksPane;
    private String currentHighlightedSection = null;

    private DeckPanel subnavigation;
    private HostSelector runtimeSelector = null;
    private HostSelector hostSelector = null;
    private ProfileSelector profileSelector = null;

    public static final String[][] SECTIONS = {
            new String[]{NameTokens.ProfileMgmtPresenter, Console.CONSTANTS.common_label_profiles()},
            new String[]{NameTokens.HostMgmtPresenter, Console.CONSTANTS.common_label_hosts()},
            new String[]{NameTokens.DomainRuntimePresenter, "Runtime"}
    };

    public static final String[][] SECTIONS_STANADLONE = {
            new String[]{NameTokens.serverConfig, "Profile"},
            new String[]{NameTokens.StandaloneRuntimePresenter, "Runtime"}
    };

    private MessageBar messageBar;

    private Map<String,Widget> appLinks = new HashMap<String, Widget>();

    private LayoutPanel headlineContainer;
    private BootstrapContext bootstrap;

    @Inject
    public Header(MessageBar messageBar, BootstrapContext bootstrap) {
        this.messageBar = messageBar;
        this.bootstrap = bootstrap;
        History.addValueChangeHandler(this);
    }

    public Widget asWidget() {

        LayoutPanel outerLayout = new LayoutPanel();
        outerLayout.addStyleName("page-header");

        headlineContainer = new LayoutPanel();
        headlineContainer.setStyleName("fill-layout");

        Widget logo = getLogoSection();
        Widget links = getLinksSection();

        LayoutPanel innerLayout = new LayoutPanel();
        innerLayout.add(logo);
        innerLayout.add(links);

        innerLayout.setWidgetLeftWidth(logo, 0, Style.Unit.PX, 50, Style.Unit.PCT);
        innerLayout.setWidgetRightWidth(links, 10, Style.Unit.PX, 50, Style.Unit.PCT);
        innerLayout.setWidgetTopHeight(links, 0, Style.Unit.PX, 80, Style.Unit.PX);

        outerLayout.add(innerLayout);

        outerLayout.setWidgetTopHeight(innerLayout, 0, Style.Unit.PX, 80, Style.Unit.PX);

        return outerLayout;
    }

    private Widget getLogoSection() {

        HorizontalPanel panel = new HorizontalPanel();
        Image logo = null;

        if(org.jboss.as.console.client.Build.PROFILE.equals("eap"))
            logo = new Image("images/logo/eap6.png");
        else
            logo = new Image("images/logo/jbossas7.png");

        logo.setStyleName("logo");

        panel.add(logo);
        HTML prodVersion = new HTML(org.jboss.as.console.client.Build.PROD_VERSION);
        prodVersion.setStyleName("header-prod-version");
        panel.add(prodVersion);


        logo.getElement().getParentElement().setAttribute("valign", "top");
        logo.getElement().getParentElement().setAttribute("style", "vertical-align:top;");

        prodVersion.getElement().getParentElement().setAttribute("valign", "top");
        prodVersion.getElement().getParentElement().setAttribute("style", "vertical-align:top;");
        return panel;
    }

    private Widget getLinksSection() {
        linksPane = new HTMLPanel(createLinks());
        linksPane.getElement().setId("header-links-section");

        String[][] sections = bootstrap.getProperty(BootstrapContext.STANDALONE).equals("true") ?
                SECTIONS_STANADLONE : SECTIONS;

        for (String[] section : sections) {
            final String name = section[0];
            final String id = "header-" + name;
            HTML widget = new HTML(section[1]);

            widget.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    Console.MODULES.getPlaceManager().revealPlace(
                            new PlaceRequest(name)
                    );
                }
            });
            linksPane.add(widget, id);

        }

        subnavigation = createSubnavigation();
        linksPane.add(subnavigation, "subnavigation");

        return linksPane;
    }

    private String createLinks() {

        String[][] sections = bootstrap.getProperty(BootstrapContext.STANDALONE).equals("true") ? SECTIONS_STANADLONE : SECTIONS;

        SafeHtmlBuilder headerString = new SafeHtmlBuilder();

        if(sections.length>0)
        {
            headerString.appendHtmlConstant("<table border=0 class='header-links' cellpadding=0 cellspacing=0 border=0>");
            headerString.appendHtmlConstant("<tr id='header-links-ref'>");

            headerString.appendHtmlConstant("<td><img src=\"images/blank.png\" width=1/></td>");
            for (String[] section : sections) {

                final String name = section[0];
                final String id = "header-" + name;
                String styleClass = "header-link";
                String styleAtt = "vertical-align:middle; text-align:center";

                String td =  "<td width='100px' style='"+styleAtt+"' id='" + id +"' class='"+styleClass+"'></td>";

                headerString.appendHtmlConstant(td);
                //headerString.append(title);

                //headerString.appendHtmlConstant("<td ><img src=\"images/blank.png\" width=1 height=32/></td>");

            }

            headerString.appendHtmlConstant("</tr>");
            headerString.appendHtmlConstant("</table>");
            headerString.appendHtmlConstant("<div id='subnavigation' style='float:right;clear:right;'/>");
        }

        return headerString.toSafeHtml().asString();
    }

    @Override
    public void onValueChange(ValueChangeEvent<String> event) {
        String historyToken = event.getValue();
        if(historyToken.equals(currentHighlightedSection))
            return;
        else
            currentHighlightedSection = historyToken;

        if(historyToken.indexOf("/")!=-1)
        {
            highlight(historyToken.substring(0, historyToken.indexOf("/")));
        }
        else
        {
            highlight(historyToken);
        }
    }

    public void highlight(String name)
    {
        if(name.equals(NameTokens.ProfileMgmtPresenter))
        {
            subnavigation.showWidget(0);
        }
        else if(name.equals(NameTokens.HostMgmtPresenter))
        {
            subnavigation.showWidget(1);
        }
        else if(name.equals(NameTokens.DomainRuntimePresenter))
        {
            subnavigation.showWidget(2);
        }

        com.google.gwt.user.client.Element target = linksPane.getElementById("header-links-ref");
        if(target!=null) // standalone doesn't provide any top level links
        {
            NodeList<Node> childNodes = target.getChildNodes();
            for(int i=0; i<childNodes.getLength(); i++)
            {
                Node n = childNodes.getItem(i);
                if(Node.ELEMENT_NODE == n.getNodeType())
                {
                    Element element = (Element) n;
                    if(element.getId().equals("header-"+name))
                        element.addClassName("header-link-selected");
                    else
                        element.removeClassName("header-link-selected");
                }
            }
        }

    }

    public void setContent(Widget content) {
        headlineContainer.clear();
        headlineContainer.add(content);
    }

    public void setHosts(List<Host> hosts) {
        if(runtimeSelector!=null)
        {
            List<String> hostNames = new ArrayList<String>(hosts.size());
            for(Host h : hosts)
            {
                hostNames.add(h.getName());
            }

            runtimeSelector.setHosts(hostNames);
            hostSelector.setHosts(hostNames);

        }
    }

    public void setServers(String host, List<Server> server) {
        if(runtimeSelector!=null)
        {
            List<String> serverNames = new ArrayList<String>(server.size());
            for(Server s : server)
            {
                serverNames.add(s.getName());
            }

            runtimeSelector.setServersOnHost(host, serverNames);

        }
    }

    public DeckPanel createSubnavigation() {

        DeckPanel subnavigation = new DeckPanel();

        profileSelector = new ProfileSelector();
        subnavigation.add(profileSelector.asWidget());

        hostSelector = new HostSelector();
        hostSelector.setServerSelection(false);
        subnavigation.add(hostSelector.asWidget());

        runtimeSelector = new HostSelector();
        subnavigation.add(runtimeSelector.asWidget());

        return subnavigation;
    }

    public void setProfiles(List<ProfileRecord> profiles) {

        if(profileSelector!=null)
        {
            List<String> profileNames = new ArrayList<String>(profiles.size());
            for(ProfileRecord p :profiles)
            {
                profileNames.add(p.getName());
            }

            profileSelector.setProfiles(profileNames);

        }


    }
}
