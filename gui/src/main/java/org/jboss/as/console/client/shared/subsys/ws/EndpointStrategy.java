package org.jboss.as.console.client.shared.subsys.ws;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.shared.subsys.ws.model.WebServiceEndpoint;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 6/10/11
 */
public interface EndpointStrategy {

    void refreshEndpoints(AsyncCallback<List<WebServiceEndpoint>> callback);
}
