package org.jboss.as.console.client.widgets.forms;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.autobean.shared.AutoBean;
import com.google.gwt.autobean.shared.AutoBeanCodex;
import com.google.gwt.autobean.shared.AutoBeanUtils;
import com.google.gwt.autobean.shared.AutoBeanVisitor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.BeanFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Form data binding that works on {@link AutoBean} entities.
 *
 * @author Heiko Braun
 * @date 2/21/11
 */
public class Form<T> {

    private Map<String, FormItem> formItems = new LinkedHashMap<String, FormItem>();
    private Map<String, Object> rememberedValues = new HashMap<String, Object>();

    private int numColumns = 1;

    private final String id = "form-"+ HTMLPanel.createUniqueId()+"_";
    private final String tablePrefix = "<table id='"+id+"' border=0 cellpadding=0 cellspacing=0>";
    private final static String tableSuffix = "</table>";

    private T editedBean;
    private Class<?> conversionType;

    BeanFactory factory = GWT.create(BeanFactory.class);

    public Form(Class<?> conversionType) {
        this.conversionType = conversionType;
    }

    /**
     * Number of layout columns.<br>
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

    public void edit(T bean) {

        this.editedBean = bean;

        AutoBean<T> autoBean = AutoBeanUtils.getAutoBean(bean);

        autoBean.accept(new AutoBeanVisitor() {
            @Override
            public boolean visitValueProperty(String propertyName, Object value, PropertyContext ctx) {
                FormItem matchingField = formItems.get(propertyName);
                if (matchingField != null) // not required to match
                {
                    matchingField.setValue(value);
                } else {
                    if (!"empty".equals(propertyName)) // empty is an autobean default property
                        Log.error("No matching field for '" + propertyName + "' (" + ctx.getType() + ")");
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
     * Get changed values since last {@link #rememberValues()}
     * @return
     */
    public Map<String, Object> getChangedValues() {

        //System.out.println("1 >" + AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(editedBean)).getPayload());

        HashMap<String, Object> values = new HashMap<String, Object>();
        snapshot(values);
        return values;
    }

    public T getUpdatedEntity() {

        Map<String, Object> values = getChangedValues();

        StringBuilder builder = new StringBuilder("{");

        int i=0;
        for(String property : values.keySet())
        {
            builder.append("\"");
            builder.append(property);
            builder.append("\"");

            builder.append(":");

            builder.append("\"");
            builder.append(values.get(property));
            builder.append("\"");

            if(i<values.size()-1)
                builder.append(", ");
            i++;

        }

        builder.append("}");

        //System.out.println("2 > " + builder.toString());

        AutoBean<?> decoded = AutoBeanCodex.decode(
                factory,
                conversionType,
                builder.toString()
        );

        /*System.out.println("> "+ decoded);
        decoded.accept(new AutoBeanVisitor() {
            @Override
            public void endVisitValueProperty(String propertyName, Object value, PropertyContext ctx) {
                System.out.println(propertyName+"->"+value);
            }
        });*/

        return (T) decoded.as();

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
        for(FormItem i : formItems.values())
        {
            i.setEnabled(b);
        }
    }

}
