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

package org.jboss.as.console.client;

import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.as.console.client.widgets.forms.EntityFactory;
import org.jboss.as.console.client.widgets.forms.Mutator;
import org.jboss.as.console.client.widgets.forms.PropertyBinding;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.as.console.rebind.forms.PropertyMetaDataGenerator;

import java.util.ArrayList;
import java.util.List;
import org.jboss.as.console.rebind.forms.PropertyMetaDataGenerator.PropBindingDeclarations;

/**
 * @author Heiko Braun
 * @date 4/27/11
 */
public class ReflectionMetaData implements PropertyMetaData {
    @Override
    public List<PropertyBinding> getBindingsForType(Class<?> type) {
        List<PropertyBinding> bindings = new ArrayList<PropertyBinding>();

        List<PropBindingDeclarations> bindingDeclarations = PropertyMetaDataGenerator.mapProperties(type);
        for(PropBindingDeclarations decl : bindingDeclarations)
        {
            bindings.add(new PropertyBinding(decl.getBindingDeclaration().getJavaName(), decl.getBindingDeclaration().getDetypedName(),
                                             decl.getBindingDeclaration().getJavaTypeName(), decl.getBindingDeclaration().key()));
        }

        return bindings;
    }

    @Override
    public BeanMetaData getBeanMetaData(Class<?> type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> EntityFactory<T> getFactory(Class<T> type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Mutator getMutator(Class<?> type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
