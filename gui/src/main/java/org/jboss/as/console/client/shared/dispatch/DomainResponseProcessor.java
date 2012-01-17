package org.jboss.as.console.client.shared.dispatch;

import org.jboss.as.console.client.Console;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 1/17/12
 */
public class DomainResponseProcessor implements ResponseProcessor {

     private static final String RESPONSE_HEADERS = "response-headers";
    private static final String PROCESS_STATE = "process-state";
    private static final String RESTART_REQUIRED = "restart-required";

    @Override
    public void process(ModelNode response) {
        // check reload state
        Console.MODULES.getReloadState().setReloadRequired(
                parseReloadRequired(response)
        );

    }

    private static boolean parseReloadRequired(ModelNode response) {
        boolean hasReloadFlag = false;
        ModelNode result = response.get("result");
        if(result.hasDefined("server-groups"))
        {
            List<Property> serverGroups = result.get("server-groups").asPropertyList();
            for(Property serverGroup : serverGroups)
            {
                ModelNode serverGroupValue = serverGroup.getValue();

                List<Property> servers = serverGroupValue.asPropertyList();
                for(Property server : servers)
                {
                    ModelNode serverValue = server.getValue();

                    ModelNode serverResponse = serverValue.get("response");

                    System.out.println(serverResponse);

                    if(serverResponse.hasDefined(RESPONSE_HEADERS))
                    {
                        List<Property> headers = serverResponse.get(RESPONSE_HEADERS).asPropertyList();
                        for(Property header : headers) {
                            if(PROCESS_STATE.equals(header.getName())) {
                                if(RESTART_REQUIRED.equals(header.getValue().asString())) {
                                    hasReloadFlag=true;

                                    System.out.println(server.getName()+ ">"+hasReloadFlag);
                                }
                            }
                        }
                    }
                }
            }
        }
        return hasReloadFlag;
    }
}
