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

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.TabPanel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jboss.as.console.client.widgets.forms.FormMetaData;
import org.jboss.as.console.client.widgets.forms.PropertyBinding;
import org.jboss.ballroom.client.widgets.forms.EditListener;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.FormValidation;

/**
 * Makes a Form with items automatically separated onto tabs.
 * 
 * Note that this class doesn't yet support grouped FormItems.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class TabbedFormLayoutPanel<T> extends TabPanel implements FormAdapter<T> {
    
    private Class<?> beanType;
    private FormMetaData formMetaData;
    private FormItemObserver[] observers;
    private Map<String, FormAdapter<T>> forms;
    private List<EditListener> listeners = new ArrayList<EditListener>();
    private FormAdapter<T> lastFormAdded;
    private List<String> formItemNames = new ArrayList<String>();
    
    public TabbedFormLayoutPanel(Class<?> beanType, FormMetaData formMetaData, FormItemObserver... observers) {
       // super(25, Style.Unit.PX);
        
        this.beanType = beanType;
        this.formMetaData = formMetaData;
        this.observers = observers;
        this.forms = makeForms();
        setStyleName("fill-layout-width");
        this.selectTab(0);
    }
    
    private Map<String, FormAdapter<T>> makeForms() {
        Map<String, FormAdapter<T>> formsMap = new LinkedHashMap<String, FormAdapter<T>>();
        for (Map.Entry<String, List<PropertyBinding>> entry : formMetaData.getTabbedAttributes().entrySet()) {
            FormAdapter<T> form = makeForm(entry.getValue());
            formsMap.put(entry.getKey(), form);
            add(form.asWidget(), entry.getKey()); // add form as a tab
            formItemNames.addAll(form.getFormItemNames());
            this.lastFormAdded = form;
        }
        return formsMap;
    }
    
    private FormAdapter<T> makeForm(List<PropertyBinding> bindings) {
        Form<T> form = new Form(beanType);
        
        if (bindings.size() < 5) {
            form.setNumColumns(1);
        } else {
            form.setNumColumns(2);
        }
        
        FormItem[][] items = new FormItem[bindings.size()][];
        int i=0;
        for (PropertyBinding propBinding : bindings) {
            items[i++] = propBinding.getFormItemForEdit(observers);
        }

        form.setFields(items);

        return form;
    }
    
    @Override
    public void edit(T bean) {
        for (FormAdapter<T> form : forms.values()) {
            form.edit(bean);
        }
        
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
    public void bind(CellTable<T> table) {
        for (FormAdapter<T> form : forms.values()) {
            form.bind(table);
        }
    }

    @Override
    public void cancel() {
        for (FormAdapter<T> form : forms.values()) {
            form.cancel();
        }
    }

    @Override
    public Map<String, Object> getChangedValues() {
        Map<String, Object> changedValues = new HashMap<String, Object>();
        for (FormAdapter<T> form : forms.values()) {
            changedValues.putAll(form.getChangedValues());
        }
        return changedValues;
    }

    @Override
    public Class<?> getConversionType() {
        return this.beanType;
    }

    @Override
    public T getEditedEntity() {
        return this.lastFormAdded.getEditedEntity();
    }

    @Override
    public List<String> getFormItemNames() {
        return this.formItemNames;
    }

    @Override
    public T getUpdatedEntity() {
        return this.lastFormAdded.getUpdatedEntity();
    }

    @Override
    public void removeEditListener(EditListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        for (FormAdapter form : forms.values()) {
            form.setEnabled(isEnabled);
        }
    }

    @Override
    public FormValidation validate() {
        FormValidation formValidation = new FormValidation();
        for (FormAdapter<T> form : forms.values()) {
            FormValidation tabValidation = form.validate();
            for (String error : tabValidation.getErrors()) {
                formValidation.addError(error);
            }
        }
        return formValidation;
    }
    
    
}
