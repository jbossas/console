package org.jboss.as.console.spi;

import com.gwtplatform.mvp.client.annotations.NameToken;
import org.jboss.as.console.client.plugins.RuntimeExtensionMetaData;
import org.jboss.as.console.client.plugins.SubsystemExtensionMetaData;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static javax.lang.model.SourceVersion.RELEASE_6;

/**
 * @author Heiko Braun
 * @date 9/13/12
 */

@SupportedSourceVersion(RELEASE_6)
public class SPIProcessor extends AbstractProcessor {


    private static final String EXTENSION_TEMPLATE = "Extension.tmpl";
    private static final String EXTENSION_FILENAME = "org.jboss.as.console.client.core.gin.Composite";

    private static final String BINDING_TEMPLATE = "ExtensionBinding.tmpl";
    private final static String BINDING_FILENAME = "org.jboss.as.console.client.core.gin.CompositeBinding";

    private static final String BEAN_FACTORY_TEMPLATE = "BeanFactory.tmpl";
    private static final String BEAN_FACTORY_FILENAME = "org.jboss.as.console.client.shared.BeanFactory";

    private static final String SUBSYSTEM_FILENAME = "org.jboss.as.console.client.plugins.SubsystemRegistryImpl";
    private static final String SUBSYSTEM_TEMPLATE = "SubsystemExtensions.tmpl";

    private static final String RUNTIME_FILENAME = "org.jboss.as.console.client.plugins.RuntimeLHSItemExtensionRegistryImpl";
    private static final String RUNTIME_TEMPLATE = "RuntimeExtensions.tmpl";

    private static final String MODULE_FILENAME = "App.gwt.xml";
    private static final String MODULE_DEV_FILENAME = "App_dev.gwt.xml";
    private static final String MODULE_PRODUCT_FILENAME = "App_RH.gwt.xml";
    private static final String MODULE_PACKAGENAME = "org.jboss.as.console.composite";
    private static final String MODULE_TEMPLATE = "App.gwt.xml.tmpl";
    private static final String MODULE_DEV_TEMPLATE = "App_dev.gwt.xml.tmpl";
    private static final String MODULE_PRODUCT_TEMPLATE = "App_RH.gwt.xml.tmpl";


    private Filer filer;
    private Messager messager;
    private ProcessingEnvironment processingEnv;
    private List<String> discoveredExtensions;
    private List<ExtensionDeclaration> discoveredBindings;
    private List<String> discoveredBeanFactories;
    private List<String> categoryClasses;
    private List<SubsystemExtensionMetaData> subsystemDeclararions;
    private List<RuntimeExtensionMetaData> runtimeExtensions;
    private Set<String> modules = new LinkedHashSet<String>();
    private Set<String> nameTokens;
    private HashMap<String, String> gwtConfigProps;

    @Override
    public void init(ProcessingEnvironment env) {
        this.processingEnv = env;
        this.filer = env.getFiler();
        this.messager = env.getMessager();
        this.discoveredExtensions = new ArrayList<String>();
        this.discoveredBindings= new ArrayList<ExtensionDeclaration>();
        this.discoveredBeanFactories = new ArrayList<String>();
        this.categoryClasses = new ArrayList<String>();
        this.subsystemDeclararions = new ArrayList<SubsystemExtensionMetaData>();
        this.runtimeExtensions = new ArrayList<RuntimeExtensionMetaData>();
        this.nameTokens = new HashSet<String>();


        parseGwtProperties();
    }

