package org.jboss.as.console.client.tools;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.help.StaticHelpPanel;
import org.jboss.as.console.client.tools.mapping.DescriptionMapper;
import org.jboss.as.console.client.tools.mapping.RequestParameter;
import org.jboss.as.console.client.tools.mapping.ResponseParameter;
import org.jboss.dmr.client.ModelNode;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 6/19/12
 */
public class DescriptionView {

    private HTML attributes;
    private HTML operations;
    private HTML children;
    //private HTML header;

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");
        layout.getElement().setAttribute("style", "padding:15px");

        DisclosurePanel attributePanel = new DisclosurePanel("Attributes");
        attributePanel.setStyleName("fill-layout-width");
        DisclosurePanel operationsPanel = new DisclosurePanel("Operations");
        operationsPanel.setStyleName("fill-layout-width");
        DisclosurePanel childrenPanel = new DisclosurePanel("Children");
        childrenPanel.setStyleName("fill-layout-width");


        /*header = new HTML();
        header.setStyleName("fill-layout");
        header.getElement().setAttribute("style", "padding:10px");*/

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

        attributePanel.add(attributes);
        operationsPanel.add(operations);
        childrenPanel.add(children);

        //inner.add(header);


        SafeHtmlBuilder helpText = new SafeHtmlBuilder();
        helpText.appendHtmlConstant("<ul>");
        helpText.appendHtmlConstant("<li>").appendEscaped("(*): Required attribute");
        helpText.appendHtmlConstant("<li>").appendEscaped("+expression: Expressions are supported");
        helpText.appendHtmlConstant("<li>").appendEscaped("runtime: Runtime value");
        helpText.appendHtmlConstant("</ul>");
        StaticHelpPanel help = new StaticHelpPanel(helpText.toSafeHtml());
        inner.add(help.asWidget());

        inner.add(attributePanel);
        inner.add(operationsPanel);
        inner.add(childrenPanel);

        attributePanel.setOpen(true);

