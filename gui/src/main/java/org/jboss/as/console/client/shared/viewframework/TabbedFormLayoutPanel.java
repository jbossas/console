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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.widgets.forms.AddressBinding;
import org.jboss.as.console.client.widgets.forms.FormMetaData;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.as.console.client.widgets.forms.PropertyBinding;
import org.jboss.ballroom.client.widgets.forms.EditListener;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.FormValidation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Makes a Form with items automatically separated onto tabs.
 *
 * Note that this class doesn't yet support grouped FormItems.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class TabbedFormLayoutPanel<T> implements FormAdapter<T> {

    private Class<?> beanType;
    private FormMetaData formMetaData;
    private FormItemObserver[] observers;
    private Map<String, FormAdapter<T>> forms;
    private List<EditListener> listeners = new ArrayList<EditListener>();
    private FormAdapter<T> lastFormAdded;
    private List<String> formItemNames = new ArrayList<String>();
    private TabPanel tabPanel;

    private EntityToDmrBridge bridge;
    private AddressBinding address;

    public TabbedFormLayoutPanel(Class<?> beanType, FormMetaData formMetaData, FormItemObserver... observers) {

        this.beanType = beanType;
        this.formMetaData = formMetaData;
        this.observers = observers;
        this.tabPanel = new TabPanel();
        this.tabPanel.setStyleName("default-tabpanel");
        this.forms = makeForms();
    }

    public Widget asWidget() {

        // populate tabs
        Set<String> keys = forms.keySet();
        for(String key : keys) {

            VerticalPanel layout = new VerticalPanel();
            layout.setStyleName("fill-layout-width");

            final FormAdapter<T> form = forms.get(key);

            final FormToolStrip<T> toolStrip = new FormToolStrip<T>(
                    form,
                    new FormToolStrip.FormCallback<T>() {
                        @Override
                        public void onSave(Map<String, Object> changeset) {
                            bridge.onSaveDetails(form.getEditedEntity(), form.getChangedValues());
                        }

                        @Override
                        public void onDelete(T entity) {
                            bridge.onRemove(entity);
                        }
                    }
            );

            // belongs to top level tools
            toolStrip.providesDeleteOp(false);

            layout.add(toolStrip.asWidget());

            if (address != null) {
                layout.add(HelpWidgetFactory.makeHelpWidget(address, form));
            }

            layout.add(form);

            tabPanel.add(layout, key);
        }

        tabPanel.selectTab(0);

        return tabPanel;
    }

    private Map<String, FormAdapter<T>> makeForms() {
        Map<String, FormAdapter<T>> formsMap = new LinkedHashMap<String, FormAdapter<T>>();
        for (Map.Entry<String, List<PropertyBinding>> entry : formMetaData.getTabbedAttributes().entrySet()) {
            FormAdapter<T> form = makeForm(entry.getValue());
            formsMap.put(entry.getKey(), form);
            formItemNames.addAll(form.getFormItemNames());
            this.lastFormAdded = form;
        }
        return formsMap;
    }

    private FormAdapter<T> makeForm(List<PropertyBinding> bindings) {
        Form<T> form = new Form(beanType);

        if (bindings.size() < 3) {
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
        Set<String> keys = forms.keySet();
        for (String key : keys) {
            FormAdapter form = forms.get(key);
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


    public void add(Widget widget, String title) {
        tabPanel.add(widget, title);
    }

    public void setBridge(EntityToDmrBridge bridge) {
        this.bridge = bridge;
    }

    public void setHelpAddress(AddressBinding address) {
        this.address = address;
    }

    @Override
    public void clearValues() {
        Set<String> keys = forms.keySet();
        for (String key : keys) {
            FormAdapter form = forms.get(key);
            form.clearValues();
        }
    }
}
