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

import org.jboss.as.console.client.model.AutoBeanStub;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.widgets.forms.AddressBinding;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.as.console.client.widgets.forms.EntityFactory;
import org.jboss.as.console.client.widgets.forms.FormMetaData;
import org.jboss.as.console.client.widgets.forms.Getter;
import org.jboss.as.console.client.widgets.forms.Mutator;
import org.jboss.as.console.client.widgets.forms.PropertyBinding;
import org.jboss.as.console.client.widgets.forms.Setter;
import org.jboss.as.console.rebind.forms.AddressDeclaration;
import org.jboss.as.console.rebind.forms.ApplicationMetaDataGenerator;
import org.jboss.as.console.rebind.forms.BindingDeclaration;

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
public class ReflectionMetaData implements ApplicationMetaData {


    private BeanFactory factory;
    private Map<Class<?>, EntityFactory> factories = new HashMap<Class<?>, EntityFactory>();
    private Map<Class<?>, Mutator> mutators = new HashMap<Class<?>, Mutator>();

    @Inject
    public ReflectionMetaData(BeanFactory factory) {
        this.factory = factory;

        setup();

    }

    private void setup() {
        try {
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
                        setupMutators(beanTypeClass);
                        setupFactories(method, beanTypeClass);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    private void setupMutators(final Class beanTypeClass) {

        Mutator mut = new Mutator();
        mutators.put(beanTypeClass, mut);

        List<ApplicationMetaDataGenerator.PropBindingDeclarations> bindings = ApplicationMetaDataGenerator.mapProperties(beanTypeClass);

        for(ApplicationMetaDataGenerator.PropBindingDeclarations binding : bindings)
        {

            final BindingDeclaration bindDecl = binding.getBindingDeclaration();
            //if(bindDecl.skip()) continue;

            // create and register setters
            final Method setter = findMatchingSetter(bindDecl.getJavaName(), beanTypeClass);

            mut.register(bindDecl.getJavaName(), new Setter() {
                @Override
                public void invoke(Object entity, Object value) {
                    try {
                        if(entity instanceof AutoBeanStub)
                            setter.invoke(((AutoBeanStub)entity).as(), value);
                        else
                            setter.invoke(entity, value);
                    } catch (Throwable e) {
                        throw new RuntimeException("Failed to invoke "+setter.getName(), e);
                    }
                }
            });

            final Method getter = findMatchingGetter(bindDecl.getJavaName(), beanTypeClass);
            mut.register(bindDecl.getJavaName(), new Getter() {
                @Override
                public Object invoke(Object entity) {
                    try {

                        if(entity instanceof AutoBeanStub)
                            return getter.invoke(((AutoBeanStub)entity).as(), null);
                        else
                            return getter.invoke(entity, null);

                    } catch (Throwable e) {
                        throw new RuntimeException("Failed to invoke "+getter.getName(), e);
                    }
                }
            });
        }
    }

    private Method findMatchingSetter(String javaName, Class beanTypeClass) {
        Method match = null;
        Method[] methods = beanTypeClass.getMethods();
        for(Method m : methods) {

            if(m.getName().startsWith("set") && m.getName().toLowerCase().indexOf(javaName.toLowerCase())!=-1)
            {
                match = m;
                break;
            }
        }

        if(null==match)
            throw new IllegalArgumentException("No setter for field '" +javaName + "' on " + beanTypeClass);
        return match;
    }


    private Method findMatchingGetter(String javaName, Class beanTypeClass) {
        Method match = null;
        Method[] methods = beanTypeClass.getMethods();
        for(Method m : methods) {
            if(
                    (m.getName().startsWith("get") || m.getName().startsWith("is"))
                            && m.getName().toLowerCase().indexOf(javaName.toLowerCase())!=-1)
            {
                match = m;
                break;
            }
        }

        if(null==match)
            throw new IllegalArgumentException("No getter for field '" +javaName + "' on " + beanTypeClass);

        return match;
    }

    private void setupFactories(final Method method, Class beanTypeClass) {
        // entity factory
        factories.put(beanTypeClass, new EntityFactory() {
            @Override
            public Object create() {
                try {
                    AutoBeanStub stub = (AutoBeanStub)method.invoke(factory, null);
                    return stub.as();

                } catch (Throwable e) {
                    throw new RuntimeException("error on "+method.getName(), e);
                }
            }
        });
    }

    @Override
    public List<PropertyBinding> getBindingsForType(Class<?> type) {
        List<PropertyBinding> bindings = new ArrayList<PropertyBinding>();

        List<ApplicationMetaDataGenerator.PropBindingDeclarations> bindingDeclarations = ApplicationMetaDataGenerator.mapProperties(type);
        for(ApplicationMetaDataGenerator.PropBindingDeclarations decl : bindingDeclarations)
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

        AddressDeclaration address = ApplicationMetaDataGenerator.parseAddress(type);
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
        return mutators.get(type);
    }

    @Override
    public FormMetaData getFormMetaData(Class<?> type) {
        throw new RuntimeException("Not yet implemented!");
    }
}
