package org.jboss.as.console.plugins;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Heiko Braun
 * @date 3/23/12
 */
public class ProcessorFactory implements AnnotationProcessorFactory {
    @Override
    public Collection<String> supportedOptions() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Collection<String> supportedAnnotationTypes() {
        List<String> supported = new ArrayList<String>();
        supported.add("com.google.gwt.inject.client.GinModules");
        return supported;
    }

    @Override
    public AnnotationProcessor getProcessorFor(
            Set<AnnotationTypeDeclaration> typeDeclarations,
            AnnotationProcessorEnvironment environment) {
        return new GinModuleProcessor(typeDeclarations, environment);
    }
}
