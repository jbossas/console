package org.jboss.as.console.client.server.deployments;

import com.allen_sauer.gwt.log.client.Log;

import java.util.Date;

/**
 * @author Heiko Braun
 * @date 1/31/11
 */
public class MockDeploymentStoreImpl implements DeploymentStore {

    private final DeploymentRecord[] records = new DeploymentRecord[]
            {
                    new DeploymentRecord("ABSC", "onlineStore.war", new Date()),
                    new DeploymentRecord("G6FF", "backOfficeApplication.war", new Date()),
                    new DeploymentRecord("DFFC", "monitor.war", new Date())
            };

    @Override
    public DeploymentRecord[] loadDeployments() {
        Log.debug("Loaded " + records.length +" deployment records");
        return records;
    }
}
