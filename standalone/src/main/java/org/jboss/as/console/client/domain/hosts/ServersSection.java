package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.*;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.widgets.ComboBox;
import org.jboss.as.console.client.widgets.LHSNavItem;
import org.jboss.as.console.client.widgets.icons.Icons;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/2/11
 */
class ServersSection {

    private TreeItem root, servers, common;
    private Tree hostTree;

    private ComboBox selection;

    private LayoutPanel layout;

    public ServersSection() {

        layout = new LayoutPanel();
        layout.setStyleName("stack-section");


        LHSNavItem createNew = new LHSNavItem(
                "Create Server",
                "servers/" + NameTokens.HostMgmtPresenter + ";action=new",
                Icons.INSTANCE.add_small()
        );

        // --------------------------------------------------

        hostTree = new Tree();

        root = new TreeItem("");
        servers = new TreeItem("Servers on Host:");
        common = new TreeItem("General Config:");

        common.addItem(new StyledTreeItem("Paths"));
        common.addItem(new StyledTreeItem("Interfaces"));
        common.addItem(new StyledTreeItem("Virtual Machines"));
        common.addItem(new StyledTreeItem("System Properties"));

        hostTree.addItem(root);
        hostTree.addItem(servers);
        hostTree.addItem(new TreeItem("&nbsp;"));// spacer
        hostTree.addItem(common);

        selection = new ComboBox();
        selection.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                fireHostSelection(event.getValue());
            }
        });

        Widget dropDown = selection.asWidget();

        HorizontalPanel horz = new HorizontalPanel();
        horz.getElement().setAttribute("width", "100%");
        horz.add(new HTML("&nbsp;Host:"));
        horz.add(dropDown);


        // --------------------------------------------------

        layout.add(horz);
        layout.add(createNew);
        layout.add(hostTree);

        layout.setWidgetTopHeight(horz, 0, Style.Unit.PX, 28, Style.Unit.PX);
        layout.setWidgetTopHeight(createNew, 28, Style.Unit.PX, 25, Style.Unit.PX);
        layout.setWidgetTopHeight(hostTree, 53, Style.Unit.PX, 100, Style.Unit.PCT);

    }

    public Widget asWidget()
    {
        return layout;
    }

    private void fireHostSelection(String hostName) {
        Console.MODULES.getEventBus().fireEvent(new HostSelectionEvent(hostName));
    }

    public void updateHosts(final List<Host> hostRecords) {

        selection.clearValues();

        for(Host record : hostRecords)
        {
            selection.addItem(record.getName());
        }

        // select first option when updated
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                selection.setItemSelected(0, true);
                fireHostSelection(hostRecords.get(0).getName());
            }
        });

    }

    public void updateInstances(List<Server> instances) {

        root.setState(false); // hide it
        servers.removeItems();

        for(Server instance: instances)
        {
            TreeItem item = new TreeItem(new HTML(instance.getName()));
            item.setStyleName("lhs-tree-item");
            servers.addItem(item);
        }

        if(instances.isEmpty())
        {
            TreeItem empty = new TreeItem(new HTML("(no servers)"));
            servers.addItem(empty);
        }

        root.setState(true);
        servers.setState(true);

    }

    class StyledTreeItem extends TreeItem {
        StyledTreeItem(String html) {
            super(html);
            setStyleName("lhs-tree-item");
        }
    }
}
