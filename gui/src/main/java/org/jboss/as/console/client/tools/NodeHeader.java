package org.jboss.as.console.client.tools;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 8/2/12
 */
public class NodeHeader {

    private HTML header;

    Widget asWidget() {

        header = new HTML();
        ScrollPanel scroll = new ScrollPanel(header);
        return scroll;
    }

    public void updateDescription(ModelNode address, ModelNode description) {
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

        builder.appendHtmlConstant("<p class='content-description'>")
                .appendEscaped(description.get("description").asString())
                .appendHtmlConstant("</p>");

        header.setHTML(builder.toSafeHtml());
    }

    public void clearDisplay() {
        header.setHTML("<h1 class='doc-address'>Please select a value node</h1>");
    }
}
