package org.jboss.as.console.client.domain.groups;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.widgets.StackSectionHeader;
import org.jboss.as.console.client.widgets.stack.DefaultStackLayoutPanel;

import java.util.List;

/**
 * LHS serber group management navigation.
 *
 * @author Heiko Braun
 * @date 2/11/11
 */
class LHSServerGroupNavigation {

    private StackLayoutPanel stack;

    private ServerGroupSection serverGroupSection;

    public LHSServerGroupNavigation() {

        stack = new DefaultStackLayoutPanel();

        serverGroupSection = new ServerGroupSection();
        stack.add(serverGroupSection.asWidget(), new StackSectionHeader("Server Groups"), 28);

        DeploymentSection deploymentSection = new DeploymentSection();
        stack.add(deploymentSection.asWidget(), new StackSectionHeader("Deployments"), 28);

    }

    public Widget asWidget()
    {
        return stack;
    }

    public void updateFrom(List<ServerGroupRecord> serverGroupRecords) {
        serverGroupSection.updateFrom(serverGroupRecords);
    }

}
