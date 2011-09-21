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

import org.jboss.as.console.client.shared.viewframework.FormItemFactories.FormItemFactory;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.dmr.client.ModelType;

/**
 * MetaData that ties attributes in DMR to the corresponding attributes in the UI.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class AttributeMetadata {
    
    private boolean isRequired;
    private String dmrName;
    private ModelType modelType;
    private String beanPropName;
    private Object defaultValue;
    private String label;
    private FormItemFactory itemFactoryForAdd;
    private FormItemFactory itemFactoryForEdit;
    
    /**
     * Create a new AttributeMetadata.
     * 
     * @param beanPropName The bean-style name for the attribute that corresponds to the AutoBean.
     * @param dmrName The name for the attribute in DMR requests.
     * @param modelType The DMR type for the attribute.
     * @param defaultValue The default value used when creating an Entity that contains the Attribute.
     * @param itemFactoryForAdd The factory that creates the proper FormItem for an Add dialog.
     * @param itemFactoryForEdit The factory that creates the proper FormItem for an Edit dialog.
     * @param label The label that this attribute is given in a Form.
     * @param isRequired Is this a required attribute.
     */
    public AttributeMetadata(String beanPropName, String dmrName, ModelType modelType, Object defaultValue, 
            FormItemFactory itemFactoryForAdd, FormItemFactory itemFactoryForEdit, String label, boolean isRequired) {
        this.isRequired = isRequired;
        this.dmrName = dmrName;
        this.modelType = modelType;
        this.beanPropName = beanPropName;
        this.defaultValue = defaultValue;
        this.label = label;
        this.itemFactoryForAdd = itemFactoryForAdd;
        this.itemFactoryForEdit = itemFactoryForEdit;
    }
    
    /**
     * Create a new AttributeMetadata.
     * 
     * @param beanPropName The bean-style name for the attribute that corresponds to the AutoBean.
     * @param dmrName The name for the attribute in DMR requests.
     * @param modelType The DMR type for the attribute.
     * @param defaultValue The default value used when creating an Entity that contains the Attribute.
     * @param itemFactoryForAddAndEdit The factory that creates the proper FormItem for an Add and Edit dialog.
     * @param label The label that this attribute is given in a Form.
     * @param isRequired Is this a required attribute.
     */
    public AttributeMetadata(String beanPropName, String dmrName, ModelType modelType, Object defaultValue, 
            FormItemFactory itemFactoryForAddAndEdit, String label, boolean isRequired) {
        this(beanPropName, dmrName, modelType, defaultValue, itemFactoryForAddAndEdit, itemFactoryForAddAndEdit, label, isRequired);
    }
    
    public boolean isRequired() {
        return this.isRequired;
    }
    
    public String getDmrName() {
        return this.dmrName;
    }
    
    public ModelType getModelType() {
        return this.modelType;
    }
    
    public String getBeanPropName() {
        return this.beanPropName;
    }
    
    public Object getDefaultValue() {
        return this.defaultValue;
    }
    
    public FormItem getItemForAdd() {
        return this.itemFactoryForAdd.makeFormItem(beanPropName, label, isRequired);
    }
    
    public FormItem getItemForEdit() {
        return this.itemFactoryForEdit.makeFormItem(beanPropName, label, isRequired);
    }
}
