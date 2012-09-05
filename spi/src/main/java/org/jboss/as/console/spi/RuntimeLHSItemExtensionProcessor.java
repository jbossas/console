package org.jboss.as.console.spi;

import com.gwtplatform.mvp.client.annotations.NameToken;
import org.jboss.as.console.client.plugins.SubsystemExtension;

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
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static javax.lang.model.SourceVersion.RELEASE_6;

/**
 * Discovers runtime menu item extension points and populates a registry that will be used within the
 * console to dynamically build the LHS navigator.
 *
 * @author Heiko Braun
 * @date 3/23/12
 */
@SupportedSourceVersion(RELEASE_6)
public class RuntimeLHSItemExtensionProcessor extends AbstractProcessor {

    private static final String FILENAME = "org.jboss.as.console.client.plugins.RuntimeLHSItemExtensionRegistryImpl";
    private static final String TEMPLATE = "RuntimeLHSItemExtensions.tmpl";

    private Filer filer;
    private Messager messager;
    private ProcessingEnvironment env;
    private List<SubsystemExtension> declararions = new ArrayList<SubsystemExtension>();

    @Override
    public void init(ProcessingEnvironment env) {
        this.env = env;
        this.filer = env.getFiler();
        this.messager = env.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<String>();
        types.add(RuntimeLHSItemExtension.class.getName());
        types.add(NameToken.class.getName());
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment roundEnv) {

        if(!roundEnv.processingOver()) {
            System.out.println("Begin Runtime Menu item Extension discovery ...");

            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(RuntimeLHSItemExtension.class);

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

            System.out.println("Runtime Menu item Extension discovery completed.");
        }

        return true;
    }

    private void handleElement(Element element) {
        List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();

        for (AnnotationMirror mirror: annotationMirrors)
        {
            final String annotationType = mirror.getAnnotationType().toString();

            if ( annotationType.equals(RuntimeLHSItemExtension.class.getName()) )
            {
                NameToken nameToken = element.getAnnotation(NameToken.class);
                RuntimeLHSItemExtension extension = element.getAnnotation(RuntimeLHSItemExtension.class);

                if(nameToken!=null)   {
                    System.out.println("Runtime menu item: " + extension.name() +" -> "+nameToken.value());

                    SubsystemExtension declared = new SubsystemExtension(
                            extension.name(), nameToken.value(),
                            extension.group(), extension.key()
                    );

                    declararions.add(declared);
                }
            }
        }
    }

    private void writeFile() {

        try
        {
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("runtimeMenuItemExtensions", declararions);

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
