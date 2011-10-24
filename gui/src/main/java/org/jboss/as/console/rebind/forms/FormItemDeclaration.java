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

package org.jboss.as.console.rebind.forms;

import java.lang.annotation.Annotation;
import org.jboss.as.console.client.widgets.forms.FormItem;

/**
 * @author Stan Silvert
 */
public class FormItemDeclaration implements FormItem  {

    private String defaultValue;
    private String label;
    private String localLabel;
    private boolean isRequired;
    private String formItemTypeForEdit;
    private String formItemTypeForAdd;
    private String subgroup;
    private String tabName;
    private int order;
    
    public FormItemDeclaration(String defaultValue, String label, String localLabel, boolean isRequired,
                              String formItemTypeForEdit, String formItemTypeForAdd, String subgroup, 
                              String tabName, int order) {
        this.defaultValue = defaultValue;
        this.label = label;
        this.localLabel = localLabel;
        this.isRequired = isRequired;
        this.formItemTypeForEdit = formItemTypeForEdit;
        this.formItemTypeForAdd = formItemTypeForAdd;
        this.subgroup = subgroup;
        this.tabName = tabName;
        this.order = order;
    }

    @Override
    public String defaultValue() {
        return this.defaultValue;
    }

    @Override
    public String label() {
        return this.label;
    }

    @Override
    public String localLabel() {
        return this.localLabel;
    }
    

    @Override
    public boolean required() {
        return this.isRequired;
    }

    @Override 
    public String formItemTypeForEdit() {
        return this.formItemTypeForEdit;
    }
    
    @Override
    public String  formItemTypeForAdd() {
        return this.formItemTypeForAdd;
    }
    
    @Override
    public String subgroup() {
        return this.subgroup;
    }
    
    @Override
    public String tabName() {
        return this.tabName;
    }

    @Override
    public int order() {
        return this.order;
    }
    
    @Override
    public Class<? extends Annotation> annotationType() {
        throw new RuntimeException("not implemented");
    }
}
