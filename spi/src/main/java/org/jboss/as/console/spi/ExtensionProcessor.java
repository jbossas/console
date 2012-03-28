package org.jboss.as.console.spi;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static javax.lang.model.SourceVersion.RELEASE_6;

@SupportedSourceVersion(RELEASE_6)
public class ExtensionProcessor extends AbstractProcessor {

    private static final String TEMPLATE = "Extension.tmpl";
    private static final String FILENAME = "org.jboss.as.console.client.core.gin.Composite";


    private Filer filer;
    private Messager messager;
    private ProcessingEnvironment processingEnv;
    List<String> discovered;


    @Override
    public void init(ProcessingEnvironment env) {
        this.processingEnv = env;
        this.filer = env.getFiler();
        this.messager = env.getMessager();
        this.discovered = new ArrayList<String>();

    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<String>();
        types.add(GinExtension.class.getName());
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if(!roundEnv.processingOver()) {
            System.out.println("Begin Components discovery ...");

            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(GinExtension.class);

            for (Element element: elements)
            {
                handleElement(element);
            }

        }

        if (roundEnv.processingOver())
        {
            try {
                // generate the actual implementation
                writeFile();

            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Components discovery completed.");
        }

        return true;
    }

    private void handleElement(Element element) {
        List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();

        for (AnnotationMirror mirror: annotationMirrors)
        {
            final String annotationType = mirror.getAnnotationType().toString();

            if ( annotationType.equals(GinExtension.class.getName()) )
            {
                GinExtension comps  = element.getAnnotation(GinExtension.class);

                PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(element);
                String fqn = packageElement.getQualifiedName().toString()+"."+
                        element.getSimpleName().toString();
                System.out.println("Components: " + fqn);
                discovered.add(fqn);
            }
        }
    }

    private void writeFile() {

        try
        {
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("extensions", discovered);

            JavaFileObject sourceFile = filer.createSourceFile(FILENAME);
            OutputStream output = sourceFile.openOutputStream();
            new TemplateProcessor().process(TEMPLATE, model, output);
            output.flush();
            output.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to create file", e);
        }
    }
}

