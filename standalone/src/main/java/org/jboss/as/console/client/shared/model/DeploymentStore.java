package org.jboss.as.console.client.shared.model;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

/**
 * Responsible for loading deployment data
 * and turning it a usable representation.
 *
 * @author Heiko Braun
 * @date 1/31/11
 */
public interface DeploymentStore {
    void loadDeployments(AsyncCallback<List<DeploymentRecord>> callback);

    void deleteDeployment(DeploymentRecord deploymentRecord, AsyncCallback<Boolean> callback);
}
