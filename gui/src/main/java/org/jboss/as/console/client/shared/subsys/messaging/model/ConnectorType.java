package org.jboss.as.console.client.shared.subsys.messaging.model;

/**
 * @author Heiko Braun
 * @date 4/4/12
 */
public enum ConnectorType {
    GENERIC("connector"), REMOTE("remote-connector"), INVM("in-vm-connector");

    private String resource;

    public String getResource() {
        return resource;
    }

    ConnectorType(String resource) {
        this.resource = resource;
    }
}
