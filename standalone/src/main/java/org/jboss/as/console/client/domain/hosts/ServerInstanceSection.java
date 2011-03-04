package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.widgets.LHSNavItem;
import org.jboss.as.console.client.widgets.icons.Icons;

/**
 * @author Heiko Braun
 * @date 3/4/11
 */
public class ServerInstanceSection implements HostSelectionEvent.HostSelectionListener{


    private LayoutPanel layout;
    private String selectedHost = null;

    public ServerInstanceSection() {

        layout = new LayoutPanel();
        layout.setStyleName("stack-section");

        LHSNavItem overview = new LHSNavItem(
                "Overview",
                "hosts/server-instances",
                Icons.INSTANCE.inventory_small()
        );

        LHSNavItem startNew = new LHSNavItem(
                "Launch Instance",
                "hosts/server-instances;action=new",
                Icons.INSTANCE.add_small()
        );

        // --------------------------------------------------

        layout.add(overview);
        layout.add(startNew);

        layout.setWidgetTopHeight(overview, 0, Style.Unit.PX, 25, Style.Unit.PX);
        layout.setWidgetTopHeight(startNew, 25, Style.Unit.PX, 25, Style.Unit.PX);

        // listen on host selection events
        Console.MODULES.getEventBus().addHandler(
                HostSelectionEvent.TYPE, this
        );


    }

    public Widget asWidget()
    {
        return layout;
    }

    @Override
    public void onHostSelection(String hostName) {
        selectedHost = hostName;
    }
}
