package org.jboss.as.console.spi;

/**
 * @author Heiko Braun
 * @date 3/27/12
 */
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({java.lang.annotation.ElementType.TYPE})
public @interface GinExtension {

    /**
     * @return the GWT module name containing this extension,
     * e.g. org.jboss.as.console.example.Extension.
     */
    String value();
}
