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

package org.jboss.as.console.client.shared.subsys.logging.refactored;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import javax.inject.Inject;
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
    
  //  private RootLoggerSubview rootLoggerSubview;
    private LoggerSubview loggerSubview;
    private ConsoleHandlerSubview consoleHandlerSubview;

    @Inject
    public LoggingView(ApplicationMetaData applicationMetaData, DispatchAsync dispatcher) {
        this.dispatcher = dispatcher;
        
       // rootLoggerSubview = new RootLoggerSubview(applicationMetaData, dispatcher);
        loggerSubview = new LoggerSubview(applicationMetaData, dispatcher);
        
        HandlerListManager handlerListManager = new HandlerListManager(loggerSubview);
        
        consoleHandlerSubview = new ConsoleHandlerSubview(applicationMetaData, dispatcher, handlerListManager);
        
        handlerListManager.setViewsOfAssignableHandlers(consoleHandlerSubview);
    }

    @Override
    public Widget createWidget() {
        TabLayoutPanel tabLayoutpanel = new TabLayoutPanel(25, Style.Unit.PX);
        tabLayoutpanel.addStyleName("default-tabpanel");
        
     //   tabLayoutpanel.add(rootLoggerSubview.asWidget(), rootLoggerSubview.getEntityDisplayName());
        tabLayoutpanel.add(loggerSubview.asWidget(), loggerSubview.getEntityDisplayName());
        tabLayoutpanel.add(consoleHandlerSubview.asWidget(), consoleHandlerSubview.getEntityDisplayName());
        
        LoggingLevelProducer.getLogLevels(dispatcher, loggerSubview, consoleHandlerSubview);
        
        return tabLayoutpanel;
    }
    
    public void initialLoad() {
     //   this.rootLoggerSubview.initialLoad();
        this.loggerSubview.initialLoad();
        this.consoleHandlerSubview.initialLoad();
    }
    
}
