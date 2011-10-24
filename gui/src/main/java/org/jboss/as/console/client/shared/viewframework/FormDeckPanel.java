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
package org.jboss.as.console.client.shared.viewframework;

import com.google.gwt.autobean.shared.AutoBean;
import com.google.gwt.autobean.shared.AutoBeanUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.ballroom.client.widgets.forms.EditListener;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.stack.NamedDeckPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A NamedDeckPanel that wraps one or more Forms.  The FormAdapter methods usually
 * delegate to the currently visible Form.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class FormDeckPanel<T> extends NamedDeckPanel implements FormAdapter<T> {
    
    private String triggerProperty;
    protected Map<String, FormAdapter<T>> forms;
    private List<EditListener> listeners = new ArrayList<EditListener>();
    protected String defaultForm;
    
    /**
     * Create a new FormDeckPanel.
     * 
     * @param triggerProperty The property whose value determines which form is displayed.
     */
    public FormDeckPanel(String triggerProperty) {
        this.triggerProperty = triggerProperty;
    }

    /**
     * Set the forms that back the FormDeckPanel
     * 
     * @param forms A Map that links each form to a value of the triggerProperty.
     * @param defaultForm Name of the default form to display if trigger property is not set.
     */
    public void setForms(Map<String, FormAdapter<T>> forms, String defaultForm) {
        this.forms = forms;
        this.defaultForm = defaultForm;
        
        for (Map.Entry<String, FormAdapter<T>> entry : forms.entrySet()) {
            add(entry.getKey(), entry.getValue().asWidget());
        }
    }
    
    public FormAdapter<T> getVisibleForm() {
        return forms.get(visibleWidgetName());
    }
    
    /**
     * Returns the form item names of the currently visible Form.
     * @return The names
     */
    @Override
    public List<String> getFormItemNames() {
        return getVisibleForm().getFormItemNames();
    }
    
    @Override
    public T getEditedEntity() {
        if (getVisibleForm() == null) return null;
        return getVisibleForm().getEditedEntity();
    }
    
    @Override
    public void setEnabled(boolean isEnabled) {
        for (FormAdapter form : forms.values()) {
            form.setEnabled(isEnabled);
        }
    }
    
    @Override
    public void bind(CellTable<T> table) {
        for (FormAdapter<T> form : forms.values()) {
            form.bind(table);
        }
        
        SingleSelectionModel<T> selectionModel = (SingleSelectionModel<T>)table.getSelectionModel();
        final SingleSelectionModel<T> finalSelectionModel = selectionModel;
        
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                edit(finalSelectionModel.getSelectedObject());
            }
        });
    }

    @Override
    public void cancel() {
        for (FormAdapter<T> form : forms.values()) {
            form.cancel();
        }
    }

    @Override
    public void edit(T bean) {
        for (FormAdapter<T> form : forms.values()) {
            form.edit(bean);
        }
        
        AutoBean autoBean = AutoBeanUtils.getAutoBean(bean);
        Map<String, Object> props = AutoBeanUtils.getAllProperties(autoBean);
        String triggerString = (String)props.get(this.triggerProperty);
        if (triggerString == null) triggerString = defaultForm;
        showWidget(triggerString);
        
        notifyListeners(bean);
    }
    
    protected void notifyListeners(T bean) {
        for (EditListener listener : listeners) {
            listener.editingBean(bean);
        }
    }
    
    @Override
    public void addEditListener(EditListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeEditListener(EditListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public Map<String, Object> getChangedValues() {
        return getVisibleForm().getChangedValues();
    }

    @Override
    public Class<?> getConversionType() {
        return getVisibleForm().getConversionType();
    }

    @Override
    public T getUpdatedEntity() {
        return getVisibleForm().getUpdatedEntity();
    }

    @Override
    public FormValidation validate() {
        return getVisibleForm().validate();
    }
}
