package org.jboss.as.console.spi;

import com.gwtplatform.mvp.client.annotations.NameToken;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.lang.model.SourceVersion.RELEASE_6;

/**
 * @author Heiko Braun
 * @date 3/23/12
 */
@SupportedSourceVersion(RELEASE_6)
public class SubsystemProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;
    private ProcessingEnvironment env;
    private List<SubsystemDeclaration> declararions = new ArrayList<SubsystemDeclaration>();

    @Override
    public void init(ProcessingEnvironment env) {
        this.env = env;
        this.filer = env.getFiler();
        this.messager = env.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<String>();
        types.add(Subsystem.class.getName());
        types.add(NameToken.class.getName());
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment roundEnv) {

        if(!roundEnv.processingOver()) {
            System.out.println("Begin Subsystem discovery ...");

            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Subsystem.class);

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

            System.out.println("Subsystem discovery completed.");
        }

        return true;
    }

    private void handleElement(Element element) {
        List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();

        for (AnnotationMirror mirror: annotationMirrors)
        {
            final String annotationType = mirror.getAnnotationType().toString();

            if ( annotationType.equals(Subsystem.class.getName()) )
            {
                NameToken nameToken = element.getAnnotation(NameToken.class);
                if(nameToken!=null)   {
                    System.out.println("Subsystem: " + nameToken.value() +" -> "+element.getSimpleName());

                    SubsystemDeclaration declared = new SubsystemDeclaration(
                            element.getSimpleName().toString(), nameToken.value()
                    );

                    declararions.add(declared);
                }
            }
        }
    }

    private void writeFile() {

    }
}
