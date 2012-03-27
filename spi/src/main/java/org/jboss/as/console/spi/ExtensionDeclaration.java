package org.jboss.as.console.spi;

/**
 * @author Heiko Braun
 * @date 3/26/12
 */
class ExtensionDeclaration {
    String type;

    ExtensionDeclaration(String type) {
        this.type = type;
    }
}