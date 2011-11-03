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
import org.jboss.as.console.client.shared.subsys.logging.model.ConsoleHandler;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.logging.LoggingLevelProducer.LogLevelConsumer;
import org.jboss.as.console.client.shared.viewframework.FrameworkView;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.ObservableFormItem;

/**
 * Subview for Console Handlers.
 * 
 * @author Stan Silvert
 */
public class ConsoleHandlerSubview extends AbstractHandlerSubview<ConsoleHandler> implements FrameworkView, LogLevelConsumer, HandlerProducer {

    public ConsoleHandlerSubview(ApplicationMetaData applicationMetaData, 
                                 DispatchAsync dispatcher, 
                                 HandlerListManager handlerListManager) {
        super(ConsoleHandler.class, applicationMetaData, dispatcher, handlerListManager);
    }

    @Override
    public void itemAction(Action action, ObservableFormItem item) {
        super.itemAction(action, item);
        if (item.getPropertyBinding().getJavaName().equals("target") && (action == Action.CREATED)) {
            ComboBoxItem targetItem = (ComboBoxItem) item.getWrapped();
            targetItem.setValueMap(new String[] {"System.out", "System.err"});
        }
    }
    
    @Override
    protected String getEntityDisplayName() {
        return Console.CONSTANTS.subsys_logging_consoleHandlers();
    }
}
