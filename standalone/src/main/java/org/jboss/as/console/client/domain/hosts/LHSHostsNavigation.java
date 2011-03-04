package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.widgets.StackSectionHeader;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/2/11
 */
class LHSHostsNavigation {

    private static final int SELECTOR_HEIGHT = 60;
    private StackLayoutPanel stack;

    private ServersSection serversSection;

    private HostSelector selector;

    LayoutPanel layout;

    public LHSHostsNavigation() {

        layout = new LayoutPanel()
        {
            @Override
            public void onResize() {
                super.onResize();
                int parentHeight = getParent().getOffsetHeight() - SELECTOR_HEIGHT;
                setSize("100%", parentHeight + "px");  // hack
            }
        };
        layout.setStyleName("fill-layout");

        selector = new HostSelector();
        final Widget selectorWidget = selector.asWidget();

        stack = new StackLayoutPanel(Style.Unit.PX);

        stack.addStyleName("section-stack");
        serversSection = new ServersSection();

        Widget serverSectionWidget = serversSection.asWidget();
        stack.add(serverSectionWidget, new StackSectionHeader("Servers"), 28);

        ServerInstanceSection instanceSection = new ServerInstanceSection();
        stack.add(instanceSection.asWidget(), new StackSectionHeader("Server Instances"), 28);

        HostConfigSection hostConfigSection = new HostConfigSection();
        stack.add(hostConfigSection.asWidget(), new StackSectionHeader("General Configuration"), 28);

        // -----------------------------

        layout.add(selectorWidget);
        layout.add(stack);

        layout.setWidgetTopHeight(selectorWidget, 0, Style.Unit.PX, SELECTOR_HEIGHT, Style.Unit.PX);
        layout.setWidgetTopHeight(stack, SELECTOR_HEIGHT, Style.Unit.PX, 100, Style.Unit.PCT);

    }

    public Widget asWidget()
    {
        Timer t = new Timer() {
            @Override
            public void run() {
                int parentHeight = layout.getParent().getOffsetHeight() - SELECTOR_HEIGHT;
                layout.setSize("100%", parentHeight +"px");
            }
        };
        t.schedule(500); // hack
        return layout;
    }

    public void updateHosts(List<Host> hosts) {
        selector.updateHosts(hosts);
    }

    public void updateInstances(List<Server> servers) {
        serversSection.updateServers(servers);
    }
}
