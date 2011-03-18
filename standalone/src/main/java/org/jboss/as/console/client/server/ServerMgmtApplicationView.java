package org.jboss.as.console.client.server;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.message.Message;
import org.jboss.as.console.client.shared.model.SubsystemRecord;

import java.util.List;

/**
 * Server management default view implementation.
 * Works on a LHS navigation and a all purpose content panel on the right.
 *
 * @see LHSServerNavigation
 *
 * @author Heiko Braun
 * @date 2/4/11
 */
public class ServerMgmtApplicationView extends ViewImpl
        implements ServerMgmtApplicationPresenter.ServerManagementView {

    private ServerMgmtApplicationPresenter presenter;

    private SplitLayoutPanel layout;
    private LayoutPanel contentCanvas;
    private LHSServerNavigation lhsNavigation;

    public ServerMgmtApplicationView() {
        super();

        layout = new SplitLayoutPanel(4);

        contentCanvas = new LayoutPanel();
        lhsNavigation = new LHSServerNavigation();

        layout.addWest(lhsNavigation.asWidget(), 180);
        layout.add(contentCanvas);

    }

    @Override
    public Widget asWidget() {
        return layout;
    }

    @Override
    public void updateFrom(List<SubsystemRecord> subsystemRecords) {
        lhsNavigation.updateFrom(subsystemRecords);
    }

    @Override
    public void setInSlot(Object slot, Widget content) {

        if (slot == ServerMgmtApplicationPresenter.TYPE_MainContent) {
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


}
