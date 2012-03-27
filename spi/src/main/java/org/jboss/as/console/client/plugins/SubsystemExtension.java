package org.jboss.as.console.client.plugins;

/**
 * @author Heiko Braun
 * @date 3/26/12
 */
public class SubsystemExtension {
    private String token;
    private String name;
    private String group;

    public SubsystemExtension(String name, String token, String group) {
        this.name = name;
        this.token = token;
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    public String getToken() {
        return token;
    }
}
