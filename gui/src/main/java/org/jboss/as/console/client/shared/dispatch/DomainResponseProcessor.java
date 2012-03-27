package org.jboss.as.console.client.shared.dispatch;

import org.jboss.as.console.client.shared.state.ReloadState;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 1/17/12
 */
public class DomainResponseProcessor implements ResponseProcessor {

    private static final String RESPONSE_HEADERS = "response-headers";
    private static final String PROCESS_STATE = "process-state";

    private static final String RESTART_REQUIRED = "restart-required";
    private static final String RELOAD_REQUIRED = "reload-required";
    private static final String SERVER_GROUPS = "server-groups";
    private static final String RESPONSE = "response";

    private ReloadState reloadState;

    @Inject
    public DomainResponseProcessor(ReloadState reloadState) {
        this.reloadState = reloadState;
    }

    @Override
    public void process(ModelNode response) {

        parseServerState(response, reloadState);

        reloadState.propagateChanges();

    }

    private static boolean parseServerState(ModelNode response, ReloadState reloadState) {
        boolean staleModel = false;

        if(response.hasDefined(SERVER_GROUPS))
        {
            List<Property> serverGroups = response.get(SERVER_GROUPS).asPropertyList();
            for(Property serverGroup : serverGroups)
            {
                ModelNode serverGroupValue = serverGroup.getValue();

                List<Property> servers = serverGroupValue.asPropertyList();
                for(Property server : servers)
                {
                    ModelNode serverValue = server.getValue();
                    ModelNode serverResponse = serverValue.get(RESPONSE);

                    if(serverResponse.hasDefined(RESPONSE_HEADERS))
                    {
                        List<Property> headers = serverResponse.get(RESPONSE_HEADERS).asPropertyList();
                        for(Property header : headers)
                        {
                            if(PROCESS_STATE.equals(header.getName()))
                            {
                                String headerValue = header.getValue().asString();

                                if(RESTART_REQUIRED.equals(headerValue))
                                {
                                    staleModel=true;
                                    reloadState.setRestartRequired(server.getName(), staleModel);
                                }
                                else if(RELOAD_REQUIRED.equals(headerValue))
                                {
                                    staleModel=true;
                                    reloadState.setReloadRequired(server.getName(), staleModel);
                                }

                                System.out.println(server.getName() +" >> "+staleModel);
                            }
                        }
                    }
                }
            }
        }

        return staleModel;
    }
}