        layout.add(inner);
        return layout;
    }

    public void updateDescription(ModelNode address, ModelNode description)
    {
        /* SafeHtmlBuilder builder = new SafeHtmlBuilder();

        final List<Property> path = address.asPropertyList();
        StringBuffer sb = new StringBuffer();
        for(Property p : path)
        {
            sb.append("/").append(p.getName()).append("=").append(p.getValue().asString());
        }

        builder.appendHtmlConstant("<h1 class='doc-address'>")
                .appendEscaped(sb.toString())
                .appendHtmlConstant("</h1>");

        builder.appendHtmlConstant("<p class='content-description'>")
                .appendEscaped(description.get("description").asString())
                .appendHtmlConstant("</p>");

        header.setHTML(builder.toSafeHtml());*/


        DescriptionMapper mapper = new DescriptionMapper(address, description);

        mapper.map(new DescriptionMapper.Mapping() {

            SafeHtmlBuilder attributeBuilder = new SafeHtmlBuilder();
            SafeHtmlBuilder operationsBuilder = new SafeHtmlBuilder();
            SafeHtmlBuilder childrenBuilder = new SafeHtmlBuilder();

            @Override
            public void onAttribute(String name, String description, String type, boolean required, boolean expressions, boolean runtime) {

                attributeBuilder.appendHtmlConstant("<tr valign=top>");
                attributeBuilder.appendHtmlConstant("<td class='doc-attribute'>");
                attributeBuilder.appendEscaped(name);
                attributeBuilder.appendHtmlConstant("</td>");

                attributeBuilder.appendHtmlConstant("<td>");
                attributeBuilder.appendEscaped(type);
                String requiredSuffix = required ? " (*)" : "";
                attributeBuilder.appendEscaped(requiredSuffix);
                attributeBuilder.appendHtmlConstant("</td>");
                attributeBuilder.appendHtmlConstant("</tr>");

                attributeBuilder.appendHtmlConstant("<tr class='doc-table-description'>");
                attributeBuilder.appendHtmlConstant("<td width=70%>").appendEscaped(description).appendHtmlConstant("</td>");
                attributeBuilder.appendHtmlConstant("<td width=30% style='color:#cccccc'>");
                String expressionSuffix = expressions? " +expression" : "";
                attributeBuilder.appendEscaped(expressionSuffix);
                String runtimeSuffix = runtime? " runtime" : "";
                attributeBuilder.appendEscaped(runtimeSuffix);
                attributeBuilder.appendHtmlConstant("</td>");
                attributeBuilder.appendHtmlConstant("</tr>");

            }

            @Override
            public void onOperation(String name, String description, List<RequestParameter> parameter, ResponseParameter response) {

                operationsBuilder.appendHtmlConstant("<tr valign=top>");
                operationsBuilder.appendHtmlConstant("<td width=70%>");
                operationsBuilder.appendHtmlConstant("<span class='doc-attribute' style='margin-bottom:10px'>");
                operationsBuilder.appendEscaped(name).appendHtmlConstant("<br/>");
                operationsBuilder.appendHtmlConstant("</span>");
                operationsBuilder.appendEscaped(description);
                operationsBuilder.appendHtmlConstant("</td>");
                operationsBuilder.appendHtmlConstant("<td width=30%>");

                // -- inner

                operationsBuilder.appendHtmlConstant("<table border=0>");
                operationsBuilder.appendHtmlConstant("<tr valign=top>");
                operationsBuilder.appendHtmlConstant("<td class='doc-attribute'>");
                operationsBuilder.appendHtmlConstant("Parameter:");
                operationsBuilder.appendHtmlConstant("</td>");
                operationsBuilder.appendHtmlConstant("</tr>");
                // parameters
                for(RequestParameter param : parameter)
                {
                    operationsBuilder.appendHtmlConstant("<tr valign=top>");
                    operationsBuilder.appendHtmlConstant("<td style='white-space:nowrap'>");
                    operationsBuilder.appendEscaped(param.getParamName()).appendEscaped(": ");
                    operationsBuilder.appendEscaped(param.getParamType());
                    String required = param.isRequired() ? " (*)" : "";
                    operationsBuilder.appendEscaped(required);
                    operationsBuilder.appendHtmlConstant("</td>");
                    operationsBuilder.appendHtmlConstant("</tr>");
                }


                operationsBuilder.appendHtmlConstant("<tr valign=top>");
                operationsBuilder.appendHtmlConstant("<td class='doc-attribute'>");
                operationsBuilder.appendHtmlConstant("Response:");
                operationsBuilder.appendHtmlConstant("</td>");
                operationsBuilder.appendHtmlConstant("</tr>");

                operationsBuilder.appendHtmlConstant("<tr valign=top>");
                operationsBuilder.appendHtmlConstant("<td style='white-space:wrap'>");
                //operationsBuilder.appendEscaped(response.getReplyDesc()).appendEscaped(" ");
                operationsBuilder.appendEscaped(response.getReplyType());
                operationsBuilder.appendHtmlConstant("</td>");
                operationsBuilder.appendHtmlConstant("</tr>");


                operationsBuilder.appendHtmlConstant("</table>");


                // -- end inner

                operationsBuilder.appendHtmlConstant("</td>");
                operationsBuilder.appendHtmlConstant("</tr>");

              /*  operationsBuilder.appendHtmlConstant("<tr class='doc-table-description'>");
                operationsBuilder.appendHtmlConstant("<td width=70%>").appendEscaped(description).appendHtmlConstant("</td>");
                operationsBuilder.appendHtmlConstant("<td width=30%>").appendHtmlConstant("</td>");
                operationsBuilder.appendHtmlConstant("</tr>");
                */


            }

            @Override
            public void onChild(String name, String description) {

                childrenBuilder.appendHtmlConstant("<tr valign=top>");
                childrenBuilder.appendHtmlConstant("<td class='doc-child'>")
                        .appendEscaped(name)
                        .appendHtmlConstant("</td>");
                childrenBuilder.appendHtmlConstant("</tr>");

                childrenBuilder.appendHtmlConstant("<tr class='doc-table-description'>");
                childrenBuilder.appendHtmlConstant("<td colspan=2>")
                        .appendEscaped(description)
                        .appendHtmlConstant("</td>");
                childrenBuilder.appendHtmlConstant("</tr>");

            }

            @Override
            public void onBegin() {
                attributeBuilder.appendHtmlConstant("<table class='doc-table' cellpadding=5>");
                operationsBuilder.appendHtmlConstant("<table class='doc-table' cellpadding=5>");
                childrenBuilder.appendHtmlConstant("<table class='doc-table' cellpadding=5>");
            }

            @Override
            public void onFinish() {
                attributeBuilder.appendHtmlConstant("</table>");
                attributes.setHTML(attributeBuilder.toSafeHtml());

                operationsBuilder.appendHtmlConstant("</table>");
                operations.setHTML(operationsBuilder.toSafeHtml());

                childrenBuilder.appendHtmlConstant("</table>");
                children.setHTML(childrenBuilder.toSafeHtml());

            }
        });

    }

    public DescriptionView() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void clearDisplay() {
        attributes.setHTML("");
        operations.setHTML("");
        children.setHTML("");
        //header.setHTML("");
    }
}
