package org.jboss.as.console.client.shared;

import com.allen_sauer.gwt.log.client.Log;

import java.util.Date;

/**
 * @author Heiko Braun
 * @date 1/31/11
 */
public class MockDeploymentStoreImpl implements DeploymentStore {

    private final DeploymentRecord[] records = new DeploymentRecord[]
            {
                    new DeploymentRecord("ols.war", "onlineStore.war", "7a2d28fc ..."),
                    new DeploymentRecord("backOfficeApp.war", "backOfficeApplication.war", "ed849ee1 ..."),
                    new DeploymentRecord("mon-1.0.war", "monitor.war", "2fd4e1c6 ...")
            };

    @Override
    public DeploymentRecord[] loadDeployments() {
        Log.debug("Loaded " + records.length +" deployment records");
        return records;
    }
}
