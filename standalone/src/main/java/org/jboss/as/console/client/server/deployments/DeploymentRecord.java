package org.jboss.as.console.client.server.deployments;

import com.smartgwt.client.widgets.grid.ListGridRecord;

import java.util.Date;

/**
 * @author Heiko Braun
 * @date 1/31/11
 */
public class DeploymentRecord extends ListGridRecord {
    public DeploymentRecord(String key, String name, Date since) {
        setAttribute("key", key);
        setAttribute("deploymentName", name);
        setAttribute("since", since);
    }
}
