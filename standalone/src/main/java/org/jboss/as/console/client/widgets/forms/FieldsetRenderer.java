package org.jboss.as.console.client.widgets.forms;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 3/3/11
 */
public class FieldsetRenderer implements GroupRenderer {

    String id;
    int numColumns;

    public FieldsetRenderer(int columns) {
        this.id = "formGroup_"+HTMLPanel.createUniqueId();
        this.numColumns = columns;
    }

    @Override
    public Widget render(String groupName, Map<String, FormItem> groupItems) {

        SafeHtmlBuilder builder = new SafeHtmlBuilder();

        builder.appendHtmlConstant("<fieldset id='"+id+"' class='default-fieldset'>");
        builder.appendHtmlConstant("<legend class='default-legend'>").appendEscaped(groupName).appendHtmlConstant("</legend>");
        builder.appendHtmlConstant("</fieldset>");

        HTMLPanel html = new HTMLPanel(builder.toSafeHtml());

        DefaultGroupRenderer defaultGroupRenderer = new DefaultGroupRenderer(numColumns);
        Widget defaultGroupWidget = defaultGroupRenderer.render("", groupItems);
        html.add(defaultGroupWidget,id);

        return html;
    }

    @Override
    public void setNumColumns(int cols) {
        this.numColumns = cols;
    }
}
