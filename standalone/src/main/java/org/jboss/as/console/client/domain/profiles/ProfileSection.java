package org.jboss.as.console.client.domain.profiles;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.events.ProfileSelectionEvent;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.shared.model.SubsystemRecord;
import org.jboss.as.console.client.widgets.ComboBox;
import org.jboss.as.console.client.widgets.LHSNavItem;
import org.jboss.as.console.client.widgets.LHSNavigationTree;
import org.jboss.as.console.client.widgets.LHSTreeItem;

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


        subsysTree = new LHSNavigationTree();
        root = new TreeItem("Subsystems in Profile:");
        subsysTree.addItem(root);

        LHSNavItem manage = new LHSNavItem(
                "In-/Exclude Subsystems", "domain/manage-subsystems"
        );

        //layout.add(manage);

        selection = new ComboBox();
        selection.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                fireProfileSelection(event.getValue());
            }
        });

        Widget dropDown = selection.asWidget();

        HorizontalPanel horz = new HorizontalPanel();
        horz.getElement().setAttribute("width", "100%");
        horz.add(new HTML("&nbsp;Profile:"));
        horz.add(dropDown);

        layout.add(horz);
        layout.add(subsysTree);

        layout.setWidgetTopHeight(horz, 0, Style.Unit.PX, 28, Style.Unit.PX);
        //layout.setWidgetTopHeight(manage, 28, Style.Unit.PX, 25, Style.Unit.PX);
        layout.setWidgetTopHeight(subsysTree, 28, Style.Unit.PX, 100, Style.Unit.PCT);
    }

    public Widget asWidget()
    {
        return layout;
    }

    private void fireProfileSelection(String profileName) {
        Console.MODULES.getEventBus().fireEvent(new ProfileSelectionEvent(profileName));
    }

    public void updateProfiles(final List<ProfileRecord> profileRecords) {

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
                fireProfileSelection(profileRecords.get(0).getName());
            }
        });

    }

    public void updateSubsystems(List<SubsystemRecord> subsystems) {

        root.removeItems();

        for(SubsystemRecord subsys: subsystems)
        {
            String token = "domain/profile/" + subsys.getTitle().toLowerCase().replace(" ", "_");
            TreeItem item = new LHSTreeItem(subsys.getTitle(), token);
            root.addItem(item);
        }

        root.setState(true);

    }
}
