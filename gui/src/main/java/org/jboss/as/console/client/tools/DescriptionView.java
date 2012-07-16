package org.jboss.as.console.client.tools;

import com.google.gwt.dom.client.Style;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 6/19/12
 */
public class DescriptionView {

    private HTML attributes;
    private HTML operations;
    private HTML children;
    private HTML header;

    Widget asWidget() {
        LayoutPanel layout = new LayoutPanel();
        layout.setStyleName("fill-layout");

        DisclosurePanel attributePanel = new DisclosurePanel("Attributes");
        attributePanel.setStyleName("fill-layout-width");
        DisclosurePanel operationsPanel = new DisclosurePanel("Operations");
        operationsPanel.setStyleName("fill-layout-width");
        DisclosurePanel childrenPanel = new DisclosurePanel("Children");
        childrenPanel.setStyleName("fill-layout-width");


        header = new HTML();
        header.setStyleName("fill-layout");
        header.getElement().setAttribute("style", "padding:10px");

        attributes = new HTML();
        attributes.setStyleName("fill-layout");
        attributes.getElement().setAttribute("style", "padding:10px");

        operations = new HTML();
        operations.setStyleName("fill-layout");
        operations.getElement().setAttribute("style", "padding:10px");

        children = new HTML();
        children.setStyleName("fill-layout");
        children.getElement().setAttribute("style", "padding:10px");


        VerticalPanel inner = new VerticalPanel();
        inner.setStyleName("fill-layout-width");
        inner.getElement().setAttribute("style", "padding:15px");

        attributePanel.add(attributes);
        operationsPanel.add(operations);
        childrenPanel.add(children);

        inner.add(header);
        inner.add(attributePanel);
        inner.add(operationsPanel);
        inner.add(childrenPanel);


        final ScrollPanel scroll = new ScrollPanel(inner);
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

        header.setHTML(builder.toSafeHtml());


        builder = new SafeHtmlBuilder();

        if(description.hasDefined("attributes"))
        {

            final List<Property> properties = description.get("attributes").asPropertyList();

            if(!properties.isEmpty())
            {
                builder.appendHtmlConstant("<table class='doc-table' cellpadding=5>");
                for(Property att : properties)
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
        }
        attributes.setHTML(builder.toSafeHtml());

        builder = new SafeHtmlBuilder();

        if(description.hasDefined("operations"))
        {

            final List<Property> properties = description.get("operations").asPropertyList();

            if(!properties.isEmpty())
            {
                builder.appendHtmlConstant("<table class='doc-table' cellpadding=5>");
                for(Property op : properties)
                {
                    builder.appendHtmlConstant("<tr>");
                    builder.appendHtmlConstant("<td class='doc-attribute'>").appendEscaped(op.getName()).appendHtmlConstant("</td>");
                    builder.appendHtmlConstant("<td>");

                    // parameters
                    if(op.getValue().hasDefined("request-properties"))
                    {
                        builder.appendHtmlConstant("<ul>");
                        for(Property param : op.getValue().get("request-properties").asPropertyList())
                        {
                            final ModelNode value = param.getValue();
                            builder.appendHtmlConstant("<li>").appendEscaped(param.getName()).appendEscaped(":");
                            builder.appendEscaped(value.get("type").asString());
                            if(value.hasDefined("required"))
                            {
                                String required = value.get("required").asBoolean() ? " (*)" : "";
                                builder.appendEscaped(required);
                            }
                        }
                        builder.appendHtmlConstant("<ul>");
                    }

                    builder.appendHtmlConstant("</td>");
                    builder.appendHtmlConstant("</tr>");

                    builder.appendHtmlConstant("<tr class='doc-table-description'>");
                    builder.appendHtmlConstant("<td colspan=2>").appendEscaped(op.getValue().get("description").asString()).appendHtmlConstant("</td>");
                    builder.appendHtmlConstant("</tr>");
                }
                builder.appendHtmlConstant("</table>");
            }
        }
        operations.setHTML(builder.toSafeHtml());

        builder = new SafeHtmlBuilder();

        if(description.hasDefined("children"))
        {
            final List<Property> properties = description.get("children").asPropertyList();

            if(!properties.isEmpty())
            {
                builder.appendHtmlConstant("<table class='doc-table' cellpadding=5>");
                for(Property child : properties)
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
        }

        children.setHTML(builder.toSafeHtml());
    }

    public DescriptionView() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void clearDisplay() {
        attributes.setHTML("");
        operations.setHTML("");
        children.setHTML("");
        header.setHTML("");
    }
}
