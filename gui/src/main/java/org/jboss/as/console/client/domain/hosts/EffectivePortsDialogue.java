package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.shared.general.SocketTable;
import org.jboss.as.console.client.shared.general.model.SocketBinding;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 6/7/11
 */
public class EffectivePortsDialogue {

    private List<SocketBinding> socketBindings;
    private ServerConfigPresenter presenter;
    private Server server;

    public EffectivePortsDialogue(ServerConfigPresenter presenter, List<SocketBinding> socketBindings, Server selectedRecord) {

        this.presenter = presenter;
        this.socketBindings = socketBindings;
        this.server = selectedRecord;
    }

    public Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");
        layout.getElement().setAttribute("style", "padding:15px;");

        layout.add(new ContentHeaderLabel("Effective ports on server: "+server.getName()));

        SocketTable socketTable = new SocketTable(server.getPortOffset());
        ScrollPanel scroll = new ScrollPanel(socketTable.asWidget());
        layout.add(scroll);
        socketTable.updateFrom("none", socketBindings);

        return layout;
    }
}
