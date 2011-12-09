package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.ServerInstance;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/4/11
 */
public class ServerPicker implements HostServerManagement {

    private HostServerTable serverSelection;

    private SelectionHandler handler;

    public ServerPicker(SelectionHandler handler) {
        this.handler = handler;
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

        serverSelection.setServer(servers);
    }

    public void setSelected(ServerInstance server, boolean isSelected)
    {
        if(!server.isRunning())
            Console.warning("Selected in-active server instance:"+server.getName());


    }

    public void setHosts(List<Host> hosts) {
        serverSelection.setHosts(hosts);
    }

    public interface SelectionHandler {
        void onSelection(ServerInstance server);
    }

    @Override
    public void loadServer(Host selectedHost) {

    }
}
