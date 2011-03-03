package org.jboss.as.console.client.widgets.forms;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import java.util.Map;

/**
 * The default renderer for a group of form items.
 *
 * @see Form
 *
 * @author Heiko Braun
 * @date 3/3/11
 */
class DefaultGroupRenderer implements GroupRenderer
{
    private final String id = "form-"+ HTMLPanel.createUniqueId()+"_";
    private final String tablePrefix = "<table id='"+id+"' border=0 cellpadding=0 cellspacing=0>";
    private final static String tableSuffix = "</table>";

    private int numColumns = 1;

    DefaultGroupRenderer(int numColumns) {
        this.numColumns = numColumns;
    }

    @Override
    public Widget render(String groupName, Map<String, FormItem> groupItems) {
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        builder.appendHtmlConstant(tablePrefix);

        // build html structure
        FormItem[] values = groupItems.values().toArray(new FormItem[0]);
        int i=0;
        while(i<values.length)
        {
            builder.appendHtmlConstant("<tr>");

            int col=0;
            for(col=0; col<numColumns; col++)
            {
                int next = i + col;
                if(next<values.length)
                {
                    FormItem item = values[next];
                    createItemCell(builder, item);
                }
                else
                {
                    break;
                }
            }

            builder.appendHtmlConstant("</tr>");
            i+=col;
        }

        builder.appendHtmlConstant(tableSuffix);

        HTMLPanel panel = new HTMLPanel(builder.toSafeHtml());

        // inline widget
        for(FormItem item : groupItems.values())
        {
            final String widgetId = id + item.getName();
            panel.add(item.asWidget(), widgetId);
        }

        return panel;
    }

    private void createItemCell(SafeHtmlBuilder builder, FormItem item) {

        final String widgetId = id + item.getName();

        builder.appendHtmlConstant("<td align='right' class='form-item-title'>");
        builder.appendEscaped(item.getTitle()+":");
        builder.appendHtmlConstant("</td>");

        builder.appendHtmlConstant("<td id='" + widgetId + "' class='form-item'>").appendHtmlConstant("</td>");
        // contents added later
        builder.appendHtmlConstant("</td>");
    }

    @Override
    public void setNumColumns(int cols) {
        this.numColumns = cols;
    }
}
