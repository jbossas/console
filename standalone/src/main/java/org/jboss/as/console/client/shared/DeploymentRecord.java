package org.jboss.as.console.client.shared;

import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * @author Heiko Braun
 * @date 1/31/11
 */
public class DeploymentRecord extends ListGridRecord {
    public DeploymentRecord(String name, String runtimeName, String sha) {
        setAttribute("name", name);
        setAttribute("runtime-name", runtimeName);
        setAttribute("sha", sha);
    }
}
