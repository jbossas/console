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
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import javax.inject.Inject;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;

/**
 * Main view class for the Logging subsystem.  
 * 
 * @author Stan Silvert
 */
public class LoggingView extends SuspendableViewImpl implements LoggingPresenter.MyView {

    private DispatchAsync dispatcher;
    
    private RootLoggerSubview rootLoggerSubview;
    private LoggerSubview loggerSubview;
    private ConsoleHandlerSubview consoleHandlerSubview;
    private FileHandlerSubview fileHandlerSubview;
    private PeriodicRotatingFileHandlerSubview periodicRotatingFileHandlerSubview;
    private SizeRotatingFileHandlerSubview sizeRotatingFileHandlerSubview;
    private AsyncHandlerSubview asyncHandlerSubview;
    private CustomHandlerSubview customHandlerSubview;

    @Inject
    public LoggingView(ApplicationMetaData applicationMetaData, DispatchAsync dispatcher) {
        this.dispatcher = dispatcher;
        
        HandlerListManager handlerListManager = new HandlerListManager();
                
        rootLoggerSubview = new RootLoggerSubview(applicationMetaData, dispatcher);
        loggerSubview = new LoggerSubview(applicationMetaData, dispatcher);
        
        consoleHandlerSubview = new ConsoleHandlerSubview(applicationMetaData, dispatcher, handlerListManager);
        fileHandlerSubview = new FileHandlerSubview(applicationMetaData, dispatcher, handlerListManager);
        periodicRotatingFileHandlerSubview = new PeriodicRotatingFileHandlerSubview(applicationMetaData, dispatcher, handlerListManager);
        sizeRotatingFileHandlerSubview = new SizeRotatingFileHandlerSubview(applicationMetaData, dispatcher, handlerListManager);
        asyncHandlerSubview = new AsyncHandlerSubview(applicationMetaData, dispatcher, handlerListManager);
        customHandlerSubview = new CustomHandlerSubview(applicationMetaData, dispatcher, handlerListManager);
        
        handlerListManager.setHandlerConsumers(rootLoggerSubview, loggerSubview, asyncHandlerSubview);
        handlerListManager.setHandlerProducers(consoleHandlerSubview, 
                                               fileHandlerSubview, 
                                               periodicRotatingFileHandlerSubview, 
                                               sizeRotatingFileHandlerSubview,
                                               asyncHandlerSubview,
                                               customHandlerSubview);
    }

    @Override
    public Widget createWidget() {
        TabLayoutPanel tabLayoutPanel = new TabLayoutPanel(25, Style.Unit.PX);
        tabLayoutPanel.addStyleName("default-tabpanel");
        
        TabPanel loggerPanel = new TabPanel();
        loggerPanel.add(rootLoggerSubview.asWidget(), rootLoggerSubview.getEntityDisplayName());
        loggerPanel.add(loggerSubview.asWidget(), loggerSubview.getEntityDisplayName());
        loggerPanel.setStyleName("fill-layout-width");
        loggerPanel.selectTab(0);
        
        
        TabPanel handlerPanel = new TabPanel();
        handlerPanel.add(consoleHandlerSubview.asWidget(), Console.CONSTANTS.subsys_logging_console());
        handlerPanel.add(fileHandlerSubview.asWidget(), Console.CONSTANTS.subsys_logging_file());
        handlerPanel.add(periodicRotatingFileHandlerSubview.asWidget(), Console.CONSTANTS.subsys_logging_periodic());
        handlerPanel.add(sizeRotatingFileHandlerSubview.asWidget(), Console.CONSTANTS.subsys_logging_size());
        handlerPanel.add(asyncHandlerSubview.asWidget(), Console.CONSTANTS.subsys_logging_async());
        handlerPanel.add(customHandlerSubview.asWidget(), Console.CONSTANTS.subsys_logging_custom());
        handlerPanel.setStyleName("fill-layout-width");
        handlerPanel.selectTab(0);
        
        tabLayoutPanel.add(loggerPanel.asWidget(), "Loggers");
        tabLayoutPanel.add(handlerPanel.asWidget(), "Handlers");
        
        LoggingLevelProducer.setLogLevels(dispatcher, rootLoggerSubview,
                                                      loggerSubview, 
                                                      consoleHandlerSubview, 
                                                      fileHandlerSubview, 
                                                      periodicRotatingFileHandlerSubview, 
                                                      sizeRotatingFileHandlerSubview,
                                                      asyncHandlerSubview,
                                                      customHandlerSubview);
        
        return tabLayoutPanel;
    }
    
    public void initialLoad() {
        rootLoggerSubview.initialLoad();
        loggerSubview.initialLoad();
        consoleHandlerSubview.initialLoad();
        fileHandlerSubview.initialLoad();
        periodicRotatingFileHandlerSubview.initialLoad(); 
        sizeRotatingFileHandlerSubview.initialLoad();
        asyncHandlerSubview.initialLoad();
        customHandlerSubview.initialLoad();
    }
    
}
