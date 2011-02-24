package org.jboss.as.console.client.domain;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.*;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.events.ProfileSelectionEvent;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.shared.SubsystemRecord;
import org.jboss.as.console.client.widgets.ComboBox;
import org.jboss.as.console.client.widgets.LHSNavItem;
import org.jboss.as.console.client.widgets.icons.Icons;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
class ProfileSection {

    private TreeItem root;
    private Tree subsysTree;

    private ComboBox selection;

    private LayoutPanel layout;

    public ProfileSection() {

        layout = new LayoutPanel();
        layout.setStyleName("stack-section");


        subsysTree = new Tree();
        root = new TreeItem("Subsystems:");
        subsysTree.addItem(root);

        LHSNavItem overview = new LHSNavItem(
                "Overview",
                "domain/"+ NameTokens.ProfileOverviewPresenter,
                Icons.INSTANCE.inventory()
        );

        layout.add(overview);

        selection = new ComboBox();


        Widget dropDown = selection.asWidget();
        layout.add(dropDown);
        layout.add(subsysTree);

        layout.setWidgetTopHeight(overview, 0, Style.Unit.PX, 25, Style.Unit.PX);
        layout.setWidgetTopHeight(dropDown, 25, Style.Unit.PX, 28, Style.Unit.PX);
        layout.setWidgetTopHeight(subsysTree, 53, Style.Unit.PX, 100, Style.Unit.PCT);
    }

    public Widget asWidget()
    {
        return layout;
    }

    private void fireProfileSelection(String profileName) {
        Console.MODULES.getEventBus().fireEvent(new ProfileSelectionEvent(profileName));
    }

    public void updateFrom(final ProfileRecord[] profileRecords) {

        selection.clearValues();

        for(ProfileRecord record : profileRecords)
        {
            selection.addItem(record.getName());
        }

        // select first option when updated
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                selection.setItemSelected(0, true);
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
