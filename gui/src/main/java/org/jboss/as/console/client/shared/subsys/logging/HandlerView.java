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
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;

import javax.inject.Inject;

/**
 * Main view class for the Logging subsystem.
 * 
 * @author Stan Silvert
 */
public class HandlerView extends SuspendableViewImpl implements LogHandlerPresenter.MyView {

    private DispatchAsync dispatcher;

    private ConsoleHandlerSubview consoleHandlerSubview;
    private FileHandlerSubview fileHandlerSubview;
    private PeriodicRotatingFileHandlerSubview periodicRotatingFileHandlerSubview;
    private SizeRotatingFileHandlerSubview sizeRotatingFileHandlerSubview;
    private AsyncHandlerSubview asyncHandlerSubview;
    private CustomHandlerSubview customHandlerSubview;
    private LogHandlerPresenter presenter;


    @Inject
    public HandlerView(ApplicationMetaData applicationMetaData, DispatchAsync dispatcher) {
        this.dispatcher = dispatcher;
        
        HandlerListManager handlerListManager = new HandlerListManager();

        consoleHandlerSubview = new ConsoleHandlerSubview(applicationMetaData, dispatcher, handlerListManager);
        fileHandlerSubview = new FileHandlerSubview(applicationMetaData, dispatcher, handlerListManager);
        periodicRotatingFileHandlerSubview = new PeriodicRotatingFileHandlerSubview(applicationMetaData, dispatcher, handlerListManager);
        sizeRotatingFileHandlerSubview = new SizeRotatingFileHandlerSubview(applicationMetaData, dispatcher, handlerListManager);
        asyncHandlerSubview = new AsyncHandlerSubview(applicationMetaData, dispatcher, handlerListManager);
        customHandlerSubview = new CustomHandlerSubview(applicationMetaData, dispatcher, handlerListManager);
        
        //TODO: handlerListManager.setHandlerConsumers(rootLoggerSubview, loggerSubview, asyncHandlerSubview);
        handlerListManager.setHandlerProducers(consoleHandlerSubview, 
                                               fileHandlerSubview, 
                                               periodicRotatingFileHandlerSubview, 
                                               sizeRotatingFileHandlerSubview,
                                               asyncHandlerSubview,
                                               customHandlerSubview);
    }

    @Override
    public void setPresenter(LogHandlerPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {
        TabLayoutPanel tabLayoutPanel = new TabLayoutPanel(25, Style.Unit.PX);
        tabLayoutPanel.addStyleName("default-tabpanel");

        tabLayoutPanel.add(consoleHandlerSubview.asWidget(), Console.CONSTANTS.subsys_logging_console());
        tabLayoutPanel.add(fileHandlerSubview.asWidget(), Console.CONSTANTS.subsys_logging_file());
        tabLayoutPanel.add(periodicRotatingFileHandlerSubview.asWidget(), Console.CONSTANTS.subsys_logging_periodic());
        tabLayoutPanel.add(sizeRotatingFileHandlerSubview.asWidget(), Console.CONSTANTS.subsys_logging_size());
        tabLayoutPanel.add(asyncHandlerSubview.asWidget(), Console.CONSTANTS.subsys_logging_async());
        tabLayoutPanel.add(customHandlerSubview.asWidget(), Console.CONSTANTS.subsys_logging_custom());
        tabLayoutPanel.selectTab(0);


        LoggingLevelProducer.setLogLevels(
                dispatcher,
                consoleHandlerSubview,
                fileHandlerSubview,
                periodicRotatingFileHandlerSubview,
                sizeRotatingFileHandlerSubview,
                asyncHandlerSubview,
                customHandlerSubview
        );
        
        return tabLayoutPanel;
    }
    
    public void initialLoad() {
        consoleHandlerSubview.initialLoad();
        fileHandlerSubview.initialLoad();
        periodicRotatingFileHandlerSubview.initialLoad(); 
        sizeRotatingFileHandlerSubview.initialLoad();
        asyncHandlerSubview.initialLoad();
        customHandlerSubview.initialLoad();
    }
    
}
