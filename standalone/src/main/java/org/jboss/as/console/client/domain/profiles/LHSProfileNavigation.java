package org.jboss.as.console.client.domain.profiles;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.model.SubsystemRecord;
import org.jboss.as.console.client.widgets.StackSectionHeader;
import org.jboss.as.console.client.widgets.stack.DefaultStackLayoutPanel;

import java.util.List;

/**
 * LHS domain management navigation.
 *
 * @author Heiko Braun
 * @date 2/11/11
 */
class LHSProfileNavigation {

    private StackLayoutPanel stack;

    private ProfileSection profileSection;

    public LHSProfileNavigation() {

        stack = new DefaultStackLayoutPanel();

        profileSection = new ProfileSection();
        stack.add(profileSection.asWidget(), new StackSectionHeader("Subsystems"), 28);

        CommonConfigSection commonSection = new CommonConfigSection();
        stack.add(commonSection.asWidget(), new StackSectionHeader("General Config"), 28);

    }

    public Widget asWidget()
    {
        return stack;
    }

    public void updateSubsystems(List<SubsystemRecord> subsystems) {

        profileSection.updateSubsystems(subsystems);
    }

    public void updateProfiles(List<ProfileRecord> profiles) {

        profileSection.updateProfiles(profiles);
    }

    public void updateServerGroups(List<ServerGroupRecord> serverGroupRecords) {
        //serverGroupSection.updateSubsystems(serverGroupRecords);
    }

}
