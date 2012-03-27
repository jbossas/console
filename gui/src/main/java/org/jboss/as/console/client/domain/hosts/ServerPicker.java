package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.events.HostSelectionEvent;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.state.ServerSelectionEvent;

import java.util.Collections;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/4/11
 */
public class ServerPicker implements HostServerManagement {

    private HostServerTable serverSelection;
    private LoadServerCmd loadServerCmd;

    public ServerPicker() {
        this.loadServerCmd = new LoadServerCmd(Console.MODULES.getHostInfoStore());
    }

    public Widget asWidget() {

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");
        layout.addStyleName("lhs-selector");
        layout.getElement().setAttribute("style","padding:4px;");

        serverSelection = new HostServerTable(this);

        serverSelection.setPopupWidth(400);
        serverSelection.setDescription(Console.CONSTANTS.server_instance_pleaseSelect());

        Widget widget = serverSelection.asWidget();
        widget.getElement().setAttribute("style", "width:100%;");

        Label label = new Label(Console.CONSTANTS.common_label_server()+":");
        label.setStyleName("header-label");

        layout.add(label);

        ScrollPanel scroll = new ScrollPanel(widget);
        layout.add(scroll);

        return layout;
    }

    public void setServers(List<ServerInstance> servers) {

        //should be done upon request
        // @see loadServer(hostName)
        //serverSelection.setServer(servers);
    }

    public void setSelected(ServerInstance server, boolean isSelected)
    {
        if(!server.isRunning())
        {
            Console.warning("Selected in-active server instance:"+server.getName());
        }

        serverSelection.selectServer(server);

    }

    public void setHosts(List<Host> hosts) {


        Host previousHost = serverSelection.getSelectedHost();

        serverSelection.setHosts(hosts);

        if(null==previousHost)
        {
            serverSelection.defaultHostSelection();
        }
        else
        {
            // restore previous selection
            serverSelection.selectHost(previousHost);
        }


    }

    @Override
    public void loadServer(Host selectedHost) {

        serverSelection.setServer(Collections.EMPTY_LIST);

        loadServerCmd.execute(selectedHost.getName(), new SimpleCallback<List<ServerInstance>>() {
            @Override
            public void onSuccess(List<ServerInstance> result) {
                serverSelection.setServer(result);
            }
        });
    }

    @Override
    public void onServerSelected(final Host host, final ServerInstance server) {

        //System.out.println("** Fire " + host.getName()+"/"+server.getName());

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                Console.getEventBus().fireEvent(
                        new HostSelectionEvent(host.getName())
                );

                Console.getEventBus().fireEvent(
                        new ServerSelectionEvent(host.getName(), server)
                );
            }
        });

    }

    public void clearSelection() {
        serverSelection.clearSelection();
    }
}
