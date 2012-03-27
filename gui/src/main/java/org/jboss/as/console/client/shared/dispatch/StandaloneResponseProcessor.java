package org.jboss.as.console.client.shared.dispatch;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.state.ReloadState;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 1/17/12
 */
public class StandaloneResponseProcessor implements ResponseProcessor {

    private static final String RESPONSE_HEADERS = "response-headers";
    private static final String PROCESS_STATE = "process-state";

    private static final String RESTART_REQUIRED = "restart-required";
    private static final String RELOAD_REQUIRED = "reload-required";
    private static final String STANDLONE_SERVER = "Standlone Server";

    private ReloadState reloadState;

    @Inject
    public StandaloneResponseProcessor(ReloadState reloadState) {
        this.reloadState = reloadState;
    }

    @Override
    public void process(ModelNode response) {

        boolean staleModel = parseServerState(response, reloadState);

        reloadState.propagateChanges();

        if(!staleModel) reloadState.reset();

    }

    private static boolean parseServerState(ModelNode response, ReloadState reloadState) {

        boolean staleModel = false;

        if(response.hasDefined(RESPONSE_HEADERS))
        {
            List<Property> headers = response.get(RESPONSE_HEADERS).asPropertyList();

            for(Property header : headers)
            {
                if(PROCESS_STATE.equals(header.getName()))
                {

                    String headerValue = header.getValue().asString();
                    if(RESTART_REQUIRED.equals(headerValue))
                    {
                        staleModel=true;
                        reloadState.setRestartRequired(STANDLONE_SERVER, staleModel);
                    }
                    else if(RELOAD_REQUIRED.equals(headerValue))
                    {
                        staleModel=true;
                        reloadState.setReloadRequired(STANDLONE_SERVER, staleModel);
                    }
                }
            }

        }
        return staleModel;
    }
}
