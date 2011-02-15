package org.jboss.as.console.client.domain;

import com.google.gwt.user.client.ui.Widget;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.layout.SectionStack;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.SubsystemRecord;

/**
 * @author Heiko Braun
 * @date 2/11/11
 */
class LHSDomainNavigation {

    private SectionStack sectionStack;

    private ProfileSection profileSection;
    private ServerGroupSection serverGroupSection;

    public LHSDomainNavigation() {

        sectionStack = new SectionStack();
        sectionStack.addStyleName("lhs-section-stack");
        sectionStack.setShowResizeBar(true);
        sectionStack.setVisibilityMode(VisibilityMode.MULTIPLE);
        sectionStack.setWidth(220);
        sectionStack.setHeight100();

        profileSection = new ProfileSection();
        profileSection.setExpanded(true);
        sectionStack.addSection(profileSection);

        serverGroupSection = new ServerGroupSection();
        serverGroupSection.setExpanded(true);
        sectionStack.addSection(serverGroupSection);

        DeploymentSection deploymentSection = new DeploymentSection();
        sectionStack.addSection(deploymentSection);

        CommonConfigSection commonSection = new CommonConfigSection();
        sectionStack.addSection(commonSection);

    }

    public Widget asWidget()
    {
        return sectionStack;
    }

    public void updateFrom(ProfileRecord[] profileRecords) {

        profileSection.updateFrom(profileRecords);
    }

    public void updateFrom(SubsystemRecord[] subsystems) {

        profileSection.updateFrom(subsystems);
    }

     public void setSelectedServerGroup(ServerGroupRecord serverGroupRecord) {

        serverGroupSection.setSelectedServerGroup(serverGroupRecord);
    }

    public void updateFrom(ServerGroupRecord[] serverGroupRecords) {
        serverGroupSection.updateFrom(serverGroupRecords);
    }
}
