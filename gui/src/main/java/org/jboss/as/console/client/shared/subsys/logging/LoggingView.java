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

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;

import javax.inject.Inject;

/**
 * Main view class for the Logging subsystem.  
 * 
 * @author Stan Silvert
 */
public class LoggingView extends SuspendableViewImpl implements LoggingPresenter.MyView {

    private DispatchAsync dispatcher;
    private RootLoggerSubview rootLoggerSubview;
    private LoggerSubview loggerSubview;

    @Inject
    public LoggingView(ApplicationMetaData applicationMetaData, DispatchAsync dispatcher, HandlerListManager handlerListManager) {
        this.dispatcher = dispatcher;

        rootLoggerSubview = new RootLoggerSubview(applicationMetaData, dispatcher);
        loggerSubview = new LoggerSubview(applicationMetaData, dispatcher);

        handlerListManager.addHandlerConsumers(rootLoggerSubview, loggerSubview);
    }

    @Override
    public Widget createWidget() {
        TabLayoutPanel tabLayoutPanel = new TabLayoutPanel(25, Style.Unit.PX);
        tabLayoutPanel.addStyleName("default-tabpanel");

        tabLayoutPanel.add(rootLoggerSubview.asWidget(), rootLoggerSubview.getEntityDisplayName());
        tabLayoutPanel.add(loggerSubview.asWidget(), loggerSubview.getEntityDisplayName());
        tabLayoutPanel.selectTab(0);

        LoggingLevelProducer.setLogLevels(
                dispatcher, rootLoggerSubview,
                loggerSubview
        );
        
        return tabLayoutPanel;
    }
    
    public void initialLoad() {
        rootLoggerSubview.initialLoad();
        loggerSubview.initialLoad();
    }
    
}
