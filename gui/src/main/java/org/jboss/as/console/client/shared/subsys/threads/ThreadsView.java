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

package org.jboss.as.console.client.shared.subsys.threads;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import javax.inject.Inject;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.threads.model.BoundedQueueThreadPool;
import org.jboss.as.console.client.shared.viewframework.AbstractEntityView;
import org.jboss.as.console.client.shared.viewframework.Columns.NameColumn;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridgeImpl;
import org.jboss.as.console.client.shared.viewframework.FrameworkView;
import org.jboss.as.console.client.widgets.forms.FormMetaData;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridge;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

/**
 * Main view class for Deployment Scanners.  This class assembles the editor and reacts to 
 * FrameworkView callbacks.
 * 
 * @author Stan Silvert
 */
public class ThreadsView extends SuspendableViewImpl implements ThreadsPresenter.MyView {

    private BoundedQueueThreadPoolView boundedQueueView;
    private BoundedQueueThreadPoolView boundedQueueView2;

    @Inject
    public ThreadsView(PropertyMetaData propertyMetaData, DispatchAsync dispatcher) {
        boundedQueueView = new BoundedQueueThreadPoolView(propertyMetaData, dispatcher) {
            @Override
            public Widget createWidget() {
                entityEditor = makeEntityEditor();
                return entityEditor.asWidget();
            }
        };
        
        // TODO: replace with another type of pool view
        boundedQueueView2 = new BoundedQueueThreadPoolView(propertyMetaData, dispatcher) {
            @Override
            public Widget createWidget() {
                entityEditor = makeEntityEditor();
                return entityEditor.asWidget();
            }
        };
    }

    @Override
    public Widget createWidget() {
        TabLayoutPanel tabLayoutpanel = new TabLayoutPanel(25, Style.Unit.PX);
        tabLayoutpanel.addStyleName("default-tabpanel");
        
        tabLayoutpanel.add(boundedQueueView.asWidget(), boundedQueueView.getPluralEntityName());
        tabLayoutpanel.add(boundedQueueView2.asWidget(), boundedQueueView.getPluralEntityName());
        return tabLayoutpanel;
    }
    
    public void initialLoad() {
        this.boundedQueueView.initialLoad();
        this.boundedQueueView2.initialLoad();
    }
  
}
