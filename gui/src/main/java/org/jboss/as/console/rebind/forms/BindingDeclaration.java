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

import org.jboss.as.console.client.widgets.forms.Binding;

import java.lang.annotation.Annotation;

/**
 * @author Heiko Braun
 * @date 4/27/11
 */
public class BindingDeclaration implements Binding  {

    private String detypedName;
    private String javaName;
    private String javaTypeName;
    private String listType;
    private boolean doSkip = false;
    private boolean isKey = false;

    private String beanClassName;
    
    /**
     * 
     * @param detypedName
     * @param javaName
     * @param listType Fully-qualified type of the List, or <code>null</code> if the java type is not java.util.List
     * @param ignore
     * @param beanClassName 
     */
    public BindingDeclaration(String detypedName, String javaName, String listType, boolean ignore, String beanClassName) {
        this.detypedName = detypedName;
        this.javaName = javaName;
        this.listType = listType;
        this.doSkip = ignore;
        this.beanClassName = beanClassName;
    }

    public void setKey(boolean key) {
        isKey = key;
    }

    @Override
    public boolean key() {
        return isKey;
    }

    public String getJavaTypeName() {
        return javaTypeName;
    }

    public void setJavaTypeName(String javaTypeName) {

        if("boolean".equals(javaTypeName))
        {
            this.javaTypeName = "java.lang.Boolean";
        }
        else if("int".equals(javaTypeName))
        {
            this.javaTypeName = "java.lang.Integer";
        }
        else if("float".equals(javaTypeName))
        {
            this.javaTypeName = "java.lang.Float";
        }
        else if("long".equals(javaTypeName))
        {
            this.javaTypeName = "java.lang.Long";
        }
        else
        {
            // default
            this.javaTypeName = javaTypeName;
        }

    }

    public String getDetypedName() {
        return detypedName;
    }

    @Override
    public String listType() {
        return listType;
    }

    public String getJavaName() {
        return javaName;
    }

    public String getPropertyName() {
        StringBuffer sb = new StringBuffer();
        sb.append(getJavaName().substring(0, 1).toUpperCase());
        sb.append(getJavaName().substring(1));
        return sb.toString();
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    @Override
    public String detypedName() {
        return detypedName;
    }

    @Override
    public boolean skip() {
        return doSkip;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        throw new RuntimeException("not implemented");
    }
}
