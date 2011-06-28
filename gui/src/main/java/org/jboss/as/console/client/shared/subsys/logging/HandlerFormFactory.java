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

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.widgets.forms.ComboBoxItem;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.StatusItem;
import org.jboss.as.console.client.widgets.forms.TextItem;

/**
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class HandlerFormFactory<LoggingHandler> implements LoggingEntityFormFactory<LoggingHandler> {

    private BeanFactory factory;
    private Class<?> conversionType;
    
    public HandlerFormFactory(BeanFactory factory, Class<?> conversionType) {
        this.factory = factory;
        this.conversionType = conversionType;
    }
    
    @Override
    public Form<LoggingHandler> makeAddForm() {
        return makeEditForm();
    }

    @Override
    public Form<LoggingHandler> makeEditForm() {
        TextItem nameItem = new TextItem("name", Console.CONSTANTS.common_label_name());
        TextItem typeItem = new TextItem("type", Console.CONSTANTS.subsys_logging_type());

        ComboBoxItem logLevelItem = new ComboBoxItem("level", Console.CONSTANTS.subsys_logging_logLevel());
        logLevelItem.setValueMap(LogLevel.STRINGS);
        
        StatusItem flushItem = new StatusItem("autoflush", Console.CONSTANTS.subsys_logging_autoFlush());

        TextItem formatterItem = new TextItem("formatter", Console.CONSTANTS.subsys_logging_formatter());
        TextItem encodingItem = new TextItem("encoding", Console.CONSTANTS.subsys_logging_encoding());
        TextItem queueItem = new TextItem("queueLength", Console.CONSTANTS.subsys_logging_queueLength());

        Form<LoggingHandler> form = new Form(this.conversionType);
        form.setNumColumns(2);
        form.setFields(nameItem, typeItem, logLevelItem, flushItem, formatterItem, encodingItem, queueItem);
        return form;
    }
    
}
