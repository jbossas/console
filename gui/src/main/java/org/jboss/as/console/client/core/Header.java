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
import org.jboss.as.console.client.core.message.MessageCenterView;

/**
 * Top level header, gives access to main applications.
 *
 * @author Heiko Braun
 * @date 1/28/11
 */
public class Header implements ValueChangeHandler<String> {

    private HTMLPanel linksPane;
    private String currentHighlightedSection = null;

    //private DeckPanel subnavigation;

    public static final String[][] SECTIONS = {
            new String[]{NameTokens.ProfileMgmtPresenter, Console.CONSTANTS.common_label_profiles()},
            new String[]{NameTokens.HostMgmtPresenter, Console.CONSTANTS.common_label_server()},
            new String[]{NameTokens.DomainRuntimePresenter, "Runtime"}
    };

    public static final String[][] SECTIONS_STANADLONE = {
            new String[]{NameTokens.serverConfig, "Profile"},
            new String[]{NameTokens.StandaloneRuntimePresenter, "Runtime"}
    };

    private MessageBar messageBar;

    //private Map<String,Widget> appLinks = new HashMap<String, Widget>();

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

        Widget logo = getLogoSection();
        Widget links = getLinksSection();

        LayoutPanel rhs = new LayoutPanel();
        rhs.setStyleName("fill-layout");

        MessageCenterView messageCenterView = Console.MODULES.getMessageCenterView();
        Widget messageCenter = messageCenterView.asWidget();
        rhs.add(messageCenter);
        rhs.add(links);

        rhs.setWidgetRightWidth(messageCenter, 10, Style.Unit.PX, 100, Style.Unit.PCT);
        rhs.setWidgetRightWidth(links, 10, Style.Unit.PX, 100, Style.Unit.PCT);
        rhs.setWidgetBottomHeight(messageCenter, 27, Style.Unit.PX, 28, Style.Unit.PX);
        rhs.setWidgetBottomHeight(links, 0, Style.Unit.PX, 24, Style.Unit.PX);

        LayoutPanel innerLayout = new LayoutPanel();
        innerLayout.add(logo);
        innerLayout.add(rhs);

        innerLayout.setWidgetLeftWidth(logo, 0, Style.Unit.PX, 50, Style.Unit.PCT);
        innerLayout.setWidgetRightWidth(rhs, 10, Style.Unit.PX, 50, Style.Unit.PCT);
        innerLayout.setWidgetBottomHeight(rhs, 0, Style.Unit.PX, 58, Style.Unit.PX);

        outerLayout.add(innerLayout);

        outerLayout.setWidgetTopHeight(innerLayout, 0, Style.Unit.PX, 58, Style.Unit.PX);


        outerLayout.getElement().setAttribute("role", "navigation");
        outerLayout.getElement().setAttribute("aria-label", "Toplevel Categories");

        return outerLayout;
    }

    private Widget getLogoSection() {

        HorizontalPanel panel = new HorizontalPanel();
        panel.setStyleName("logo-section");
        panel.getElement().setAttribute("role", "presentation");

        Image logo = null;

        if(org.jboss.as.console.client.Build.PROFILE.equals("eap"))
        {
            logo = new Image("images/logo/eap_text.png");
            logo.setAltText("JBoss Enterprise Application Platform");
        }
        else {
            logo = new Image("images/logo/jbossas7_text.png");
            logo.setAltText("JBoss Application Server");
        }

        logo.setStyleName("logo");

        panel.add(logo);
        HTML prodVersion = new HTML(org.jboss.as.console.client.Build.PROD_VERSION);
        prodVersion.setStyleName("header-prod-version");
        panel.add(prodVersion);


        logo.getElement().getParentElement().setAttribute("valign", "bottom");
        logo.getElement().getParentElement().setAttribute("style", "vertical-align:bottom;");

        prodVersion.getElement().getParentElement().setAttribute("valign", "bottom");
        prodVersion.getElement().getParentElement().setAttribute("style", "vertical-align:bottom;");
        prodVersion.getElement().getParentElement().setAttribute("width", "100%");
        return panel;
    }

    private Widget getLinksSection() {
        linksPane = new HTMLPanel(createLinks());
        linksPane.getElement().setId("header-links-section");
        linksPane.getElement().setAttribute("role", "menubar");
        linksPane.getElement().setAttribute("aria-controls", "main-content-area");

        String[][] sections = bootstrap.getProperty(BootstrapContext.STANDALONE).equals("true") ?
                SECTIONS_STANADLONE : SECTIONS;

        for (String[] section : sections) {
            final String name = section[0];
            final String id = "header-" + name;

            SafeHtmlBuilder html = new SafeHtmlBuilder();
            html.appendHtmlConstant("<div class='header-link-label'>");
            html.appendHtmlConstant("<span role='menuitem'>");
            html.appendHtmlConstant(section[1]);
            html.appendHtmlConstant("</span>");
            html.appendHtmlConstant("</div>");
            HTML widget = new HTML(html.toSafeHtml());
            widget.setStyleName("fill-layout");

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

        //subnavigation = createSubnavigation();
        //linksPane.add(subnavigation, "subnavigation");

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

                String td =  "<td style='"+styleAtt+"' id='" + id +"' class='"+styleClass+"'></td>";

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
        toggleSubnavigation(name);

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
                    {
                        element.addClassName("header-link-selected");
                        element.setAttribute("aria-selected", "true");
                    }
                    else {
                        element.removeClassName("header-link-selected");
                        element.setAttribute("aria-selected", "false");
                    }
                }
            }
        }

    }

    private void toggleSubnavigation(String name) {

    }

    public DeckPanel createSubnavigation() {

        DeckPanel subnavigation = new DeckPanel();

        // TODO: fill in contents

        return subnavigation;
    }
}
