package org.jboss.as.console.client.domain.model;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/2/11
 */
public interface HostInformationStore {
    void getHosts(AsyncCallback<List<Host>> callback);
    void getServerConfigurations(String name, AsyncCallback<List<Server>> callback);
    void getServerInstances(String host, AsyncCallback<List<ServerInstance>> callback);
    void loadServerConfig(String host, String serverConfig, final AsyncCallback<Server> callback);
    void getVirtualMachines(String host, final AsyncCallback<List<String>> callback) ;
}
