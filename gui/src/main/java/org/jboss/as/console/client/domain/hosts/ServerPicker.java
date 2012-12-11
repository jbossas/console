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
import org.jboss.as.console.client.shared.state.GlobalServerSelection;
import org.jboss.as.console.client.shared.state.HostList;
import org.jboss.as.console.client.shared.state.ServerInstanceList;

/**
 * @author Heiko Braun
 * @date 11/4/11
 */
public class ServerPicker implements HostServerManagement {

    private HostServerTable hostServerTable;
    private LoadServerCmd loadServerCmd;

    public void resetHostSelection() {
        hostServerTable.clearSelection();
    }

    public ServerPicker() {
        this.loadServerCmd = new LoadServerCmd(Console.MODULES.getDomainEntityManager());
    }

    public Widget asWidget() {

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");
        layout.addStyleName("lhs-selector");
        layout.getElement().setAttribute("style","padding:4px;margin-top:10px");

        hostServerTable = new HostServerTable(this);

        hostServerTable.setPopupWidth(400);
        hostServerTable.setDescription(Console.CONSTANTS.server_instance_pleaseSelect());

        Widget widget = hostServerTable.asWidget();
        widget.getElement().setAttribute("style", "width:100%;");

        Label label = new Label("Server:");
        label.setStyleName("header-label");

        layout.add(label);
        layout.add(widget);

        return layout;
    }

    public void setHosts(HostList hosts) {
        hostServerTable.setHosts(hosts);
    }

    @Override
    public void loadServer(final String selectedHost, final Command... commands) {

        loadServerCmd.execute(new SimpleCallback<ServerInstanceList>() {
            @Override
            public void onSuccess(ServerInstanceList result) {

                hostServerTable.setServer(result);

                // execute post loading commands
                for(Command c : commands)
                    c.execute();
            }
        });
    }

    @Override
    public void onServerSelected(final Host host, final ServerInstance server) {

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                Console.getEventBus().fireEvent(
                        new GlobalServerSelection(server)
                );
            }
        });

    }
}
