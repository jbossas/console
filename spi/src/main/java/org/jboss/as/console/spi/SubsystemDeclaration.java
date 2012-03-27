package org.jboss.as.console.spi;

/**
 * @author Heiko Braun
 * @date 3/26/12
 */
public class SubsystemDeclaration {
    private String type;
    private String token;

    public SubsystemDeclaration(String type, String token) {
        this.type = type;
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public String getToken() {
        return token;
    }
}
