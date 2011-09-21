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

import org.jboss.as.console.client.shared.viewframework.FormDeckPanel;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import org.jboss.as.console.client.Console;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.DefaultGroupRenderer;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.forms.FormItem;

import java.util.HashMap;
import java.util.Map;

import static org.jboss.as.console.client.shared.subsys.logging.HandlerAttribute.LEVEL;
import static org.jboss.as.console.client.shared.subsys.logging.HandlerAttribute.NAME;
import static org.jboss.as.console.client.shared.subsys.logging.HandlerAttribute.CLASS;
import static org.jboss.as.console.client.shared.subsys.logging.HandlerAttribute.MODULE;

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
        Map<String, FormAdapter<LoggingHandler>> handlerForms = new HashMap<String, FormAdapter<LoggingHandler>>(HandlerType.values().length);
        final AddHandlerDeckPanel formDeckPanel = new AddHandlerDeckPanel("type");
        
        for (HandlerType handlerType : HandlerType.values()) {
            Form<LoggingHandler> form = new Form<LoggingHandler>(this.conversionType);
            form.setNumColumns(1);
            
            ComboBoxItem handlerTypeItem = new ComboBoxItem("type", Console.CONSTANTS.subsys_logging_type());
            formDeckPanel.addComboBox(handlerType.getDisplayName(), handlerTypeItem);
            handlerTypeItem.setValueMap(HandlerType.getAllDisplayNames());
            handlerTypeItem.setValue(handlerType.getDisplayName());
            handlerTypeItem.addValueChangeHandler(new ValueChangeHandler<String>() {
                @Override
                public void onValueChange(ValueChangeEvent<String> event) {
                    formDeckPanel.showWidget(event.getValue());
                }
            });

            FormItem levelItem = LEVEL.getItemForAdd();
            levelItem.setValue("INFO");

            if (handlerType.equals(HandlerType.CUSTOM)) {
                FormItem classNameItem = CLASS.getItemForAdd();
                FormItem moduleItem = MODULE.getItemForAdd();
                form.setFields(handlerTypeItem, NAME.getItemForAdd(), levelItem, moduleItem, classNameItem);
            } else {
                form.setFields(handlerTypeItem, NAME.getItemForAdd(), levelItem);
            }
            
            handlerForms.put(handlerType.getDisplayName(), form);
        }

        formDeckPanel.setForms(handlerForms, HandlerType.PERIODIC_ROTATING_FILE.getDisplayName());
        return formDeckPanel;
    }
    
    // Convoluted extension of FormDeckPanel that collects the ComboBoxItems.  Since the ComboBoxItem is the
    // thing that switches panels, we need to set its value to whatever was selected.  Otherwise, whatever
    // the user selected would appear to change to whatever the value was last time the panel was displayed.
    private class AddHandlerDeckPanel extends FormDeckPanel<LoggingHandler> {
        private Map<String, ComboBoxItem> comboBoxes = new HashMap<String, ComboBoxItem>();
        
        AddHandlerDeckPanel(String triggerProperty) {
            super(triggerProperty);
        }
        
        void addComboBox(String type, ComboBoxItem comboBox) {
            this.comboBoxes.put(type, comboBox);
        }

        @Override
        public void showWidget(String name) {
            ComboBoxItem comboBox = comboBoxes.get(name);
            
            // Only change the value if it is wrong.  Otherwise, it triggers the ValueChangeHandler again
            // and you get an endless loop.
            if (!name.equals(comboBox.getValue())) comboBox.setValue(name);
            
            super.showWidget(name);
        }
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
            int mainItemCount = (handlerType == HandlerType.CUSTOM) ? attributes.length - 1 : attributes.length;
            FormItem[] formItems = new FormItem[mainItemCount];
            for (int i=0; i < attributes.length; i++) {
                if (attributes[i] == HandlerAttribute.PROPERTIES) continue;
                formItems[i] = attributes[i].getItemForEdit();
            }
            Form<LoggingHandler> form = new Form<LoggingHandler>(this.conversionType);
            form.setFields(formItems);
            form.setNumColumns(2);
            
            // put Properties Editor in a single column spanning the bottom of the form
            if (handlerType == HandlerType.CUSTOM) form.setFieldsInGroup(Console.CONSTANTS.subsys_logging_handlerProperties(), 
                                                                         new DefaultGroupRenderer(),
                                                                         HandlerAttribute.PROPERTIES.getItemForEdit());
            
            handlerForms.put(handlerType.getDisplayName(), form);
        }

        FormDeckPanel formDeckPanel = new FormDeckPanel("type");
        formDeckPanel.setForms(handlerForms, HandlerType.PERIODIC_ROTATING_FILE.getDisplayName());
        return formDeckPanel;
    }
    
}
