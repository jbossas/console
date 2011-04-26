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

import com.google.gwt.autobean.shared.AutoBean;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import org.jboss.as.console.client.widgets.forms.Binding;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * @author Heiko Braun
 * @date 4/19/11
 */
public class PropertyMetaDataGenerator extends Generator{

    private static final String BEAN_FACTORY_NAME = "org.jboss.as.console.client.shared.BeanFactory";
    /**
     * Simple name of class to be generated
     */
    private String className = null;

    /**
     * Package name of class to be generated
     */
    private String packageName = null;

    /**
     * Fully qualified class name passed into GWT.create()
     */
    private String typeName = null;


    public String generate(TreeLogger logger, GeneratorContext context, String typeName)
            throws UnableToCompleteException
    {
        this.typeName = typeName;
        TypeOracle typeOracle = context.getTypeOracle();

        try
        {
            // get classType and save instance variables
            JClassType classType = typeOracle.getType(typeName);
            packageName = classType.getPackage().getName();
            className = classType.getSimpleSourceName() + "Impl";

            // Generate class source code
            generateClass(logger, context);

        }
        catch (Exception e)
        {
            // record to logger that Map generation threw an exception
            e.printStackTrace(System.out);
            logger.log(TreeLogger.ERROR, "Failed to generate property meta data", e);
        }

        // return the fully qualified name of the class generated
        return packageName + "." + className;
    }

    /**
     * Generate source code for new class. Class extends
     * <code>HashMap</code>.
     *
     * @param logger  Logger object
     * @param context Generator context
     */
    private void generateClass(TreeLogger logger, GeneratorContext context)
    {

        // get print writer that receives the source code
        PrintWriter printWriter = context.tryCreate(logger, packageName, className);

        // print writer if null, source code has ALREADY been generated, return
        if (printWriter == null) return;

        // init composer, set class properties, create source writer
        ClassSourceFileComposerFactory composerFactory =
                new ClassSourceFileComposerFactory(packageName, className);

        // Imports
        composerFactory.addImport("org.jboss.as.console.client.widgets.forms.*");
        composerFactory.addImport("java.util.*");

        // Interfaces
        composerFactory.addImplementedInterface("org.jboss.as.console.client.widgets.forms.PropertyMetaData");

        // SourceWriter
        SourceWriter sourceWriter = composerFactory.createSourceWriter(context, printWriter);

        // fields
        generateFields(sourceWriter);

        // ctor
        generateConstructor(logger, context, sourceWriter);

        // Methods
        generateMethods(sourceWriter);

        // close generated class
        sourceWriter.outdent();
        sourceWriter.println("}");

        // commit generated class
        context.commit(logger, printWriter);
    }

    private void generateFields(SourceWriter sourceWriter) {
        sourceWriter.println("private Map<Class<?>,List<PropertyBinding>> registry = new HashMap<Class<?>,List<PropertyBinding>>();");
    }

    private void generateConstructor(TreeLogger logger, GeneratorContext context , SourceWriter sourceWriter)
    {
        // start constructor source generation
        sourceWriter.println("public " + className + "() { ");
        sourceWriter.indent();
        sourceWriter.println("super();");

        try {
            Class<?> beanFactoryClass = getClass().getClassLoader().loadClass(BEAN_FACTORY_NAME);
            for(Method method : beanFactoryClass.getDeclaredMethods())
            {
                Type returnType = method.getGenericReturnType();
                if(returnType instanceof ParameterizedType){
                    ParameterizedType type = (ParameterizedType) returnType;
                    Type[] typeArguments = type.getActualTypeArguments();

                    if(typeArguments[0] instanceof Class)
                    {
                        Class beanTypeClass = (Class) typeArguments[0];
                        sourceWriter.println("registry.put("+beanTypeClass.getName()+".class, new ArrayList<PropertyBinding>());");

                        // map properties
                        mapProperties(beanTypeClass, context, sourceWriter);
                    }
                }

            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load " + BEAN_FACTORY_NAME);
        }

        sourceWriter.outdent();
        sourceWriter.println("}");
    }

    private void mapProperties(Class beanTypeClass, GeneratorContext context, SourceWriter sourceWriter) {

        for(Method method : beanTypeClass.getDeclaredMethods())
        {
            String methodName = method.getName();
            if(methodName.startsWith("get"))
            {
                String token = methodName.substring(3, methodName.length());
                writeRegisterPropBinding(beanTypeClass, sourceWriter, method, token);
            }
            else if(methodName.startsWith("is"))
            {
                String token = methodName.substring(2, methodName.length());
                writeRegisterPropBinding(beanTypeClass, sourceWriter, method, token);
            }
            else
            {
                continue;
            }
        }
    }

    private void writeRegisterPropBinding(Class beanTypeClass, SourceWriter sourceWriter, Method method, String token) {
        String firstLetter = token.substring(0,1);
        String remainder   = token.substring(1);
        String normalized = firstLetter.toLowerCase() + remainder;

        String javaName = normalized;
        String detypedName = javaName;

        // @Binding can override the detyped name
        Binding bindingDeclaration = method.getAnnotation(Binding.class);
        if(bindingDeclaration!=null)
        {
            if(bindingDeclaration.ignore()) return; // skip

            detypedName = bindingDeclaration.detypedName();
        }

        sourceWriter.println("registry.get("+beanTypeClass.getName()+".class).add(");
        sourceWriter.indent();
        sourceWriter.println("new PropertyBinding(\""+javaName+"\", \""+detypedName+"\")");
        sourceWriter.outdent();
        sourceWriter.println(");");
    }

    private void generateMethods(SourceWriter sourceWriter)
    {
        // start constructor source generation
        sourceWriter.println("public List<PropertyBinding> getBindingsForType(Class<?> type) { ");
        sourceWriter.indent();

        // write content
        sourceWriter.println("return registry.get(type);");

        // end constructor source generation
        sourceWriter.outdent();
        sourceWriter.println("}");
    }

}
