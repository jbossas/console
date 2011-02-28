package org.jboss.as.console.client.domain;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.widgets.StackSectionHeader;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.SubsystemRecord;

import java.util.List;

/**
 * LHS domain management navigation.
 *
 * @author Heiko Braun
 * @date 2/11/11
 */
class LHSDomainNavigation {

    private StackLayoutPanel stack;

    private ProfileSection profileSection;
    private ServerGroupSection serverGroupSection;

    public LHSDomainNavigation() {

        stack = new StackLayoutPanel(Style.Unit.PX);
        stack.addStyleName("section-stack");
        stack.setWidth("250");

        profileSection = new ProfileSection();
        stack.add(profileSection.asWidget(), new StackSectionHeader("Subsystems"), 28);


        //serverGroupSection = new ServerGroupSection();
        //stack.add(serverGroupSection.asWidget(), new StackSectionHeader("Server Groups"), 28);

        //DeploymentSection deploymentSection = new DeploymentSection();
        //stack.add(deploymentSection.asWidget(), new StackSectionHeader("Deployments"), 28);

        CommonConfigSection commonSection = new CommonConfigSection();
        stack.add(commonSection.asWidget(), new StackSectionHeader("General Config"), 28);

    }

    public Widget asWidget()
    {
        return stack;
    }

    public void updateFrom(List<SubsystemRecord> subsystems) {

        profileSection.updateFrom(subsystems);
    }

    public void updateProfiles(List<ProfileRecord> profiles) {

        //profileSection.updateProfiles(profiles);
    }

    public void updateFrom(ServerGroupRecord[] serverGroupRecords) {
        //serverGroupSection.updateFrom(serverGroupRecords);
    }

}
