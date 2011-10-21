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
package org.jboss.as.console.client.shared.subsys.ejb3;

import java.util.EnumSet;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;

import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.ejb3.model.ThreadPoolEntity;
import org.jboss.as.console.client.shared.viewframework.AbstractSingleEntityView;
import org.jboss.as.console.client.shared.viewframework.FrameworkButton;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.ObservableFormItem;

/**
 * @author David Bosschaert
 */
public abstract class AbstractThreadPoolView<T extends ThreadPoolEntity> extends AbstractSingleEntityView<T> {
    private ComboBoxItem threadPoolItem;

    protected AbstractThreadPoolView(Class<? extends T> beanType, PropertyMetaData propertyMetaData,
            DispatchAsync dispatcher) {
        super(beanType, propertyMetaData, dispatcher, EnumSet.of(FrameworkButton.ADD, FrameworkButton.REMOVE));
    }

    @Override
    public Widget createWidget() {
        super.createWidget();
        return entityDetails.asWidget();
    }

    @Override
    public void itemAction(Action action, ObservableFormItem item) {
        if (action != Action.CREATED)
            return;

        String javaName = item.getPropertyBinding().getJavaName();
        if ("threadPoolName".equals(javaName))
            threadPoolItem = (ComboBoxItem) item.getWrapped();
    }

    public void setThreadPoolNames(List<String> threadPoolNames) {
        if (threadPoolItem != null)
            threadPoolItem.setValueMap(threadPoolNames);
    }
}
