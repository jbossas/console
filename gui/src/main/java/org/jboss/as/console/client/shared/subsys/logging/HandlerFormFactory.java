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

import java.util.HashMap;
import java.util.Map;
import org.jboss.as.console.client.Console;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.forms.FormItem;

import static org.jboss.as.console.client.shared.subsys.logging.HandlerAttribute.*;

/**
 * Creates the Form objects needed for LoggingHandlers.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class HandlerFormFactory<LoggingHandler> implements LoggingEntityFormFactory<LoggingHandler> {

    private Class<?> conversionType;
    private EntityBridge<LoggingHandler> bridge;
    
    public HandlerFormFactory(Class<?> conversionType, EntityBridge<LoggingHandler> bridge) {
        this.conversionType = conversionType;
        this.bridge = bridge;
    }
    
    @Override
    public FormAdapter<LoggingHandler> makeAddEntityForm() {
        ComboBoxItem handlerTypeItem = new ComboBoxItem("type", Console.CONSTANTS.subsys_logging_type());
        handlerTypeItem.setValueMap(HandlerType.getAllDisplayNames());
        handlerTypeItem.setValue("periodic-rotating-file-handler");

        FormItem levelItem = LEVEL.getItemForAdd();
        levelItem.setValue("INFO");
        
        Form<LoggingHandler> form = new Form(this.conversionType);
        form.setNumColumns(1);
        form.setFields(NAME.getItemForAdd(), handlerTypeItem, levelItem);
        return form;
    }

    @Override
    public AssignHandlerChooser<LoggingHandler> makeAssignHandlerForm() {
      return new AssignHandlerChooser(this.conversionType);
    }

    @Override
    public UnassignHandlerChooser<LoggingHandler> makeUnassignHandlerForm() {
        return new UnassignHandlerChooser(this.conversionType, bridge);
    }

    @Override
    public FormAdapter<LoggingHandler> makeEditForm() {
        
        Map<String, FormAdapter<LoggingHandler>> handlerForms = new HashMap<String, FormAdapter<LoggingHandler>>(HandlerType.values().length);
        for (HandlerType handlerType : HandlerType.values()) {
            HandlerAttribute[] attributes = handlerType.getAttributes();
            FormItem[] formItems = new FormItem[attributes.length];
            for (int i=0; i < attributes.length; i++) {
                formItems[i] = attributes[i].getItemForEdit();
            }
            Form<LoggingHandler> form = new Form<LoggingHandler>(this.conversionType);
            form.setFields(formItems);
            form.setNumColumns(2);
            handlerForms.put(handlerType.getDisplayName(), form);
        }

        FormAdapter formDeckPanel = new FormDeckPanel(handlerForms, "type");
        
        return formDeckPanel;
    }
    
}
