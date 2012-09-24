package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.state.ServerSelectionEvent;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/4/11
 */
public class ServerPicker implements HostServerManagement {

    private HostServerTable hostServerTable;
    private LoadServerCmd loadServerCmd;

    static class Preselection {
        private String host;
        private ServerInstance server;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public ServerInstance getServer() {
            return server;
        }

        public void setServer(ServerInstance server) {
            this.server = server;
        }

        public void clear() {
            this.host=null;
            this.server=null;
        }
    }

    static Preselection preselection = new Preselection();

    public ServerPicker() {
        this.loadServerCmd = new LoadServerCmd(Console.MODULES.getHostInfoStore());
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

    /**
     * Update the host list and applies a selection.
     * Either default (first one) or preselected (if given)
     *
     * @param hosts
     */
    public void setHosts(List<Host> hosts) {
        hostServerTable.setHosts(hosts);

        if(preselection.getHost()!=null)
        {

            final Command applySelection = new Command() {
                @Override
                public void execute() {
                    hostServerTable.pickHost(preselection.getHost());
                    hostServerTable.selectServer(preselection.getServer());
                }
            };


            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    loadServer(preselection.getHost(), applySelection);
                }
            });


        }
        else
        {
            hostServerTable.defaultHostSelection();
        }
    }

    @Override
    public void loadServer(final String selectedHost, final Command... commands) {

        loadServerCmd.execute(selectedHost, new SimpleCallback<List<ServerInstance>>() {
            @Override
            public void onSuccess(List<ServerInstance> result) {

                hostServerTable.setServer(result);

                // apply selection policy
                if(preselection.getServer()!=null)
                {
                    hostServerTable.selectServer(preselection.getServer());
                }
                else if(result.size()>0)
                {
                    hostServerTable.selectServer(result.get(0));
                }

                // execute post loading commands
                for(Command c : commands)
                    c.execute();
            }
        });
    }

    @Override
    public void onServerSelected(final Host host, final ServerInstance server) {

        assert host!=null : "No host set";

        // update the preselection
        preselection.setHost(host.getName());
        preselection.setServer(server);


        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                Console.getEventBus().fireEvent(
                        new ServerSelectionEvent(host.getName(), server, ServerSelectionEvent.Source.Picker)
                );
            }
        });

    }

    /**
     * invoked by event bus
     * @param hostName
     * @param server
     */
    public void setPreselection(String hostName, ServerInstance server) {
        preselection.setHost(hostName);
        preselection.setServer(server);
    }
}
