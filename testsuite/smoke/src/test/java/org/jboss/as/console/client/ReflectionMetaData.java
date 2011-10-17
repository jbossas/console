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

import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.widgets.forms.AddressBinding;
import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.as.console.client.widgets.forms.EntityFactory;
import org.jboss.as.console.client.widgets.forms.Mutator;
import org.jboss.as.console.client.widgets.forms.PropertyBinding;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.as.console.rebind.forms.AddressDeclaration;
import org.jboss.as.console.rebind.forms.PropertyMetaDataGenerator;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Heiko Braun
 * @date 4/27/11
 */
public class ReflectionMetaData implements PropertyMetaData {


    private BeanFactory factory;
    private Map<Class<?>, EntityFactory> factories = new HashMap<Class<?>, EntityFactory>();

    @Inject
    public ReflectionMetaData(BeanFactory factory) {
        this.factory = factory;

        setupFactories();
    }

    private void setupFactories() {
        try
        {
            Class<?> beanFactoryClass =
                    getClass().getClassLoader().loadClass("org.jboss.as.console.client.shared.BeanFactory");

            for(final Method method : beanFactoryClass.getDeclaredMethods())
            {

                Type returnType = method.getGenericReturnType();
                if(returnType instanceof ParameterizedType){
                    ParameterizedType type = (ParameterizedType) returnType;
                    Type[] typeArguments = type.getActualTypeArguments();

                    if(typeArguments[0] instanceof Class)
                    {
                        Class beanTypeClass = (Class) typeArguments[0];

                        factories.put(beanTypeClass, new EntityFactory() {
                            @Override
                            public Object create() {
                                try {
                                    return method.invoke(null);
                                } catch (Throwable e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });

                    }
                }
            }

        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<PropertyBinding> getBindingsForType(Class<?> type) {
        List<PropertyBinding> bindings = new ArrayList<PropertyBinding>();

        List<PropertyMetaDataGenerator.PropBindingDeclarations> bindingDeclarations = PropertyMetaDataGenerator.mapProperties(type);
        for(PropertyMetaDataGenerator.PropBindingDeclarations decl : bindingDeclarations)
        {
            PropertyBinding propertyBinding = new PropertyBinding(
                    decl.getBindingDeclaration().getJavaName(),
                    decl.getBindingDeclaration().getDetypedName(),
                    decl.getBindingDeclaration().getJavaTypeName(),
                    decl.getBindingDeclaration().key(),
                    decl.getBindingDeclaration().expr()
            );


            bindings.add(propertyBinding);
        }

        return bindings;
    }

    @Override
    public BeanMetaData getBeanMetaData(Class<?> type) {

        AddressDeclaration address = PropertyMetaDataGenerator.parseAddress(type);
        AddressBinding addressBinding = new AddressBinding();
        for(String[] tuple : address.getAddress())
        {
            addressBinding.add(tuple[0], tuple[1]);
        }

        BeanMetaData metaData = new BeanMetaData(type, addressBinding, getBindingsForType(type));
        return metaData;
    }

    @Override
    public <T> EntityFactory<T> getFactory(Class<T> type) {
        return factories.get(type);
    }

    @Override
    public Mutator getMutator(Class<?> type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
