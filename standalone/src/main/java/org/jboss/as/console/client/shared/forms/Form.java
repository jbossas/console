package org.jboss.as.console.client.shared.forms;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.util.DataClass;

import java.util.*;

/**
 * @author Heiko Braun
 * @date 2/21/11
 */
public class Form {
    private Map<String, FormItem> formItems = new LinkedHashMap<String, FormItem>();
    private List<ItemChangedHandler> itemChangedHandler= new ArrayList<ItemChangedHandler>();

    private Map<String, Object> rememberedValues = new HashMap<String, Object>();

    private int numColumns = 1;

    private final String id = "form-"+ HTMLPanel.createUniqueId()+"_";
    private final String tablePrefix = "<table id='"+id+"' border=0 cellpadding=0 cellspacing=0>";
    private final static String tableSuffix = "</table>";

    public Form() {

    }

    public void setNumColumns(int columns)
    {
        this.numColumns = columns;
    }

    public void setFields(FormItem... items) {
        for(FormItem item : items)
        {
            formItems.put(item.getName(), item);
        }
    }

    public void addItemChangedHandler(ItemChangedHandler handler)
    {
        this.itemChangedHandler.add(handler);
    }

    public void editRecord(DataClass record) {
        for(String att : record.getAttributes())
        {
            FormItem matchingField = formItems.get(att);
            if(matchingField!=null) // not required to match
            {
                matchingField.setValue(record.getAttribute(att));
            }
        }
    }

    public void rememberValues() {
        snapshot(rememberedValues);
    }

    private void snapshot(Map<String, Object> buffer) {
        buffer.clear();
        for(FormItem item : formItems.values())
        {
            buffer.put(item.getName(), item.getValue());
        }
    }

    public Widget asWidget() {
        return build();
    }

    private Widget build() {
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        builder.appendHtmlConstant(tablePrefix);

        // build html structure
        FormItem[] values = formItems.values().toArray(new FormItem[0]);
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
        for(FormItem item : formItems.values())
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

    public void setEnabled(boolean b) {

    }

    public Map<String, Object> getChangedValues() {

        // TOD: implement diff operation. Currently it simply returns all values
        HashMap<String, Object> values = new HashMap<String, Object>();
        snapshot(values);
        return values;
    }
}
