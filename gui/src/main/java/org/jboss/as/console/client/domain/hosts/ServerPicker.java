package org.jboss.as.console.client.domain.hosts;

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
    private boolean isBootstrapped = false;

    public ServerPicker() {
        this.loadServerCmd = new LoadServerCmd(Console.MODULES.getHostInfoStore());
    }

    public Widget asWidget() {

        serverSelection = new HostServerTable(this);

        serverSelection.setPopupWidth(400);
        serverSelection.setDescription("Please select a server instance:");

        Widget widget = serverSelection.asWidget();
        widget.getElement().setAttribute("style", "width:100%;");


        return widget;
    }

    public void setServers(List<ServerInstance> servers) {

        //should be done upon request
        // @see loadServer(hostName)
        //serverSelection.setServer(servers);
    }

    public void setSelected(ServerInstance server, boolean isSelected)
    {
        if(!server.isRunning())
            Console.warning("Selected in-active server instance:"+server.getName());


    }

    public void setHosts(List<Host> hosts) {
        serverSelection.setHosts(hosts);

        if(!isBootstrapped)
        {
            isBootstrapped = true;
            serverSelection.doBootstrap();
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
    public void onServerSelected(Host host, ServerInstance server) {

        System.out.println("** Fire " + host.getName()+"/"+server.getName());

        Console.MODULES.getEventBus().fireEvent(
                new HostSelectionEvent(host.getName())
        );

        Console.MODULES.getEventBus().fireEvent(
                new ServerSelectionEvent(host.getName(), server.getName())
        );
    }
}
