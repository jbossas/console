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
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.message.MessageBar;

import java.util.HashMap;
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

    public static final String[][] SECTIONS = {
            new String[]{NameTokens.ProfileMgmtPresenter, "Profiles"},
            new String[]{NameTokens.ServerGroupMgmtPresenter, "Server Groups"},
            new String[]{NameTokens.HostMgmtPresenter, "Hosts"}
    };

    public static final String[][] SECTIONS_STANADLONE = {
            new String[]{NameTokens.serverConfig, "Profile"},
             new String[]{NameTokens.DeploymentMgmtPresenter, "Deployments"}
    };

    private MessageBar messageBar;

    private Map<String,Widget> appLinks = new HashMap<String, Widget>();

    private LayoutPanel contentPanel;
    private BootstrapContext bootstrap;

    @Inject
    public Header(MessageBar messageBar, BootstrapContext bootstrap) {
        this.messageBar = messageBar;
        this.bootstrap = bootstrap;
        History.addValueChangeHandler(this);
    }

    public Widget asWidget() {

        LayoutPanel outerLayout = new LayoutPanel();
        contentPanel = new LayoutPanel();

        Widget logo = getLogoSection();
        Widget links = getLinksSection();

        LayoutPanel innerLayout = new LayoutPanel();
        innerLayout.setStyleName("header");
        //innerLayout.add(logo);
        innerLayout.add(links);

        //innerLayout.setWidgetLeftWidth(logo, 0, Style.Unit.PX, 50, Style.Unit.PX);
        innerLayout.setWidgetRightWidth(links, 10, Style.Unit.PX, 100, Style.Unit.PCT);
        innerLayout.setWidgetTopHeight(links, 0, Style.Unit.PX, 40, Style.Unit.PX);

        outerLayout.add(innerLayout);
        outerLayout.add(contentPanel);

        outerLayout.setWidgetTopHeight(innerLayout, 0, Style.Unit.PX, 40, Style.Unit.PX);
        outerLayout.setWidgetTopHeight(contentPanel , 34, Style.Unit.PX, 25, Style.Unit.PX);

        return outerLayout;
    }

    private Widget getLogoSection() {

        Image logo = new Image("images/header/product-framework.png", 0, 0, 30, 25);
        logo.setStyleName("header-logo");
        return logo;
    }

    private Widget getLinksSection() {
        linksPane = new HTMLPanel(createLinks());
        linksPane.getElement().setId("header-links-section");

        String[][] sections = bootstrap.hasProperty(BootstrapContext.STANDALONE) ? SECTIONS_STANADLONE : SECTIONS;
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
        return linksPane;
    }

    private String createLinks() {
        SafeHtmlBuilder headerString = new SafeHtmlBuilder();
        headerString.appendHtmlConstant("<table class='header-links' cellpadding=0 cellspacing=0 border=0>");
        headerString.appendHtmlConstant("<tr id='header-links-ref'>");

        headerString.appendHtmlConstant("<td style=\"width:1px;height:25px\"><img src=\"images/header/header_bg_line.png\"/></td>");
        String[][] sections = bootstrap.hasProperty(BootstrapContext.STANDALONE) ? SECTIONS_STANADLONE : SECTIONS;
        for (String[] section : sections) {

            final String name = section[0];
            final String id = "header-" + name;
            String styleClass = "header-link";
            String styleAtt = "vertical-align:middle; text-align:center";

            String td =  "<td style='"+styleAtt+"' width='100px' id='" + id +"'"+
                            " class='"+styleClass+"'></td>";
            headerString.appendHtmlConstant(td);
            //headerString.append(title);


            headerString.appendHtmlConstant("<td style=\"width: 1px;\"><img src=\"images/header/header_bg_line.png\"/></td>");
        }

        headerString.appendHtmlConstant("</tr></table>");

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
        NodeList<Node> childNodes = linksPane.getElementById("header-links-ref").getChildNodes();
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

    public void setContent(Widget content) {
        contentPanel.clear();
        contentPanel.add(content);
    }
}
