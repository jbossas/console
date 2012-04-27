package org.jboss.as.console.server.proxy;

import java.io.IOException;

/**
 * @author Heiko Braun
 * @date 4/27/12
 */
public class ServiceUnavailableException extends IOException {

    private String retryAfter;

    public ServiceUnavailableException(String retryAfter) {
        super();
        this.retryAfter = retryAfter;
    }

    public String getRetryAfter() {
        return retryAfter;
    }
}
