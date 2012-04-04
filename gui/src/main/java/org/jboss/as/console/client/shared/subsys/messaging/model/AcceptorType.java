package org.jboss.as.console.client.shared.subsys.messaging.model;

/**
 * @author Heiko Braun
 * @date 4/4/12
 */
public enum AcceptorType {
    GENERIC("acceptor"), REMOTE("remote-acceptor"), INVM("in-vm-acceptor");

    private String resource;

    public String getResource() {
        return resource;
    }

    AcceptorType(String resource) {
        this.resource = resource;
    }
}
