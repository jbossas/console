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
import org.jboss.as.console.client.shared.state.CurrentServerSelection;
import org.jboss.as.console.client.shared.state.ServerSelectionEvent;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/4/11
 */
public class ServerPicker implements HostServerManagement {

    private HostServerTable hostServerTable;
    private LoadServerCmd loadServerCmd;
    private CurrentServerSelection serverSelectionState;

    public ServerPicker(CurrentServerSelection serverSelectionState) {
        this.loadServerCmd = new LoadServerCmd(Console.MODULES.getHostInfoStore());
        this.serverSelectionState = serverSelectionState;
    }

    public Widget asWidget() {

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");
        layout.addStyleName("lhs-selector");
        layout.getElement().setAttribute("style","padding:4px;");

        hostServerTable = new HostServerTable(this);

        hostServerTable.setPopupWidth(400);
        hostServerTable.setDescription(Console.CONSTANTS.server_instance_pleaseSelect());

        Widget widget = hostServerTable.asWidget();
        widget.getElement().setAttribute("style", "width:100%;");

        Label label = new Label(Console.CONSTANTS.common_label_server()+":");
        label.setStyleName("header-label");

        layout.add(label);

        ScrollPanel scroll = new ScrollPanel(widget);
        layout.add(scroll);

        return layout;
    }

    public void setHosts(List<Host> hosts) {

        Host previousHost = hostServerTable.getSelectedHost();

        hostServerTable.setHosts(hosts);

        if(serverSelectionState.isSet())
        {
            // preselected (external) server/host combination

            for(Host host : hosts)
            {
                if(host.getName().equals(serverSelectionState.getHost()))
                {
                    //hostServerTable.selectHost(host);
                    //hostServerTable.selectServer(serverSelectionState.getServer());
                    break;
                }
            }

        }
        else if(null==previousHost)
        {
            hostServerTable.defaultHostSelection();
        }
        else
        {
            // restore previous selection
            hostServerTable.selectHost(previousHost);
        }


    }

    @Override
    public void loadServer(final Host selectedHost) {

        loadServerCmd.execute(selectedHost.getName(), new SimpleCallback<List<ServerInstance>>() {
            @Override
            public void onSuccess(List<ServerInstance> result) {

                hostServerTable.setServer(selectedHost, result);

                if(result.isEmpty())
                {
                    // no server on host. some operation are not available
                    Console.getEventBus().fireEvent(
                            new HostSelectionEvent(selectedHost.getName())
                    );

                }
            }
        });
    }

    @Override
    public void onServerSelected(final Host host, final ServerInstance server) {

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                Console.getEventBus().fireEvent(
                        new ServerSelectionEvent(host.getName(), server, ServerSelectionEvent.Source.Picker)
                );
            }
        });

    }

    public void setPreselection(String hostName, ServerInstance server) {

        //hostServerTable.selectHost(hostName);
        hostServerTable.selectServer(server);
    }
}
