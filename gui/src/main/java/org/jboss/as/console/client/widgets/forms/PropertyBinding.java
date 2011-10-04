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

package org.jboss.as.console.client.widgets.forms;

import org.jboss.as.console.client.shared.viewframework.FormItemType;
import org.jboss.ballroom.client.widgets.forms.FormItem;

/**
 * @author Heiko Braun
 * @date 4/19/11
 */
public class PropertyBinding {
    private String detypedName;
    private String javaName;
    private String javaTypeName;
    private boolean isKey = false;
    private String defaultValue;
    private String label;
    private boolean isRequired;
    private FormItemType formItemTypeForEdit;
    private FormItemType formItemTypeForAdd;
    private String subgroup;

    public PropertyBinding(String javaName, String detypedName, String javaTypeName, boolean isKey) {
        this.detypedName = detypedName;
        this.javaName = javaName;
        this.javaTypeName = javaTypeName;
        this.isKey = isKey;
    }
    
    public PropertyBinding(String javaName, String detypedName, String javaTypeName, boolean isKey,
                           String defaultValue, String label, boolean isRequired,
                           String formItemTypeForEdit, String formItemTypeForAdd, String subgroup) {
        this(javaName, detypedName, javaTypeName, isKey);
        this.defaultValue = defaultValue;
        this.label = label;
        this.isRequired = isRequired;
        this.formItemTypeForEdit = FormItemType.valueOf(formItemTypeForEdit);
        this.formItemTypeForAdd = FormItemType.valueOf(formItemTypeForAdd);
        this.subgroup = subgroup;
    }

    public String getJavaTypeName() {
        return javaTypeName;
    }

    public String getDetypedName() {
        return detypedName;
    }

    public void setDetypedName(String detypedName) {
        this.detypedName = detypedName;
    }

    public String getJavaName() {
        return javaName;
    }

    public void setJavaName(String javaName) {
        this.javaName = javaName;
    }

    public boolean isKey() {
        return isKey;
    }
    
    public Object getDefaultValue() {
        if ("java.lang.String".equals(javaTypeName)) return defaultValue;
        if ("java.lang.Boolean".equals(javaTypeName)) return Boolean.valueOf(defaultValue);
        if ("java.lang.Long".equals(javaTypeName)) return Long.parseLong(defaultValue);
        if ("java.lang.Integer".equals(javaTypeName)) return Integer.parseInt(defaultValue);
        if ("java.lang.Double".equals(javaTypeName)) return Double.parseDouble(defaultValue);
        if ("java.lang.Float".equals(javaTypeName)) return Float.parseFloat(defaultValue);
        
        throw new RuntimeException("Unable to convert " + javaName + " default value " +
                                   defaultValue + " to type " + javaTypeName);
    }
    
    public FormItem getFormItemForAdd() {
        return formItemTypeForAdd.getFactory().makeFormItem(javaName, label, isRequired);
    }

    public FormItem getFormItemForEdit() {
        return formItemTypeForEdit.getFactory().makeFormItem(javaName, label, isRequired);
    }

    public boolean isRequired() {
        return isRequired;
    }

    public String getLabel() {
        return label;
    }
    
    public String getSubgroup() {
        return subgroup;
    }

    @Override
    public String toString() {

        String keyInd = isKey() ? "* " : "";
        return keyInd+javaName+">"+detypedName+" ("+javaTypeName+")";
    }
}
