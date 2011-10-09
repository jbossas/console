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

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.viewframework.FormItemObserver.Action;
import org.jboss.as.console.client.widgets.forms.PropertyBinding;
import org.jboss.ballroom.client.widgets.forms.FormItem;

/**
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class ObservableFormItem extends FormItem {

    private PropertyBinding propBinding;
    private FormItem wrapped;
    private FormItemObserver[] observers;
    
    public ObservableFormItem(PropertyBinding propBinding, FormItem wrapped, FormItemObserver... observers) {
        super(wrapped.getName(), wrapped.getTitle());
        this.wrapped = wrapped;
        this.propBinding = propBinding;
        this.observers = observers;
        notifyObservers(Action.CREATED);
    }
    
    private void notifyObservers(Action action) {
        for (FormItemObserver observer : observers) {
            observer.itemAction(action, this);
        }
    }
    
    public PropertyBinding getPropertyBinding() {
        return this.propBinding;
    }
    
    public FormItem getWrapped() {
        return wrapped;
    }
    
    @Override
    public String getErrMessage() {
        return wrapped.getErrMessage();
    }

    @Override
    public String getName() {
        return wrapped.getName();
    }

    @Override
    public String getTitle() {
        return wrapped.getTitle();
    }

    @Override
    public boolean isErroneous() {
        return wrapped.isErroneous();
    }

    @Override
    public boolean isModified() {
        return wrapped.isModified();
    }

    @Override
    public boolean isRequired() {
        return wrapped.isRequired();
    }

    @Override
    public boolean isUndefined() {
        return wrapped.isUndefined();
    }

    @Override
    public boolean render() {
        return wrapped.render();
    }

    @Override
    public void resetMetaData() {
        wrapped.resetMetaData();
    }

    @Override
    public void setErrMessage(String errMessage) {
        wrapped.setErrMessage(errMessage);
    }

    @Override
    public void setErroneous(boolean b) {
        wrapped.setErroneous(b);
    }

    @Override
    public void setRequired(boolean required) {
        wrapped.setRequired(required);
    }

    @Override
    public Widget asWidget() {
        return wrapped.asWidget();
    }

    @Override
    public void clearValue() {
        wrapped.clearValue();
        notifyObservers(Action.VALUE_CHANGED);
    }

    @Override
    public void setEnabled(boolean b) {
        wrapped.setEnabled(b);
    }

    @Override
    public boolean validate(Object value) {
        boolean result = wrapped.validate(value);
        notifyObservers(Action.VALIDATED);
        return result;
    }

    @Override
    public Object getValue() {
        return wrapped.getValue();
    }

    @Override
    public void setValue(Object value) {
        wrapped.setValue(value);
        notifyObservers(Action.VALUE_CHANGED);
    }
    
}
