package org.jboss.as.console.client.shared.state;

import org.jboss.dmr.client.ModelNode;

/**
 * @author Heiko Braun
 * @date 1/17/12
 */
public interface ResponseProcessor {

    void process(ModelNode response);
}
