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
import org.jboss.as.console.client.widgets.forms.ComboBoxItem;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.ListItem;
import org.jboss.as.console.client.widgets.forms.TextBoxItem;
import org.jboss.as.console.client.widgets.forms.TextItem;

/**
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class LoggerConfigFormFactory<LoggerConfig> implements LoggingEntityFormFactory<LoggerConfig> {

    private Class<?> conversionType;
    private EntityBridge<LoggerConfig> bridge;
    
    public LoggerConfigFormFactory(Class<?> conversionType, EntityBridge<LoggerConfig> bridge) {
        this.conversionType = conversionType;
        this.bridge = bridge;
    }
    
    @Override
    public Form<LoggerConfig> makeAddEntityForm() {
        TextBoxItem nameItem = new TextBoxItem("name", Console.CONSTANTS.common_label_name());

        ComboBoxItem logLevelItem = new ComboBoxItem("level", Console.CONSTANTS.subsys_logging_logLevel());
        logLevelItem.setValueMap(LogLevel.STRINGS);
        logLevelItem.setValue(LogLevel.INFO.toString());

        Form<LoggerConfig> form = new Form(this.conversionType);
        form.setNumColumns(1);
        form.setFields(nameItem, logLevelItem);
        return form;
    }

    @Override
    public AssignHandlerChooser<LoggerConfig> makeAssignHandlerForm() {
        return new AssignHandlerChooser(this.conversionType);
    }

    @Override
    public UnassignHandlerChooser<LoggerConfig> makeUnassignHandlerForm() {
        return new UnassignHandlerChooser(this.conversionType, this.bridge);
    }
    
    @Override
    public Form<LoggerConfig> makeEditForm() {
        TextItem nameItem = new TextItem("name", Console.CONSTANTS.common_label_name());

        ComboBoxItem logLevelItem = new ComboBoxItem("level", Console.CONSTANTS.subsys_logging_logLevel());
        logLevelItem.setValueMap(LogLevel.STRINGS);

        ListItem handlersItem = new ListItem("handlers", Console.CONSTANTS.subsys_logging_handlers(), true);

        Form<LoggerConfig> form = new Form(this.conversionType);
        form.setNumColumns(1);
        form.setFields(nameItem, logLevelItem, handlersItem);
        return form;
    }
    
}
