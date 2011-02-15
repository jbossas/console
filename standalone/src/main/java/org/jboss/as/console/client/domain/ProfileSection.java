package org.jboss.as.console.client.domain;

import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.events.ChangeEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangeHandler;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeNode;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.components.NavTreeNode;
import org.jboss.as.console.client.components.NavigationTreeGrid;
import org.jboss.as.console.client.domain.events.ProfileSelectionEvent;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.SubsystemRecord;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
class ProfileSection extends SectionStackSection {

    private NavTreeNode subsysNode;
    private NavigationTreeGrid subsysTreeGrid;
    private ComboBoxItem profileSelection;


    public ProfileSection() {

        super("Profiles");

        subsysTreeGrid = new NavigationTreeGrid("profile");
        subsysTreeGrid.setEmptyMessage("Please select a profile.");

        final DynamicForm form = new DynamicForm();
        form.setWidth100();

        profileSelection = new ComboBoxItem();
        profileSelection.setTitle("Profile");
        profileSelection.setType("comboBox");
        profileSelection.addChangeHandler(new ChangeHandler()
        {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                Console.MODULES.getEventBus().fireEvent(new ProfileSelectionEvent((String) changeEvent.getValue()));
                profileSelection.blurItem();
            }
        });
        profileSelection.setShowTitle(true);
        form.setFields(profileSelection);

        subsysNode = new NavTreeNode( "subsystems", false);
        Tree profileTree = new Tree();
        profileTree.setRoot(subsysNode);

        subsysTreeGrid.setData(profileTree);
        subsysTreeGrid.getTree().openAll();

        this.addItem(form);
        this.addItem(subsysTreeGrid);
    }

    public void updateFrom(ProfileRecord[] profileRecords) {

        String[] updates = new String[profileRecords.length];
        int i=0;
        for(ProfileRecord record : profileRecords)
        {
            updates[i] = record.getAttribute("profile-name");
            i++;
        }

        profileSelection.setValueMap(updates);
        subsysTreeGrid.markForRedraw();
    }

    public void updateFrom(SubsystemRecord[] subsystems) {

        subsysTreeGrid.getTree().closeAll(subsysNode);

        TreeNode[] nodes = new TreeNode[subsystems.length];
        int i = 0;
        for(SubsystemRecord subsys: subsystems)
        {
            nodes[i] = new NavTreeNode(subsys.getName(), subsys.getTitle());
            i++;
        }

        subsysNode.setChildren(nodes);

        subsysTreeGrid.markForRedraw();
        subsysTreeGrid.getTree().openAll(subsysNode);
    }
}
