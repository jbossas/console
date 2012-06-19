package org.jboss.as.console.client.tools;

import com.google.gwt.dom.client.Style;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

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

        builder.appendHtmlConstant("<h2>")
                .appendEscaped(description.get("description").asString())
                .appendHtmlConstant("</h2>");

        builder.appendHtmlConstant("<h3>Attributes</h3>");

        if(description.hasDefined("attributes"))
        {
            builder.appendHtmlConstant("<table style='border:1px solid grey; font-size:12px!important' width='90%' cellpadding=5>");
            for(Property att : description.get("attributes").asPropertyList())
            {
                builder.appendHtmlConstant("<tr>");
                builder.appendHtmlConstant("<td>").appendEscaped(att.getName()).appendHtmlConstant("</td>");
                builder.appendHtmlConstant("<td>").appendEscaped(att.getValue().get("type").asString()).appendHtmlConstant("</td>");
                builder.appendHtmlConstant("</tr>");

                builder.appendHtmlConstant("<tr>");
                builder.appendHtmlConstant("<td colspan=2 style='font-size:10px!important;padding-bottom:8px;border-bottom:1px solid grey'>").appendEscaped(att.getValue().get("description").asString()).appendHtmlConstant("</td>");
                builder.appendHtmlConstant("</tr>");
            }
            builder.appendHtmlConstant("</table>");
        }

        builder.appendHtmlConstant("<h3>Children</h3>");

        if(description.hasDefined("children"))
                {
                    builder.appendHtmlConstant("<table style='border:1px solid grey; font-size:12px' width='90%' cellpadding=5>");
                    for(Property child : description.get("children").asPropertyList())
                    {
                        builder.appendHtmlConstant("<tr>");
                        builder.appendHtmlConstant("<td>")
                                .appendEscaped(child.getName())
                                .appendHtmlConstant("</td>");
                        builder.appendHtmlConstant("</tr>");
                    }
                    builder.appendHtmlConstant("</table>");
                }

        html.setHTML(builder.toSafeHtml());
    }

    public void clearDisplay() {
        html.setHTML("");
    }
}
