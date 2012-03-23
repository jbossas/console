package org.jboss.as.console.plugins;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.AnnotationTypeElementDeclaration;
import com.sun.mirror.declaration.AnnotationValue;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.util.SourcePosition;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author Heiko Braun
 * @date 3/23/12
 */
public class GinModuleProcessor implements AnnotationProcessor {

    private Set<AnnotationTypeDeclaration> typeDeclarations;
    private AnnotationProcessorEnvironment environment;
    private AnnotationTypeDeclaration ginModule;

    public GinModuleProcessor(Set<AnnotationTypeDeclaration> typeDeclarations, AnnotationProcessorEnvironment environment) {
        this.typeDeclarations = typeDeclarations;
        this.environment = environment;

        ginModule = (AnnotationTypeDeclaration) environment
				.getTypeDeclaration("com.google.gwt.inject.client.GinModules");

    }

    @Override
    public void process() {

        Collection<Declaration> declarations = environment.getDeclarationsAnnotatedWith(ginModule);

        for (Declaration declaration : declarations) {
            processNoteAnnotations(declaration);
        }
    }


    private void processNoteAnnotations(Declaration declaration) {
		// Get all of the annotation usage for this declaration.
		// the annotation mirror is a reflection of what is in the source.
		Collection<AnnotationMirror> annotations = declaration.getAnnotationMirrors();
		// iterate over the mirrors.
		for (AnnotationMirror mirror : annotations) {
			// if the mirror in this iteration is for our note declaration...
			if(mirror.getAnnotationType().getDeclaration().equals(ginModule)) {

				// print out the goodies.
				SourcePosition position = mirror.getPosition();
				Map<AnnotationTypeElementDeclaration, AnnotationValue> values = mirror.getElementValues();

				System.out.println("Declaration: " + declaration.toString());
				System.out.println("Position: " + position);
				System.out.println("Values:");

				for (Map.Entry<AnnotationTypeElementDeclaration, AnnotationValue> entry : values.entrySet()) {
					AnnotationTypeElementDeclaration elemDecl = entry.getKey();
					AnnotationValue value = entry.getValue();
					System.out.println("    " + elemDecl + "=" + value);
				}
			}
		}
	}

}
