package org.jboss.as.console.client.domain;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.*;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.NameTokens;
import org.jboss.as.console.client.components.LHSNavItem;
import org.jboss.as.console.client.icons.Icons;
import org.jboss.as.console.client.domain.events.ProfileSelectionEvent;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.shared.SubsystemRecord;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
class ProfileSection {

    private TreeItem root;
    private Tree subsysTree;
    private ListBox profileSelection;

    private LayoutPanel layout;

    public ProfileSection() {

        layout = new LayoutPanel();
        layout.setStyleName("stack-section");

        profileSelection = new ListBox();
        subsysTree = new Tree();
        root = new TreeItem("Subsystems:");
        subsysTree.addItem(root);

        LHSNavItem overview = new LHSNavItem(
                "Overview",
                "domain/"+ NameTokens.ProfileOverviewPresenter,
                Icons.INSTANCE.inventory()
        );

        layout.add(overview);
        layout.add(profileSelection);
        layout.add(subsysTree);

        layout.setWidgetTopHeight(overview, 0, Style.Unit.PX, 25, Style.Unit.PX);
        layout.setWidgetTopHeight(profileSelection, 25, Style.Unit.PX, 25, Style.Unit.PX);
        layout.setWidgetTopHeight(subsysTree, 50, Style.Unit.PX, 100, Style.Unit.PCT);
    }

    public Widget asWidget()
    {
        return layout;
    }

    private void fireProfileSelection(String profileName) {
        Console.MODULES.getEventBus().fireEvent(new ProfileSelectionEvent(profileName));
    }

    public void updateFrom(final ProfileRecord[] profileRecords) {

        profileSelection.clear();

        for(ProfileRecord record : profileRecords)
        {
            profileSelection.addItem(record.getName());
        }

        // select first option when updated
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                profileSelection.setItemSelected(0, true);
                fireProfileSelection(profileRecords[0].getName());
            }
        });

    }

    public void updateFrom(List<SubsystemRecord> subsystems) {

        root.removeItems();

        for(SubsystemRecord subsys: subsystems)
        {
            TreeItem item = new TreeItem(new HTML(subsys.getTitle()));
            item.setStyleName("lhs-tree-item");
            root.addItem(item);
        }

        root.setState(true);

    }
}
