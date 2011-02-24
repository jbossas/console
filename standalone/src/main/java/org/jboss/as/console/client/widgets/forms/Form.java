package org.jboss.as.console.client.widgets.forms;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.autobean.shared.AutoBean;
import com.google.gwt.autobean.shared.AutoBeanVisitor;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.*;

/**
 * Form data binding that works on {@link AutoBean} entities.
 *
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

    /**
     * Number of layot columns.<br>
     * Form fields will fill columns in the order they have been specified
     * in {@link #setFields(FormItem[])}.
     *
     * @param columns
     */
    public void setNumColumns(int columns)
    {
        this.numColumns = columns;
    }

    /**
     * Specify the form fields.
     * Needs to be called before {@link #asWidget()}.
     *
     * @param items
     */
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

    public void editRecord(AutoBean<?> record) {

        record.accept(new AutoBeanVisitor() {
            @Override
            public boolean visitValueProperty(String propertyName, Object value, PropertyContext ctx) {
                FormItem matchingField = formItems.get(propertyName);
                if(matchingField!=null) // not required to match
                {
                    matchingField.setValue(value);
                }
                else
                {
                    if(!"empty".equals(propertyName)) // empty is an autobean default property
                        Log.error("No matching field for '"+propertyName+"' ("+ctx.getType()+")");
                }
                return true;
            }
        });
    }

    /**
     * Take a value snapshot for later comparison
     *
     * @see #getChangedValues()
     */
    public void rememberValues() {
        snapshot(rememberedValues);
    }

    /**
     * Get changed values sine last  {@link #rememberValues()}
     * @return
     */
    public Map<String, Object> getChangedValues() {

        // TODO: implement diff operation. Currently it simply returns all values
        // Take a look at AutoBeanUtils#diff()

        HashMap<String, Object> values = new HashMap<String, Object>();
        snapshot(values);
        return values;
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

    /**
     * Enable/disable this form.
     *
     * @param b
     */
    public void setEnabled(boolean b) {

    }
}
