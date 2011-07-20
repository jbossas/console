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
import org.jboss.as.console.client.shared.subsys.logging.model.LoggingHandler;
import org.jboss.as.console.client.widgets.forms.CheckBoxItem;
import org.jboss.as.console.client.widgets.forms.ComboBoxItem;
import org.jboss.as.console.client.widgets.forms.FormItem;
import org.jboss.as.console.client.widgets.forms.ListItem;
import org.jboss.as.console.client.widgets.forms.NumberBoxItem;
import org.jboss.as.console.client.widgets.forms.TextBoxItem;
import org.jboss.as.console.client.widgets.forms.TextItem;

/**
 * Enum that ties handler attributes in DMR to the corresponding attributes in the UI.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public enum HandlerAttribute {
    
    NAME(true, "name", "name", "", Console.CONSTANTS.common_label_name(), new TextBoxItemFactory(), new TextItemFactory()),
    LEVEL(true, "level", "level", "INFO", Console.CONSTANTS.subsys_logging_logLevel(), new ComboBoxItemFactory(LogLevel.STRINGS)),
    ENCODING(true, "encoding", "encoding", "UTF-8", Console.CONSTANTS.subsys_logging_encoding(), new TextBoxItemFactory()),
    FILTER(false, "filter", "filter", "", Console.CONSTANTS.subsys_logging_filter(), new TextBoxItemFactory()),
    FORMATTER(true, "formatter", "formatter", "%d{HH:mm:ss,SSS} %-5p [%c] (%t) %s%E%n", Console.CONSTANTS.subsys_logging_formatter(), new TextBoxItemFactory()),
    AUTOFLUSH(false, "autoflush", "autoflush", "true", Console.CONSTANTS.subsys_logging_autoFlush(), new CheckBoxItemFactory()),
    APPEND(false, "append", "append", "false", Console.CONSTANTS.subsys_logging_append(), new CheckBoxItemFactory()),
    FILE_RELATIVE_TO(false, "relative-to", "fileRelativeTo", "jboss.server.log.dir", Console.CONSTANTS.subsys_logging_fileRelativeTo(), new TextBoxItemFactory()),
    FILE_PATH(true, "path", "filePath", "mylog.log", Console.CONSTANTS.subsys_logging_filePath(), new TextBoxItemFactory()),
    ROTATE_SIZE(true, "rotate-size", "rotateSize", "10485760", Console.CONSTANTS.subsys_logging_rotateSize(), new TextBoxItemFactory()), // 10MB default
    MAX_BACKUP_INDEX(true, "max-backup-index", "maxBackupIndex", "5", Console.CONSTANTS.subsys_logging_maxBackupIndex(), new TextBoxItemFactory()),
    TARGET(true, "target", "target", "SYSTEM_OUT", Console.CONSTANTS.subsys_logging_target(), new TextBoxItemFactory()), // default value can either be SYSTEM_OUT or SYSTEM_ERR
    SUBHANDLERS(false, "subhandlers", "subhandlers", "", Console.CONSTANTS.subsys_logging_subhandlers(), new ListBoxItemFactory()),
    OVERFLOW_ACTION(false, "overflow-action", "overflowAction", "BLOCK", Console.CONSTANTS.subsys_logging_overflowAction(), new ComboBoxItemFactory(new String[] {"BLOCK", "DISCARD"})),
    QUEUE_LENGTH(true, "queue-length", "queueLength", "512", Console.CONSTANTS.subsys_logging_queueLength(), new TextBoxItemFactory()),
    SUFFIX(true, "suffix", "suffix", ".yyyy-MM-dd", Console.CONSTANTS.subsys_logging_suffix(), new TextBoxItemFactory());
    
    private boolean isRequired;
    private String dmrName;
    private String beanPropName;
    private String defaultValue;
    private String label;
    private FormItemFactory itemFactoryForAdd;
    private FormItemFactory itemFactoryForEdit;
    
    /**
     * Create a new HandlerAttribute.
     * 
     * @param isRequired Is this a required attribute.
     * @param dmrName The name for the attribute in DMR requests.
     * @param beanPropName The bean-style name for the attribute that corresponds to the AutoBean.
     * @param defaultValue The default value used when creating a Handler.
     * @param label The label that this attribute is given in a Form.
     * @param itemFactoryForAdd The factory that creates the proper FormItem for an Add dialog.
     * @param itemFactoryForEdit The factory that creates the proper FormItem for an Edit dialog.
     */
    private HandlerAttribute(boolean isRequired, String dmrName, String beanPropName, String defaultValue, 
                             String label, FormItemFactory itemFactoryForAdd, FormItemFactory itemFactoryForEdit) {
        this.isRequired = isRequired;
        this.dmrName = dmrName;
        this.beanPropName = beanPropName;
        this.defaultValue = defaultValue;
        this.label = label;
        this.itemFactoryForAdd = itemFactoryForAdd;
        this.itemFactoryForEdit = itemFactoryForEdit;
    }
    
    /**
     * Create a new HandlerAttribute.
     * 
     * @param isRequired Is this a required attribute.
     * @param dmrName The name for the attribute in DMR requests.
     * @param beanPropName The bean-style name for the attribute that corresponds to the AutoBean.
     * @param defaultValue The default value used when creating a Handler.
     * @param label The label that this attribute is given in a Form.
     * @param itemFactoryForAddAndEdit The factory that creates the proper FormItem for an Add and Edit dialog.
     */
    private HandlerAttribute(boolean isRequired, String dmrName, String beanPropName, String defaultValue, 
                             String label, FormItemFactory itemFactoryForAddAndEdit) {
        this(isRequired, dmrName, beanPropName, defaultValue, label, itemFactoryForAddAndEdit, itemFactoryForAddAndEdit);
    }
    
    /**
     * Find a HandlerAttribute with the given bean property.
     * @param beanPropName The name of the bean property.
     * @return The HandlerAttribute
     * @throws IllegalArgumentException if the HandlerAttribute is not found.
     */
    public static HandlerAttribute findHandlerAttribute(String beanPropName) {
        for (HandlerAttribute attrib : HandlerAttribute.values()) {
            if (attrib.beanPropName.equals(beanPropName)) return attrib;
        }
        
        throw new IllegalArgumentException("Unknown HandlerAttribute with beanPropName name " + beanPropName);
    }
    
    public String getDmrName() {
        return this.dmrName;
    }
    
    public String getBeanPropName() {
        return this.beanPropName;
    }
    
    public String getDefaultValue() {
        return this.defaultValue;
    }
    
    public FormItem<LoggingHandler> getItemForAdd() {
        return this.itemFactoryForAdd.makeFormItem(beanPropName, label, isRequired);
    }
    
    public FormItem<LoggingHandler> getItemForEdit() {
        return this.itemFactoryForEdit.makeFormItem(beanPropName, label, isRequired);
    }
    
    private static interface FormItemFactory {
        FormItem makeFormItem(String beanPropName, String label, boolean isRequired);
    }
    
    private static class TextItemFactory implements FormItemFactory {
        @Override
        public FormItem makeFormItem(String beanPropName, String label, boolean isRequired) {
            TextItem textItem = new TextItem(beanPropName, label);
            textItem.setRequired(isRequired);
            return textItem;
        }
    }
    
    private static class TextBoxItemFactory implements FormItemFactory {
        @Override
        public FormItem makeFormItem(String beanPropName, String label, boolean isRequired) {
            TextBoxItem textBoxItem = new TextBoxItem(beanPropName, label);
            textBoxItem.setRequired(isRequired);
            return textBoxItem;
        }
    }
    
    private static class ComboBoxItemFactory implements FormItemFactory {
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
    
    private static class CheckBoxItemFactory implements FormItemFactory {
        @Override
        public FormItem makeFormItem(String beanPropName, String label, boolean isRequired) {
            CheckBoxItem checkBoxItem = new CheckBoxItem(beanPropName, label);
            checkBoxItem.setRequired(isRequired);
            return checkBoxItem;
        }
    }
    
    private static class ListBoxItemFactory implements FormItemFactory {
        @Override
        public FormItem makeFormItem(String beanPropName, String label, boolean isRequired) {
            ListItem listItem = new ListItem(beanPropName, label, true);
            listItem.setRequired(isRequired);
            return listItem;
        }
    }
    
    private static class NumberBoxItemFactory implements FormItemFactory {
        @Override
        public FormItem makeFormItem(String beanPropName, String label, boolean isRequired) {
            NumberBoxItem numberItem = new NumberBoxItem(beanPropName, label);
            numberItem.setRequired(isRequired);
            return numberItem;
        }
    }
    
}
