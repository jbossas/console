package org.jboss.as.console.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import org.jboss.as.console.client.components.ViewName;
import org.jboss.as.console.client.util.message.MessageBar;

/**
 * Top level header, gives access to main applications.
 *
 * @author Heiko Braun
 * @date 1/28/11
 */
public class Header implements ValueChangeHandler<String> {

    private String currentlySelectedSection = "config/Server/Subsystems";
    private HTMLPanel linksPane;
    private String currentHighlightedSection = null;

    public static final ViewName[] SECTIONS = {
            new ViewName("system", "System Overview"),
            new ViewName("server", "Server Management"),  // or domain management
            new ViewName("users", "User Management"),
            new ViewName("settings", "Settings")
    };

    private MessageBar messageBar;

    @Inject
    public Header(MessageBar messageBar) {
        this.messageBar = messageBar;
        History.addValueChangeHandler(this);
    }

    public Widget asWidget() {

        VLayout outerLayout = new VLayout();
        outerLayout.setWidth100();
        outerLayout.setMembersMargin(0);
        outerLayout.setHeight(60);

        final HLayout innerLayout = new HLayout();
        ToolStrip topStrip = new ToolStrip();
        topStrip.setHeight(34);
        topStrip.setWidth100();
        topStrip.setMembersMargin(20);
        topStrip.addMember(getLogoSection());
        topStrip.addMember(getLinksSection());

        innerLayout.addMember(topStrip);

        outerLayout.addMember(innerLayout);
        outerLayout.addMember(messageBar);

        return outerLayout;
    }

    private Canvas getLogoSection() {

        Img logo = new Img("header/product-framework.png", 30, 25);
        logo.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {

            }
        });

        logo.setStyleName("header-logo");

        return logo;
    }

    private Widget getLinksSection() {
        linksPane = new HTMLPanel(setupLinks());
        return linksPane;
    }

    private String setupLinks() {
        StringBuilder headerString = new StringBuilder(
            "<table style=\"height: 34px;\" cellpadding=\"0\" cellspacing=\"0\"><tr id='header-links'>");

        headerString.append("<td style=\"width: 1px;\"><img src=\"images/header/header_bg_line.png\"/></td>");
        for (ViewName section : SECTIONS) {

            final String id = "header-" + section.getName();

            String styleClass = "TopSectionLink";
            if (section.getName().equals(currentlySelectedSection)) {
                styleClass = "TopSectionLinkSelected";
            }

            // Set explicit identifiers because the generated scLocator is not getting picked up by Selenium.
            headerString.append("<td style=\"vertical-align:middle\" id=\"" + id +"\"").append(section).append("\" class=\"")
                .append(styleClass).append("\" onclick=\"document.location='#").append(section).append("'\" >");
            headerString.append(section.getTitle());
            headerString.append("</td>\n");

            headerString.append("<td style=\"width: 1px;\"><img src=\"images/header/header_bg_line.png\"/></td>");
        }

        headerString.append("</tr></table>");

        return headerString.toString();
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

    private void highlight(String name)
    {
        NodeList<Node> childNodes = linksPane.getElementById("header-links").getChildNodes();
        for(int i=0; i<childNodes.getLength(); i++)
        {
            Node n = childNodes.getItem(i);
            if(Node.ELEMENT_NODE == n.getNodeType())
            {
                Element element = (Element) n;
                if(element.getId().equals("header-"+name))
                    element.addClassName("TopSectionLinkSelected");
                else
                    element.removeClassName("TopSectionLinkSelected");
            }
        }
    }
}
