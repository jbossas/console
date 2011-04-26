/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jboss.as.console.client.widgets.forms;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.autobean.shared.AutoBean;
import com.google.gwt.autobean.shared.AutoBeanCodex;
import com.google.gwt.autobean.shared.AutoBeanUtils;
import com.google.gwt.autobean.shared.AutoBeanVisitor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.shared.BeanFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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

    private int numColumns = 1;

    private Map<String,GroupRenderer> registeredRenderer = new HashMap<String, GroupRenderer>();

    private Class<?> conversionType;

    private static BeanFactory factory = GWT.create(BeanFactory.class);

    private int nextId = 1;
    private T editedEntity;

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

    int maxTitleLength = 0;
    public void setFieldsInGroup(String group, FormItem... items) {

        // create new group
        LinkedHashMap<String, FormItem> groupItems = new LinkedHashMap<String, FormItem>();
        formItems.put(group, groupItems);

        for(FormItem item : items)
        {
            String title = item.getTitle();
            if(title.length()>maxTitleLength)
            {
                maxTitleLength = title.length();
            }

            // key maybe be used multiple times
            String itemKey = item.getName();

            if(groupItems.containsKey(itemKey)) {
                groupItems.put(itemKey+"#"+nextId, item);
                nextId++;
            }
            else
            {
                groupItems.put(itemKey, item);
            }
        }
    }

    public void setFieldsInGroup(String group, GroupRenderer renderer, FormItem... items) {

        registeredRenderer.put(group, renderer);

        setFieldsInGroup(group, items);
    }

    public void edit(T bean) {

        AutoBean<T> autoBean = AutoBeanUtils.getAutoBean(bean);

        this.editedEntity = bean;

        if(null==autoBean)
            throw new IllegalArgumentException("Not an auto bean: " + bean.getClass());

        autoBean.accept(new AutoBeanVisitor() {

            private boolean isComplex = false;

            @Override
            public boolean visitValueProperty(String propertyName, Object value, PropertyContext ctx) {

                if(isComplex) return true; // skip complex types

                FormItem matchingField = null;

                for(Map<String, FormItem> groupItems : formItems.values())
                {
                    for(String key : groupItems.keySet()) // keys maybe used multiple times
                    {
                        if(key.startsWith(propertyName))
                        {
                            matchingField = groupItems.get(key);
                            matchingField.setValue(value);
                        }
                    }
                }

                if (null==matchingField && !"empty".equals(propertyName))
                    Log.warn("No matching field for '" + propertyName + "' (" + ctx.getType() + ")");

                return true;
            }


            @Override
            public void endVisitReferenceProperty(String propertyName, AutoBean<?> value, PropertyContext ctx) {
                //System.out.println("end reference "+propertyName);
                isComplex = false;
            }

            @Override
            public boolean visitReferenceProperty(String propertyName, AutoBean<?> value, PropertyContext ctx) {
                isComplex = true;
                //System.out.println("begin reference "+propertyName+ ": "+ctx.getType());
                return true;
            }
        });
    }

    /**
     * Get changed values since last {@link #edit(Object)} ()}
     * @return
     */
    public Map<String, Object> getChangedValues() {

        final T updatedEntity = getUpdatedEntity();
        final T editedEntity = getEditedEntity();

        Map<String, Object> diff = AutoBeanUtils.diff(
                AutoBeanUtils.getAutoBean(editedEntity),
                AutoBeanUtils.getAutoBean(updatedEntity)
        );

        return diff;
    }

    public FormValidation validate()
    {
        FormValidation outcome = new FormValidation();
        for(Map<String, FormItem> groupItems : formItems.values())
        {
            int i=0;
            for(FormItem item : groupItems.values())
            {
                if(! item.validate(item.getValue()) )
                {
                    outcome.addError(item.getName());
                    item.setErroneous(true);
                }
                else
                {
                    item.setErroneous(false);
                }
            }
        }

        return outcome;
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

        AutoBean<?> decoded = AutoBeanCodex.decode(
                factory,
                conversionType,
                builder.toString()
        );

        return (T) decoded.as();

    }

    public Widget asWidget() {
        return build();
    }

    private Widget build() {
        VerticalPanel parentPanel = new VerticalPanel();
        parentPanel.setStyleName("fill-layout-width");

        RenderMetaData metaData = new RenderMetaData();
        metaData.setNumColumns(numColumns);
        metaData.setTitleWidth(maxTitleLength);

        for(String group : formItems.keySet())
        {
            Map<String, FormItem> groupItems = formItems.get(group);
            if(DEFAULT_GROUP.equals(group))
            {
                DefaultGroupRenderer defaultGroupRenderer = new DefaultGroupRenderer();

                Widget defaultGroupWidget = defaultGroupRenderer.render(metaData,DEFAULT_GROUP, groupItems);
                parentPanel.add(defaultGroupWidget);
            }
            else
            {
                GroupRenderer groupRenderer = registeredRenderer.get(group)!=null ?
                        registeredRenderer.get(group) : new FieldsetRenderer();

                Widget widget = groupRenderer.render(metaData, group, groupItems);
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

    /**
     * Binds a default single selection model to the table
     * that displays selected rows in a form.
     *
     * @param instanceTable
     */
    public void bind(CellTable<T> instanceTable) {
        final SingleSelectionModel<T> selectionModel = new SingleSelectionModel<T>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                edit(selectionModel.getSelectedObject());
            }
        });
        instanceTable.setSelectionModel(selectionModel);
    }

    public T getEditedEntity() {
        return editedEntity;
    }
}
