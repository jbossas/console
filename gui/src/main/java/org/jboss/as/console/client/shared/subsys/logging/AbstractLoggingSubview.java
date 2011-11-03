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
package org.jboss.as.console.client.shared.subsys.logging;

import com.google.gwt.user.client.ui.Widget;
import java.util.EnumSet;
import org.jboss.as.console.client.shared.subsys.logging.LoggingLevelProducer.LogLevelConsumer;
import org.jboss.as.console.client.shared.viewframework.AbstractEntityView;
import org.jboss.as.console.client.shared.viewframework.FrameworkButton;
import org.jboss.as.console.client.shared.viewframework.FrameworkView;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.ObservableFormItem;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;

/**
 * Abstract base class for sub-views.  This class knows how to set the log levels in the drop-downs.
 * 
 * @author Stan Silvert
 */
public abstract class AbstractLoggingSubview<T> extends AbstractEntityView<T> implements FrameworkView, LogLevelConsumer {

    protected ComboBoxItem levelItemForEdit;
    protected FormItem[] levelItemForAdd;

    public AbstractLoggingSubview(Class<?> beanType, ApplicationMetaData propertyMetaData) {
        this(beanType, propertyMetaData, EnumSet.noneOf(FrameworkButton.class));
    }
    
    public AbstractLoggingSubview(Class<?> type, ApplicationMetaData applicationMetaData, EnumSet<FrameworkButton> hideButtons) {
        super(type, applicationMetaData, hideButtons);
        levelItemForAdd = formMetaData.findAttribute("level").getFormItemForAdd();
    }

    @Override
    public Widget createWidget() {
        Widget widget = super.createEmbeddableWidget();
        widget.setHeight("1000px");
        return widget;
    }

    @Override
    public void itemAction(Action action, ObservableFormItem item) {
        if (item.getPropertyBinding().getJavaName().equals("level") && (action == Action.CREATED)) {
            levelItemForEdit = (ComboBoxItem) item.getWrapped();
        }
    }

    @Override
    public void setLogLevels(String[] logLevels) {
        levelItemForEdit.setValueMap(logLevels);
        FormItem itemForAdd = ((ObservableFormItem)this.levelItemForAdd[0]).getWrapped();
        ((ComboBoxItem)itemForAdd).setValueMap(logLevels);
    }
    
}
