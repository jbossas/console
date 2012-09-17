package org.jboss.as.console.client.plugins;

/**
 * @author Heiko Braun
 * @date 3/26/12
 */
public class RuntimeExtensionmetaData {

    private String token;
    private String name;
    private String group;
    private String key;

    public RuntimeExtensionmetaData(String name, String token, String group, String key) {
        this.name = name;
        this.token = token;
        this.group = group;
        this.key = key;
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

    public String getKey() {
        return key;
    }
}
