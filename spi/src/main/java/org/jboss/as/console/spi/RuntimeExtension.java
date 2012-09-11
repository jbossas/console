package org.jboss.as.console.spi;

import org.jboss.as.console.client.plugins.RuntimeGroup;

/**
 * @author Heiko Braun
 * @date 3/26/12
 */
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({java.lang.annotation.ElementType.TYPE})
public @interface RuntimeExtension {

    String name();
    String group() default RuntimeGroup.METRICS;
    String key();
}
