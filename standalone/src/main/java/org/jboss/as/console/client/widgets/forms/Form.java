package org.jboss.as.console.client.widgets.forms;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.autobean.shared.AutoBean;
import com.google.gwt.autobean.shared.AutoBeanCodex;
import com.google.gwt.autobean.shared.AutoBeanUtils;
import com.google.gwt.autobean.shared.AutoBeanVisitor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.VerticalPanel;
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

    private final static String DEFAULT_GROUP = "default";

    private Map<String, Map<String, FormItem>> formItems = new LinkedHashMap<String, Map<String, FormItem>>();
    private Map<String, Object> rememberedValues = new HashMap<String, Object>();

    private int numColumns = 1;

    private T editedBean;

    private Map<String,GroupRenderer> registeredRenderer = new HashMap<String, GroupRenderer>();

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
        setFieldsInGroup(DEFAULT_GROUP, items);
    }

    public void setFieldsInGroup(String group, FormItem... items) {

        // create new group
        LinkedHashMap<String, FormItem> groupItems = new LinkedHashMap<String, FormItem>();
        formItems.put(group, groupItems);

        for(FormItem item : items)
        {
            groupItems.put(item.getName(), item);
        }
    }

    public void setFieldsInGroup(String group, GroupRenderer renderer, FormItem... items) {

        registeredRenderer.put(group, renderer);

        setFieldsInGroup(group, items);
    }

    public void edit(T bean) {

        this.editedBean = bean;

        AutoBean<T> autoBean = AutoBeanUtils.getAutoBean(bean);

        autoBean.accept(new AutoBeanVisitor() {
            @Override
            public boolean visitValueProperty(String propertyName, Object value, PropertyContext ctx) {

                FormItem matchingField = null;

                for(Map<String, FormItem> groupItems : formItems.values())
                {
                    matchingField = groupItems.get(propertyName);
                    if (matchingField != null)
                        matchingField.setValue(value);

                }

                if (null==matchingField && !"empty".equals(propertyName))
                    Log.warn("No matching field for '" + propertyName + "' (" + ctx.getType() + ")");

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


        StringBuilder builder = new StringBuilder("{");
        int g=0;
        for(Map<String, FormItem> groupItems : formItems.values())
        {
            int i=0;
            for(FormItem item : groupItems.values())
            {

                builder.append("\"");
                builder.append(item.getName());
                builder.append("\"");

                builder.append(":");

                builder.append("\"");
                builder.append(item.getValue());
                builder.append("\"");

                if(i<groupItems.size()-1)
                    builder.append(", ");
                i++;

            }

            if(g<formItems.size()-1)
                    builder.append(", ");

            g++;
        }
        builder.append("}");

        //System.out.println("JSON > " + builder.toString());

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

        for(Map<String, FormItem> groupItems : formItems.values())
        {
            for(FormItem item : groupItems.values())
            {
                buffer.put(item.getName(), item.getValue());
            }
        }
    }

    public Widget asWidget() {
        return build();
    }

    private Widget build() {
        VerticalPanel parentPanel = new VerticalPanel();
        parentPanel.setStyleName("fill-layout-width");

        for(String group : formItems.keySet())
        {
            Map<String, FormItem> groupItems = formItems.get(group);
            if(DEFAULT_GROUP.equals(group))
            {
                DefaultGroupRenderer defaultGroupRenderer = new DefaultGroupRenderer(numColumns);
                Widget defaultGroupWidget = defaultGroupRenderer.render(DEFAULT_GROUP, groupItems);
                parentPanel.add(defaultGroupWidget);
            }
            else
            {
                GroupRenderer groupRenderer = registeredRenderer.get(group)!=null ?
                        registeredRenderer.get(group) : new FieldsetRenderer(numColumns);
                groupRenderer.setNumColumns(numColumns);

                Widget widget = groupRenderer.render(group, groupItems);
                parentPanel.add(widget);
            }
        }

        return parentPanel;
    }

    /**
     * Enable/disable this form.
     *
     * @param b
     */
    public void setEnabled(boolean b) {
        for(Map<String, FormItem> groupItems : formItems.values())
        {
            for(FormItem item : groupItems.values())
            {
                item.setEnabled(b);
            }
        }
    }

}
