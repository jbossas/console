package org.jboss.as.console.client.shared.dispatch;

import org.jboss.as.console.client.Console;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 1/17/12
 */
public class StandaloneResponseProcessor implements ResponseProcessor {

    private static final String RESPONSE_HEADERS = "response-headers";
    private static final String PROCESS_STATE = "process-state";
    private static final String RELOAD_REQUIRED = "reload-required";

    @Override
    public void process(ModelNode response) {
        // check reload state
        Console.MODULES.getReloadState().setReloadRequired(
                parseReloadRequired(response)
        );

    }

    private static boolean parseReloadRequired(ModelNode response) {
        boolean hasReloadFlag = false;
        if(response.hasDefined(RESPONSE_HEADERS))
        {
            List<Property> headers = response.get(RESPONSE_HEADERS).asPropertyList();
            for(Property header : headers) {
                if(PROCESS_STATE.equals(header.getName())) {
                    if(RELOAD_REQUIRED.equals(header.getValue().asString())) {
                        hasReloadFlag=true;
                    }
                }
            }
        }
        return hasReloadFlag;
    }
}
