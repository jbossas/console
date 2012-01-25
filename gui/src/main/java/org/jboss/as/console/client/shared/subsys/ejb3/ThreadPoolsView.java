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

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.threads.UnboundedQueueThreadPoolView;
import org.jboss.as.console.client.shared.subsys.threads.model.UnboundedQueueThreadPool;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridge;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridgeImpl;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;

/**
 * @author David Bosschaert
 */
public class ThreadPoolsView extends UnboundedQueueThreadPoolView {
    private final EntityToDmrBridgeImpl<UnboundedQueueThreadPool> bridge;
    private EJB3Presenter presenter;

    public ThreadPoolsView(ApplicationMetaData propertyMetaData, DispatchAsync dispatcher) {
        super(propertyMetaData, dispatcher);
        bridge = new EntityToDmrBridgeImpl<UnboundedQueueThreadPool>(propertyMetaData, UnboundedQueueThreadPool.class, this, dispatcher);
    }

    @Override
    public Widget createWidget() {
        setDescription(Console.CONSTANTS.subsys_ejb3_threadpools_desc());

        return createEmbeddableWidget();
    }

    @Override
    public EntityToDmrBridge<UnboundedQueueThreadPool> getEntityBridge() {
        return bridge;
    }

    @Override
    protected String getEntityDisplayName() {
        return Console.CONSTANTS.subsys_ejb3_threadPools();
    }

    @Override
    public void refresh() {
        super.refresh();
        presenter.propagateThreadPoolNames(bridge.getEntityList());
    }

    public void setPresenter(EJB3Presenter presenter) {
        this.presenter = presenter;
    }
}
