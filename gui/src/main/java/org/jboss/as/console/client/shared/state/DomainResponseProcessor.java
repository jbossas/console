package org.jboss.as.console.client.shared.state;

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
                //System.out.println("\n\n");
                ModelNode serverGroupValue = serverGroup.getValue();
                //System.out.println("server-group: "+serverGroup.getName());

                //  -- Server Group --
                if(serverGroupValue.hasDefined("host"))
                {
                    List<Property> hosts = serverGroupValue.get("host").asPropertyList();
                    for(Property host : hosts)
                    {
                        // -- Host  --

                        ModelNode hostValue = host.getValue();
                        //System.out.println("host: "+host.getName());
                        parseHost(host.getName(), hostValue, reloadState);
                    }
                }
            }
        }

        return staleModel;
    }

    private static void parseHost(final String hostName, ModelNode hostValue, ReloadState reloadState) {

        //System.out.println(hostValue);

        List<Property> servers = hostValue.asPropertyList();

        for(Property server : servers)
        {

            // -- Server  --
            //System.out.println("server: "+ server.getName());

            ModelNode serverResponse = server.getValue().get(RESPONSE);

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
                            reloadState.setRestartRequired(
                                    "Host: "+hostName+", server: "+server.getName(),
                                    true
                            );
                        }
                        else if(RELOAD_REQUIRED.equals(headerValue))
                        {
                            reloadState.setReloadRequired(
                                    "Host: "+hostName+", server: "+server.getName(),
                                    true
                            );
                        }
                    }
                }
            }
        }
    }
}
