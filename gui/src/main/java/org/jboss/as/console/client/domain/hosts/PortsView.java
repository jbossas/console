package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.shared.general.SocketTable;
import org.jboss.as.console.client.shared.general.model.SocketBinding;
import org.jboss.as.console.client.shared.help.StaticHelpPanel;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 6/7/11
 */
public class PortsView {

    private SocketTable socketTable;

    public Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        StaticHelpPanel helpPanel = new StaticHelpPanel("The effective ports on the currently selected server.");
        layout.add(helpPanel.asWidget());

        socketTable = new SocketTable();
        DefaultCellTable tableWidget = socketTable.asWidget();
        ScrollPanel scroll = new ScrollPanel(tableWidget);
        layout.add(scroll);

        DefaultPager pager = new DefaultPager();
        pager.setDisplay(tableWidget);

        layout.add(pager);
        return layout;
    }

    public void setPorts(String socketBinding, Server selectedRecord, List<SocketBinding> sockets) {
        socketTable.updateFrom(selectedRecord.getGroup(), sockets, selectedRecord.getPortOffset());
    }
}
