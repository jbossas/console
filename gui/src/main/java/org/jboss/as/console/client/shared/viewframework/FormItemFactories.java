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
package org.jboss.as.console.client.shared.viewframework;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.properties.PropertyEditorFormItem;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.ListItem;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;

/**
 * Handy set of classes that know how to make a FormItem for edit or display of typical
 * data types.  You extend these classes to add extra validation.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class FormItemFactories {
    
    public static interface FormItemFactory {
        FormItem makeFormItem(String beanPropName, String label, boolean isRequired);
    }
    
    public static class TextItemFactory implements FormItemFactory {
        @Override
        public FormItem makeFormItem(String beanPropName, String label, boolean isRequired) {
            TextItem textItem = new TextItem(beanPropName, label);
            textItem.setRequired(isRequired);
            return textItem;
        }
    }
    
    public static class TextBoxItemFactory implements FormItemFactory {
        @Override
        public FormItem makeFormItem(String beanPropName, String label, boolean isRequired) {
            TextBoxItem textBoxItem = new TextBoxItem(beanPropName, label);
            textBoxItem.setRequired(isRequired);
            return textBoxItem;
        }
    }
 
    public static class ByteUnitItemFactory implements FormItemFactory {
        @Override
        public FormItem makeFormItem(String beanPropName, String label, boolean isRequired) {
            ByteUnitItem byteUnitItem = new ByteUnitItem(beanPropName, label);
            byteUnitItem.setRequired(isRequired);
            return byteUnitItem;
        }
        
        private static class ByteUnitItem extends TextBoxItem {
            private char[] UNIT_CHARS = {'b', 'k', 'm', 'g', 't'};
            
            public ByteUnitItem(String name, String title) {
                super(name, title);
            }
            
            @Override
            // GWT doesn't allow me to use regex to do this validation.  
            // Compiler chokes on Pattern class.
            public boolean validate(String value) {
                if (!super.validate(name)) {
                    return false;
                }
                
                if (value.length() < 2) return invalidValue();
                
                char finalChar = value.toLowerCase().charAt(value.length() - 1);
                
                boolean foundUnit = false;
                for (char unit : UNIT_CHARS) {
                    if (unit == finalChar) foundUnit = true;
                }
                
                if (!foundUnit) return invalidValue();
                    
                String number = value.substring(0, value.length() - 1);
                try {
                    Long.parseLong(number);
                } catch (NumberFormatException e) {
                    return invalidValue();
                }
                
                return true;
            }
            
            private boolean invalidValue() {
                this.errMessage = Console.CONSTANTS.subsys_logging_invalidByteSpec();
                return false;
            }
        }
    }
    
    public static class ComboBoxItemFactory implements FormItemFactory {
        private String[] values;
        
        public ComboBoxItemFactory(String[] values) {
            this.values = values;
        }
        
        @Override
        public FormItem makeFormItem(String beanPropName, String label, boolean isRequired) {
            ComboBoxItem comboBoxItem = new ComboBoxItem(beanPropName, label);
            comboBoxItem.setRequired(isRequired);
            comboBoxItem.setValueMap(values);
            return comboBoxItem;
        }
    }
    
    public static class CheckBoxItemFactory implements FormItemFactory {
        @Override
        public FormItem makeFormItem(String beanPropName, String label, boolean isRequired) {
            CheckBoxItem checkBoxItem = new CheckBoxItem(beanPropName, label);
            checkBoxItem.setRequired(isRequired);
            return checkBoxItem;
        }
    }
    
    public static class ListBoxItemFactory implements FormItemFactory {
        @Override
        public FormItem makeFormItem(String beanPropName, String label, boolean isRequired) {
            ListItem listItem = new ListItem(beanPropName, label, true);
            listItem.setRequired(isRequired);
            return listItem;
        }
    }
    
    /**
     * Factory for Short, Integer, and Long values
     */
    public static class NumberBoxItemFactory implements FormItemFactory {
        @Override
        public FormItem makeFormItem(String beanPropName, String label, boolean isRequired) {
            NumberBoxItem numberItem = new NumberBoxItem(beanPropName, label);
            numberItem.setRequired(isRequired);
            return numberItem;
        }
    }
    
    public static class PropertyEditorItemFactory implements FormItemFactory {
        private String addDialogTitle;
        private int rows;
        
        /**
         * @param addDialogTitle The title shown when the Add button is pressed on the PropertyEditor.
         * @param rows The number of rows in the PropertyEditor.
         */
        public PropertyEditorItemFactory(String addDialogTitle, int rows) {
            this.addDialogTitle = addDialogTitle;
            this.rows = rows;
        }
        
        @Override
        public FormItem makeFormItem(String beanPropName, String label, boolean isRequired) {
            PropertyEditorFormItem propEditor = new PropertyEditorFormItem(beanPropName, 
                                                                           label, 
                                                                           addDialogTitle, 
                                                                           rows);
            propEditor.setRequired(isRequired);
            return propEditor;
        }
    }
}
