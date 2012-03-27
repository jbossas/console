package org.jboss.as.console.spi;

/**
 * @author Heiko Braun
 * @date 3/26/12
 */
public class ExtensionDeclaration {
    private String type;

    public ExtensionDeclaration(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}