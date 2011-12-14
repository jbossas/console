package org.jboss.as.console.server.proxy;

import java.io.IOException;

/**
 * @author Heiko Braun
 * @date 12/14/11
 */
public class RedirectException extends IOException {

    String location;

    public RedirectException(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }
}
