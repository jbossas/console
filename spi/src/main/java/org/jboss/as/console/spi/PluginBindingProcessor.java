package org.jboss.as.console.spi;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static javax.lang.model.SourceVersion.RELEASE_6;

/**
 * Discovers {@link PluginBinding}'s and dynamically creates a GinModule
 * implementation, that installs each discovered module.
 */
@SupportedSourceVersion(RELEASE_6)
public class PluginBindingProcessor extends AbstractProcessor {

    private static final String TEMPLATE = "PluginBinding.tmpl";

    private final static String FINAL_MODULE_NAME = "console.spi.extension.class";
    private final static String DEFAULT_MODULE_NAME = "org.jboss.as.console.app.client.PluginModule";

    private Filer filer;
	private Messager messager;
    private ProcessingEnvironment processingEnv;
    List<ExtensionDeclaration> discovered;
    private String finalModule;

    @Override
	public void init(ProcessingEnvironment env) {
		this.processingEnv = env;
        this.filer = env.getFiler();
		this.messager = env.getMessager();
        this.discovered = new ArrayList<ExtensionDeclaration>();
        this.finalModule = env.getOptions().get(FINAL_MODULE_NAME) !=null ?
                env.getOptions().get(FINAL_MODULE_NAME) : DEFAULT_MODULE_NAME;

    }

    @Override
    public Set<String> getSupportedOptions() {
        Set<String> types = new HashSet<String>();
        types.add(FINAL_MODULE_NAME);
        return types;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<String>();
        types.add(PluginBinding.class.getName());
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if(!roundEnv.processingOver()) {
            System.out.println("Begin Module discovery ...");

            Set<? extends Element> elements = roundEnv.getRootElements();

            for (Element element: elements)
            {
                handleRootElementAnnotationMirrors(element, discovered);
            }

        }

        if (roundEnv.processingOver())
        {
            try {
                // generate the actual implementation
                writeModuleFile();

            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Module discovery completed.");
        }

        return true;
    }

    private void handleRootElementAnnotationMirrors(Element aElement, List<ExtensionDeclaration> discovered)
    {
        List<? extends AnnotationMirror> annotationMirrors = aElement.getAnnotationMirrors();

        for (AnnotationMirror mirror: annotationMirrors)
        {
            final String annotationType = mirror.getAnnotationType().toString();

            if ( annotationType.equals(PluginBinding.class.getName()) )
            {
                discovered.add(new ExtensionDeclaration(aElement.asType().toString()));
            }
        }
    }

    private void writeModuleFile() throws Exception {

        JavaFileObject sourceFile = filer.createSourceFile(finalModule);

        String simpleName = finalModule.substring(finalModule.lastIndexOf(".")+1, finalModule.length());
        String packageName = finalModule.substring(0, finalModule.lastIndexOf("."));

        System.out.println("Module file: " + packageName+"."+simpleName);

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("packageName", packageName);
        model.put("className", simpleName);
        model.put("extensions", discovered);

        OutputStream output = sourceFile.openOutputStream();
        new TemplateProcessor().process(TEMPLATE, model, output);

        output.flush();
        output.close();

    }
}
