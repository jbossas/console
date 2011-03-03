package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.*;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.Places;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.widgets.ComboBox;
import org.jboss.as.console.client.widgets.LHSNavItem;
import org.jboss.as.console.client.widgets.icons.Icons;
import org.jboss.as.console.client.widgets.resource.DefaultTreeResources;

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
                "hosts/" + NameTokens.ServerPresenter + ";action=new",
                Icons.INSTANCE.add_small()
        );

        // --------------------------------------------------

        hostTree = new Tree(DefaultTreeResources.INSTANCE);

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
            }
        });

    }

    public void updateServers(List<Server> servers) {

        root.setState(false); // hide it
        this.servers.removeItems();

        for(Server server: servers)
        {
            String serverName = server.getName();
            final String token = "hosts/" + NameTokens.ServerPresenter+
                    ";host="+selection.getSelectedValue() +
                    ";server=" + serverName;

            HTML link = new HTML(serverName);
            final TreeItem item = new TreeItem(link);
            item.setStyleName("lhs-tree-item");

            link.addClickHandler(new ClickHandler()
            {
                @Override
                public void onClick(ClickEvent event) {
                    hostTree.setSelectedItem(item);
                    Console.MODULES.getPlaceManager().revealPlaceHierarchy(
                            Places.fromString(token)
                    );
                }
            });

            this.servers.addItem(item);

        }

        if(servers.isEmpty())
        {
            TreeItem empty = new TreeItem(new HTML("(no servers)"));
            this.servers.addItem(empty);
        }

        root.setState(true);
        this.servers.setState(true);

    }

    class StyledTreeItem extends TreeItem {
        StyledTreeItem(String html) {
            super(html);
            setStyleName("lhs-tree-item");
        }
    }
}
