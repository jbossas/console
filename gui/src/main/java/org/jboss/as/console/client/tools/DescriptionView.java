package org.jboss.as.console.client.tools;

import com.google.gwt.dom.client.Style;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 6/19/12
 */
public class DescriptionView {

    private HTML html;

    Widget asWidget() {
        LayoutPanel layout = new LayoutPanel();
        layout.setStyleName("fill-layout");


        html = new HTML();
        html.setStyleName("fill-layout");
        html.getElement().setAttribute("style", "padding:10px");

        final ScrollPanel scroll = new ScrollPanel(html);
        layout.add(scroll);

        layout.setWidgetTopHeight(scroll, 0, Style.Unit.PX, 100, Style.Unit.PCT);

        return layout;
    }

    public void updateDescription(ModelNode address, ModelNode description)
    {

        System.out.println(description);

        SafeHtmlBuilder builder = new SafeHtmlBuilder();


        final List<Property> path = address.asPropertyList();
        StringBuffer sb = new StringBuffer();
        for(Property p : path)
        {
            sb.append("/").append(p.getName()).append("=").append(p.getValue().asString());
        }

        builder.appendHtmlConstant("<h1 class='doc-address'>")
                       .appendEscaped(sb.toString())
                       .appendHtmlConstant("</h1>");

        builder.appendHtmlConstant("<h2 class='doc-description'>")
                .appendEscaped(description.get("description").asString())
                .appendHtmlConstant("</h2>");

        builder.appendHtmlConstant("<h3>Attributes</h3>");

        if(description.hasDefined("attributes"))
        {
            builder.appendHtmlConstant("<table class='doc-table' cellpadding=5>");
            for(Property att : description.get("attributes").asPropertyList())
            {
                builder.appendHtmlConstant("<tr>");
                builder.appendHtmlConstant("<td class='doc-attribute'>").appendEscaped(att.getName()).appendHtmlConstant("</td>");
                builder.appendHtmlConstant("<td>").appendEscaped(att.getValue().get("type").asString()).appendHtmlConstant("</td>");
                builder.appendHtmlConstant("</tr>");

                builder.appendHtmlConstant("<tr class='doc-table-description'>");
                builder.appendHtmlConstant("<td colspan=2>").appendEscaped(att.getValue().get("description").asString()).appendHtmlConstant("</td>");
                builder.appendHtmlConstant("</tr>");
            }
            builder.appendHtmlConstant("</table>");
        }

        builder.appendHtmlConstant("<h3>Children</h3>");

        if(description.hasDefined("children"))
                {
                    builder.appendHtmlConstant("<table class='doc-table' cellpadding=5>");
                    for(Property child : description.get("children").asPropertyList())
                    {
                        builder.appendHtmlConstant("<tr>");
                        builder.appendHtmlConstant("<td class='doc-child'>")
                                .appendEscaped(child.getName())
                                .appendHtmlConstant("</td>");
                        builder.appendHtmlConstant("</tr>");

                        builder.appendHtmlConstant("<tr class='doc-table-description'>");
                        builder.appendHtmlConstant("<td colspan=2>")
                                .appendEscaped(child.getValue().get("description").asString())
                                .appendHtmlConstant("</td>");
                        builder.appendHtmlConstant("</tr>");
                    }
                    builder.appendHtmlConstant("</table>");
                }

        html.setHTML(builder.toSafeHtml());
    }

    public DescriptionView() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void clearDisplay() {
        html.setHTML("");
    }
}
