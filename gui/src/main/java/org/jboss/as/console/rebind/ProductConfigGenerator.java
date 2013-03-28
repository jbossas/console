package org.jboss.as.console.rebind;

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

import java.io.PrintWriter;
import java.util.List;

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.ConfigurationProperty;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.PropertyOracle;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Heiko Braun
 * @date 4/19/11
 */
public class ProductConfigGenerator extends Generator {

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
        System.out.println("\n\n\n################# Generating " + typeName + "\n\n");
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
        catch (Throwable e)
        {
            // record to logger that Map generation threw an exception
            e.printStackTrace(System.out);
            logger.log(TreeLogger.ERROR, "Failed to generate product config", e);
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
    private void generateClass(TreeLogger logger, GeneratorContext context) throws Throwable
    {

        // get print writer that receives the source code
        PrintWriter printWriter = context.tryCreate(logger, packageName, className);

        // print writer if null, source code has ALREADY been generated, return
        if (printWriter == null) return;

        // init composer, set class properties, create source writer
        ClassSourceFileComposerFactory composerFactory =
                new ClassSourceFileComposerFactory(packageName, className);

        // Imports
        composerFactory.addImport("org.jboss.as.console.client.Console");
        composerFactory.addImport("org.jboss.as.console.client.ProductConfig");

        composerFactory.addImport("java.util.*");

        // Interfaces
        composerFactory.addImplementedInterface("org.jboss.as.console.client.ProductConfig");

        // SourceWriter
        SourceWriter sourceWriter = composerFactory.createSourceWriter(context, printWriter);

        // fields
        generateFields(sourceWriter);

        // ctor
        generateConstructor(logger, context, sourceWriter);

        // Methods
        generateMethods(sourceWriter, context);

        // close generated class
        sourceWriter.outdent();
        sourceWriter.println("}");

        // commit generated class
        context.commit(logger, printWriter);
    }

    private void generateFields(SourceWriter sourceWriter) {
        //sourceWriter.println("private static Map<Class<?>, List<PropertyBinding>> registry = new HashMap<Class<?>,List<PropertyBinding>>();");

    }

    private void generateConstructor(TreeLogger logger, GeneratorContext context, SourceWriter sourceWriter)
    {
        // start constructor source generation
        sourceWriter.println("public " + className + "() { ");
        sourceWriter.indent();
        sourceWriter.println("super();");

        // TODO

        sourceWriter.outdent();
        sourceWriter.println("}");
    }


    private void generateMethods(SourceWriter sourceWriter, GeneratorContext context) throws Throwable
    {

        PropertyOracle propertyOracle = context.getPropertyOracle();
        String consoleProfileProperty =
                propertyOracle.getConfigurationProperty("console.profile").getValues().get(0);

        if(null==consoleProfileProperty)
            throw new BadPropertyValueException("Missing configuration property 'console.profile'!");


        String prodVersionProperty =
                propertyOracle.getConfigurationProperty("console.product.version").getValues().get(0);

        String consoleProductVersion = (prodVersionProperty != null) ?
                prodVersionProperty : "";

        String devHostProperty = null;
        try
        {
            ConfigurationProperty configurationProperty = propertyOracle.getConfigurationProperty("console.dev.host");
            if (configurationProperty != null)
            {
                List<String> values = configurationProperty.getValues();
                if (values != null && !values.isEmpty())
                {
                    devHostProperty = values.get(0);
                }
            }
        }
        finally
        {
            // fall back to localhost
            if (devHostProperty == null)
            {
                devHostProperty = "127.0.0.1";
            }
        }

        // most of the config attributes are by default empty
        // they need be overriden by custom gwt.xml descriptor on a project/product level

        sourceWriter.println("public String getProductTitle() { ");
        sourceWriter.indent();
        sourceWriter.println("return \"\";");
        sourceWriter.outdent();
        sourceWriter.println("}");

        sourceWriter.println("public String getProductVersion() { ");
        sourceWriter.indent();
        sourceWriter.println("return \""+consoleProductVersion+"\";");
        sourceWriter.outdent();
        sourceWriter.println("}");

        sourceWriter.println("public String getCoreVersion() { ");
        sourceWriter.indent();
        sourceWriter.println("return org.jboss.as.console.client.Build.VERSION;");
        sourceWriter.outdent();
        sourceWriter.println("}");

        sourceWriter.println("public String getDevHost() { ");
        sourceWriter.indent();
        sourceWriter.println("return \""+devHostProperty+"\";");
        sourceWriter.outdent();
        sourceWriter.println("}");

        sourceWriter.println("public ProductConfig.Profile getProfile() { ");
        sourceWriter.indent();
        if("eap".equals(consoleProfileProperty))
            sourceWriter.println("return ProductConfig.Profile.EAP;");
        else
            sourceWriter.println("return ProductConfig.Profile.JBOSS;");
        sourceWriter.outdent();
        sourceWriter.println("}");

    }


}
