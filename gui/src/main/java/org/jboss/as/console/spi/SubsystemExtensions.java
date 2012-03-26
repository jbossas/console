package org.jboss.as.console.spi;

import com.gwtplatform.mvp.client.annotations.NameToken;
import org.apache.maven.plugin.logging.Log;
import org.jboss.ballroom.codegen.mojo.TemplateProcessor;
import org.jboss.errai.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Heiko Braun
 * @date 3/23/12
 */
public class SubsystemExtensions implements TemplateProcessor {

    @Override
    public void process(Reflections scanner, Map<String, List> model, ClassLoader loader, Log log) {

        try
        {
            log.info("--- Searching for Subsystem extensions ... ---");

            Set<Class<?>> subsystems = scanner.getTypesAnnotatedWith(Subsystem.class);
            List<SubsystemDeclaration> classNames = new ArrayList<SubsystemDeclaration>(subsystems.size());
            for(Class<?> clazz : subsystems)
            {

                if(!com.gwtplatform.mvp.client.proxy.Proxy.class.isAssignableFrom(clazz))
                {
                    log.warn("com.gwtplatform.mvp.client.proxy.Proxy not assignable from " + clazz + ". Will be ignored ...");
                    continue;
                }

                String type = clazz.getCanonicalName();
                log.info("Found " + type);

                NameToken tokenAnnotation = clazz.getAnnotation(NameToken.class);
                String token = tokenAnnotation !=null ? tokenAnnotation.value() : "not-set";
                SubsystemDeclaration subsys = new SubsystemDeclaration(type, token);
                classNames.add(subsys);
            }

            model.put("subsystemExtensions", classNames);

        }
        catch (Exception e) {
            log.error("Failed to process classes", e);
        }
    }
}