    private void parseGwtProperties() {
        // GWT config properties
        Map<String, String> options = processingEnv.getOptions();
        gwtConfigProps = new HashMap<String, String>();
        for(String key : options.keySet())
        {
            if(key.startsWith("gwt."))
            {
                gwtConfigProps.put(key.substring(4, key.length()), options.get(key));
            }
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<String>();
        types.add(GinExtension.class.getName());
        types.add(GinExtensionBinding.class.getName());
        types.add(BeanFactoryExtension.class.getName());
        types.add(SubsystemExtension.class.getName());
        types.add(RuntimeExtension.class.getName());
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment roundEnv) {

        if(!roundEnv.processingOver()) {
            System.out.println("Begin Components discovery ...");

            Set<? extends Element> extensionElements = roundEnv.getElementsAnnotatedWith(GinExtension.class);

            for (Element element: extensionElements)
            {
                handleGinExtensionElement(element);
            }

            System.out.println("Begin Bindings discovery ...");

            Set<? extends Element> extensionBindingElements = roundEnv.getElementsAnnotatedWith(GinExtensionBinding.class);

            for (Element element: extensionBindingElements)
            {
                handleGinExtensionBindingElement(element);
            }

            System.out.println("Begin BeanFactory discovery ...");

            Set<? extends Element> beanFactoryElements = roundEnv.getElementsAnnotatedWith(BeanFactoryExtension.class);

            for (Element element: beanFactoryElements)
            {
                handleBeanFactoryElement(element);
            }

            System.out.println("Begin Subsystem discovery ...");

            Set<? extends Element> subsystemElements = roundEnv.getElementsAnnotatedWith(SubsystemExtension.class);

            for (Element element: subsystemElements)
            {
                handleSubsystemElement(element);
            }

            System.out.println("Begin Runtime Extension discovery ...");

            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(RuntimeExtension.class);

            for (Element element: elements)
            {
                handleRuntimeExtensions(element);
            }

        }

        if (roundEnv.processingOver())
        {
            try {
                // generate the actual implementation
                writeFiles();

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to process SPI artifacts");
            }

            System.out.println("SPI component discovery completed.");
        }

        return true;
    }

    private void handleGinExtensionBindingElement(Element element) {
        String typeName = element.asType().toString();
        System.out.println("Binding: "+typeName);
        discoveredBindings.add(new ExtensionDeclaration(typeName));
    }

    private void handleRuntimeExtensions(Element element) {
        List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();

        for (AnnotationMirror mirror: annotationMirrors)
        {
            final String annotationType = mirror.getAnnotationType().toString();

            if ( annotationType.equals(RuntimeExtension.class.getName()) )
            {
                NameToken nameToken = element.getAnnotation(NameToken.class);
                RuntimeExtension extension = element.getAnnotation(RuntimeExtension.class);

                if(nameToken!=null)   {
                    System.out.println("Runtime Extension: " + extension.name() +" -> "+nameToken.value());

                    RuntimeExtensionMetaData declared = new RuntimeExtensionMetaData(
                            extension.name(), nameToken.value(),
                            extension.group(), extension.key()
                    );

                    runtimeExtensions.add(declared);
                }
            }
        }
    }

    private void handleGinExtensionElement(Element element) {
        List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();

        for (AnnotationMirror mirror: annotationMirrors)
        {
            final String annotationType = mirror.getAnnotationType().toString();

            if ( annotationType.equals(GinExtension.class.getName()) )
            {
                GinExtension comps  = element.getAnnotation(GinExtension.class);

                final String module = comps.value();
                if (module != null && module.length() > 0) {
                    modules.add(module);
                }

                PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(element);
                String fqn = packageElement.getQualifiedName().toString()+"."+
                        element.getSimpleName().toString();
                System.out.println("Components: " + fqn);
                discoveredExtensions.add(fqn);
            }
        }
    }

    private void handleBeanFactoryElement(Element element) {
        List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();

        for (AnnotationMirror mirror: annotationMirrors)
        {
            final String annotationType = mirror.getAnnotationType().toString();

            if ( annotationType.equals(BeanFactoryExtension.class.getName()) )
            {
                BeanFactoryExtension factory  = element.getAnnotation(BeanFactoryExtension.class);
                PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(element);
                String fqn = packageElement.getQualifiedName().toString()+"."+
                        element.getSimpleName().toString();
                System.out.println("Factory: " + fqn);
                discoveredBeanFactories.add(fqn);
            } else if (annotationType.equals("com.google.web.bindery.autobean.shared.AutoBeanFactory.Category")) {
                final Collection<? extends AnnotationValue> values = mirror.getElementValues().values();
                if (values.size() > 0) {
                    for (AnnotationValue categoryClass : (List<? extends AnnotationValue>)values.iterator().next().getValue()) {
                        categoryClasses.add(((TypeMirror)categoryClass.getValue()).toString());
                    }
                }
            }
        }
    }

    private void handleSubsystemElement(Element element) {
        List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();

        for (AnnotationMirror mirror: annotationMirrors)
        {
            final String annotationType = mirror.getAnnotationType().toString();

            if ( annotationType.equals(SubsystemExtension.class.getName()) )
            {
                NameToken nameToken = element.getAnnotation(NameToken.class);
                SubsystemExtension subsystem = element.getAnnotation(SubsystemExtension.class);

                if(nameToken!=null)   {
                    System.out.println("Subsystem: " + subsystem.name() +" -> "+nameToken.value());

                    SubsystemExtensionMetaData declared = new SubsystemExtensionMetaData(
                            subsystem.name(), nameToken.value(),
                            subsystem.group(), subsystem.key()
                    );

                    subsystemDeclararions.add(declared);
                    if (!nameTokens.add(nameToken.value())) {
                        throw new RuntimeException("Duplicate name token '" + nameToken.value() + "' declared on '"
                                + element.asType());
                    }
                }
            }
        }
    }


    private void writeFiles() throws Exception {
        writeGinjectorFile();
        writeBindingFile();
        writeBeanFactoryFile();
        writeSubsystemFile();
        writeRuntimeFile();
        writeModuleFile();
        writeDevModuleFile();
        writeProductModuleFile();
        writeProxyConfigurations();
    }

    private void writeRuntimeFile() throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("runtimeMenuItemExtensions", runtimeExtensions);

        JavaFileObject sourceFile = filer.createSourceFile(RUNTIME_FILENAME);
        OutputStream output = sourceFile.openOutputStream();
        new TemplateProcessor().process(RUNTIME_TEMPLATE, model, output);
        output.flush();
        output.close();
    }

