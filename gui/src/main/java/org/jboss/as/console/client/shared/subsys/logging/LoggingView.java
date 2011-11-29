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
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;

import javax.inject.Inject;

/**
 * Main view class for the Logging subsystem.  
 * 
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
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

    private DeckPanel deck;
    private int page = 0;


    @Inject
    public LoggingView(ApplicationMetaData applicationMetaData, DispatchAsync dispatcher, HandlerListManager handlerListManager) {
        this.dispatcher = dispatcher;

        rootLoggerSubview = new RootLoggerSubview(applicationMetaData, dispatcher);
        loggerSubview = new LoggerSubview(applicationMetaData, dispatcher);

        consoleHandlerSubview = new ConsoleHandlerSubview(applicationMetaData, dispatcher, handlerListManager);
        fileHandlerSubview = new FileHandlerSubview(applicationMetaData, dispatcher, handlerListManager);
        periodicRotatingFileHandlerSubview = new PeriodicRotatingFileHandlerSubview(applicationMetaData, dispatcher, handlerListManager);
        sizeRotatingFileHandlerSubview = new SizeRotatingFileHandlerSubview(applicationMetaData, dispatcher, handlerListManager);
        asyncHandlerSubview = new AsyncHandlerSubview(applicationMetaData, dispatcher, handlerListManager);
        customHandlerSubview = new CustomHandlerSubview(applicationMetaData, dispatcher, handlerListManager);

        handlerListManager.addHandlerConsumers(rootLoggerSubview, loggerSubview, asyncHandlerSubview);
        handlerListManager.addHandlerProducers(consoleHandlerSubview,
                fileHandlerSubview,
                periodicRotatingFileHandlerSubview,
                sizeRotatingFileHandlerSubview,
                asyncHandlerSubview,
                customHandlerSubview);
    }

    @Override
    public Widget createWidget() {

        deck = new DeckPanel();
        deck.setStyleName("fill-layout");

        TabLayoutPanel loggersTabs = new TabLayoutPanel(25, Style.Unit.PX);
        loggersTabs.addStyleName("default-tabpanel");

        loggersTabs.add(rootLoggerSubview.asWidget(), rootLoggerSubview.getEntityDisplayName());
        loggersTabs.add(loggerSubview.asWidget(), loggerSubview.getEntityDisplayName());
        loggersTabs.selectTab(0);


        TabLayoutPanel handlersTabs = new TabLayoutPanel(25, Style.Unit.PX);
        handlersTabs.addStyleName("default-tabpanel");

        handlersTabs.add(consoleHandlerSubview.asWidget(), Console.CONSTANTS.subsys_logging_console());
        handlersTabs.add(fileHandlerSubview.asWidget(), Console.CONSTANTS.subsys_logging_file());
        handlersTabs.add(periodicRotatingFileHandlerSubview.asWidget(), Console.CONSTANTS.subsys_logging_periodic());
        handlersTabs.add(sizeRotatingFileHandlerSubview.asWidget(), Console.CONSTANTS.subsys_logging_size());
        handlersTabs.add(asyncHandlerSubview.asWidget(), Console.CONSTANTS.subsys_logging_async());
        handlersTabs.add(customHandlerSubview.asWidget(), Console.CONSTANTS.subsys_logging_custom());
        handlersTabs.selectTab(0);

        deck.add(loggersTabs);
        deck.add(handlersTabs);

        // default
        deck.showWidget(page);

        LoggingLevelProducer.setLogLevels(
                dispatcher, rootLoggerSubview,
                consoleHandlerSubview,
                loggerSubview,
                fileHandlerSubview,
                periodicRotatingFileHandlerSubview,
                sizeRotatingFileHandlerSubview,
                asyncHandlerSubview,
                customHandlerSubview
        );


        return deck;
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

    @Override
    public void setPage(int page) {
        if(deck!=null)
            deck.showWidget(page);

        this.page = page;
    }
}
