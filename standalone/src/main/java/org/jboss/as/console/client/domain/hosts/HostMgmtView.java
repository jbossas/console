package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.core.message.Message;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.Server;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/2/11
 */
public class HostMgmtView extends SuspendableViewImpl implements HostMgmtPresenter.MyView {

    private HostMgmtPresenter presenter;

    private SplitLayoutPanel layout;
    private LayoutPanel contentCanvas;
    private LHSHostsNavigation lhsNavigation;

    public HostMgmtView() {
        layout = new SplitLayoutPanel(4);

        contentCanvas = new LayoutPanel();
        lhsNavigation = new LHSHostsNavigation();

        layout.addWest(lhsNavigation.asWidget(), 180);
        layout.add(contentCanvas);
    }

    @Override
    public void setPresenter(HostMgmtPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {
        return layout;
    }

    @Override
    public void setInSlot(Object slot, Widget content) {

        if (slot == HostMgmtPresenter.TYPE_MainContent) {
            if(content!=null)
                setContent(content);

        } else {
            Console.MODULES.getMessageCenter().notify(
                    new Message("Unknown slot requested:" + slot)
            );
        }
    }

    private void setContent(Widget newContent) {
        contentCanvas.clear();
        contentCanvas.add(newContent);
    }

    @Override
    public void updateHosts(List<Host> hosts) {
        lhsNavigation.updateHosts(hosts);
    }

    @Override
    public void updateServers(List<Server> servers) {
        lhsNavigation.updateInstances(servers);
    }
}