    private void writeSubsystemFile() throws Exception{
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("subsystemExtensions", subsystemDeclararions);

        JavaFileObject sourceFile = filer.createSourceFile(SUBSYSTEM_FILENAME);
        OutputStream output = sourceFile.openOutputStream();
        new TemplateProcessor().process(SUBSYSTEM_TEMPLATE, model, output);
        output.flush();
        output.close();
    }

    private void writeBeanFactoryFile() throws Exception{
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("extensions", discoveredBeanFactories);
        model.put("categoryClasses", categoryClasses);

        JavaFileObject sourceFile = filer.createSourceFile(BEAN_FACTORY_FILENAME);
        OutputStream output = sourceFile.openOutputStream();
        new TemplateProcessor().process(BEAN_FACTORY_TEMPLATE, model, output);
        output.flush();
        output.close();
    }

    private void writeBindingFile() throws Exception {
        JavaFileObject sourceFile = filer.createSourceFile(BINDING_FILENAME);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("extensions", discoveredBindings);

        OutputStream output = sourceFile.openOutputStream();
        new TemplateProcessor().process(BINDING_TEMPLATE, model, output);

        output.flush();
        output.close();
    }

    private void writeGinjectorFile() throws Exception {

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("extensions", discoveredExtensions);

        JavaFileObject sourceFile = filer.createSourceFile(EXTENSION_FILENAME);
        OutputStream output = sourceFile.openOutputStream();
        new TemplateProcessor().process(EXTENSION_TEMPLATE, model, output);
        output.flush();
        output.close();

    }

    private void writeModuleFile() {

        try
        {
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("modules", modules);
            model.put("properties", gwtConfigProps);

            FileObject sourceFile = filer.createResource(StandardLocation.SOURCE_OUTPUT, MODULE_PACKAGENAME,
                    MODULE_FILENAME);
            OutputStream output = sourceFile.openOutputStream();
            new TemplateProcessor().process(MODULE_TEMPLATE, model, output);
            output.flush();
            output.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to create file", e);
        }
    }

    private void writeDevModuleFile() {

        try
        {
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("modules", modules);
            model.put("properties", gwtConfigProps);

            FileObject sourceFile = filer.createResource(StandardLocation.SOURCE_OUTPUT, MODULE_PACKAGENAME,
                    MODULE_DEV_FILENAME);
            OutputStream output = sourceFile.openOutputStream();
            new TemplateProcessor().process(MODULE_DEV_TEMPLATE, model, output);
            output.flush();
            output.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to create file", e);
        }
    }

    private void writeProductModuleFile() {

        try
        {
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("modules", modules);
            model.put("properties", gwtConfigProps);

            FileObject sourceFile = filer.createResource(StandardLocation.SOURCE_OUTPUT, MODULE_PACKAGENAME,
                    MODULE_PRODUCT_FILENAME);
            OutputStream output = sourceFile.openOutputStream();
            new TemplateProcessor().process(MODULE_PRODUCT_TEMPLATE, model, output);
            output.flush();
            output.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to create file", e);
        }
    }


    private void writeProxyConfigurations() {

        try
        {
            String devHostUrl = gwtConfigProps.get("console.dev.host") != null ?
                    gwtConfigProps.get("console.dev.host") : "127.0.0.1";

            Map<String, Object> model = new HashMap<String, Object>();
            model.put("devHost", devHostUrl);

            FileObject sourceFile = filer.createResource(
                    StandardLocation.SOURCE_OUTPUT, "", "gwt-proxy.properties");
            OutputStream output1 = sourceFile.openOutputStream();

            FileObject sourceFile2 = filer.createResource(
                    StandardLocation.SOURCE_OUTPUT, "", "upload-proxy.properties");
            OutputStream output2 = sourceFile2.openOutputStream();

            FileObject sourceFile3 = filer.createResource(
                    StandardLocation.SOURCE_OUTPUT, "", "logout.properties");
            OutputStream output3 = sourceFile3.openOutputStream();

            new TemplateProcessor().process("gwt.proxy.tmpl", model, output1);
            new TemplateProcessor().process("gwt.proxy.upload.tmpl", model, output2);
            new TemplateProcessor().process("gwt.proxy.logout.tmpl", model, output3);

            output1.close();
            output2.close();
            output3.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to create file", e);
        }
    }
}
