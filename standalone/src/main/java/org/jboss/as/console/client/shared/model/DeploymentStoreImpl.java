package org.jboss.as.console.client.shared.model;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/18/11
 */
public class DeploymentStoreImpl implements DeploymentStore {
    @Override
    public void loadDeployments(AsyncCallback<List<DeploymentRecord>> callback) {

    }

    @Override
    public void deleteDeployment(DeploymentRecord deploymentRecord, AsyncCallback<Boolean> callback) {

    }
}
