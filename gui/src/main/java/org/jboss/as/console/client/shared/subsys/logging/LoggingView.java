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
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DisposableViewImpl;


/**
 * @author Stan Silvert
 * @date 3/29/11
 */
public class LoggingView extends DisposableViewImpl implements LoggingPresenter.MyView {

    private LoggingPresenter presenter;
    private LoggerEditor loggingEditor;
    private HandlerEditor handlerEditor;

    @Override
    public Widget createWidget() {
        loggingEditor = new LoggerEditor(presenter);
        handlerEditor = new HandlerEditor(presenter);
        
        TabLayoutPanel tabLayoutpanel = new TabLayoutPanel(25, Style.Unit.PX);
        tabLayoutpanel.addStyleName("default-tabpanel");
        
        tabLayoutpanel.add(loggingEditor.asWidget(), "Logger");
        tabLayoutpanel.add(handlerEditor.asWidget(), Console.CONSTANTS.subsys_logging_handlers());

        return tabLayoutpanel;
    }

    @Override
    public void setPresenter(LoggingPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateLoggingInfo(LoggingInfo loggingInfo) {
        loggingEditor.updateLoggerConfigs(loggingInfo);
        handlerEditor.updateHandlers(loggingInfo);
    }
    
    @Override
    public void enableLoggerDetails(boolean isEnabled) {
        loggingEditor.enableLoggerDetails(isEnabled);
    }
    
    @Override
    public void enableHandlerDetails(boolean isEnabled) {
        handlerEditor.enableHandlerDetails(isEnabled);
    }
}
