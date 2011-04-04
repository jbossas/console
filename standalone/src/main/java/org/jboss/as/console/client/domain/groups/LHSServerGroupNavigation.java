package org.jboss.as.console.client.domain.groups;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;

import java.util.List;

/**
 * LHS serber group management navigation.
 *
 * @author Heiko Braun
 * @date 2/11/11
 */
class LHSServerGroupNavigation {

    private LayoutPanel layout;

    private VerticalPanel stack;

    private ServerGroupSection serverGroupSection;

    public LHSServerGroupNavigation() {

        layout = new LayoutPanel();
        layout.getElement().setAttribute("style", "width:99%;border-right:1px solid #E0E0E0");
        layout.setStyleName("fill-layout");

        stack = new VerticalPanel();
        stack.setStyleName("fill-layout-width");

        serverGroupSection = new ServerGroupSection();
        stack.add(serverGroupSection.asWidget());

        DeploymentSection deploymentSection = new DeploymentSection();
        stack.add(deploymentSection.asWidget());

        layout.add(stack);
    }

    public Widget asWidget()
    {
        return layout;
    }

    public void updateFrom(List<ServerGroupRecord> serverGroupRecords) {
        serverGroupSection.updateFrom(serverGroupRecords);
    }

}
