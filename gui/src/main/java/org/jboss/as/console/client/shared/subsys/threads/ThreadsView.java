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
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;

import javax.inject.Inject;
import org.jboss.as.console.client.widgets.pages.PagedView;

/**
 * Main view class for the Threads subsystem.
 *
 * @author Stan Silvert
 */
public class ThreadsView extends SuspendableViewImpl implements ThreadsPresenter.MyView {

    private ThreadFactoryView threadFactoryView;
    private BoundedQueueThreadPoolView boundedQueuePoolView;
    private BlockingBoundedQueueThreadPoolView blockingBoundedQueuePoolView;
    private UnboundedQueueThreadPoolView unboundedQueuePoolView;
    private QueuelessThreadPoolView queuelessPoolView;
    private BlockingQueuelessThreadPoolView blockingQueuelessPoolView;
    private ScheduledThreadPoolView scheduledPoolView;

    @Inject
    public ThreadsView(ApplicationMetaData propertyMetaData, DispatchAsync dispatcher) {
        queuelessPoolView = new QueuelessThreadPoolView(propertyMetaData, dispatcher);
        blockingQueuelessPoolView = new BlockingQueuelessThreadPoolView(propertyMetaData, dispatcher);
        unboundedQueuePoolView = new UnboundedQueueThreadPoolView(propertyMetaData, dispatcher);
        boundedQueuePoolView = new BoundedQueueThreadPoolView(propertyMetaData, dispatcher);
        blockingBoundedQueuePoolView = new BlockingBoundedQueueThreadPoolView(propertyMetaData, dispatcher);
        scheduledPoolView = new ScheduledThreadPoolView(propertyMetaData, dispatcher);
        threadFactoryView = new ThreadFactoryView(propertyMetaData, dispatcher,
                queuelessPoolView, blockingQueuelessPoolView, unboundedQueuePoolView,
                boundedQueuePoolView, blockingBoundedQueuePoolView, scheduledPoolView);
    }

    @Override
    public Widget createWidget() {
        TabLayoutPanel tabLayoutPanel = new TabLayoutPanel(40, Style.Unit.PX);
        tabLayoutPanel.addStyleName("default-tabpanel");

        tabLayoutPanel.add(threadFactoryView.asWidget(), "Thread Factories");

        PagedView poolPages = new PagedView(true);
        poolPages.addPage("Queless", queuelessPoolView.asWidget());
        poolPages.addPage("Blocking Queless", blockingQueuelessPoolView.asWidget());
        poolPages.addPage("Unbounded", unboundedQueuePoolView.asWidget());
        poolPages.addPage("Bounded", boundedQueuePoolView.asWidget());
        poolPages.addPage("Blocking Bounded", blockingBoundedQueuePoolView.asWidget());
        poolPages.addPage("Scheduled", scheduledPoolView.asWidget());

        tabLayoutPanel.add(poolPages.asWidget(), "Thread Pools");

        tabLayoutPanel.selectTab(0);
        poolPages.showPage(0);

        return tabLayoutPanel;
    }

    public void initialLoad() {
        this.threadFactoryView.initialLoad();
        this.queuelessPoolView.initialLoad();
        this.blockingQueuelessPoolView.initialLoad();
        this.unboundedQueuePoolView.initialLoad();
        this.boundedQueuePoolView.initialLoad();
        this.blockingBoundedQueuePoolView.initialLoad();
        this.scheduledPoolView.initialLoad();
    }

}
