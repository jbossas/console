package org.jboss.as.console.client.domain;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.components.SuspendableViewImpl;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.SubsystemRecord;
import org.jboss.as.console.client.util.message.Message;

/**
 * Domain management default view implementation.
 * Works on a LHS navigation and a all purpose content panel on the right.
 *
 * @author Heiko Braun
 * @date 2/4/11
 */
public class DomainMgmtApplicationViewImpl extends SuspendableViewImpl
        implements DomainMgmtApplicationPresenter.MyView{

    private DomainMgmtApplicationPresenter presenter;
    private SplitLayoutPanel layout;
    private LayoutPanel contentCanvas;
    private LHSDomainNavigation lhsNavigation;

    public DomainMgmtApplicationViewImpl() {
        super();

        layout = new SplitLayoutPanel(4);
        layout.setStyleName("lhs-navigation-panel");

        contentCanvas = new LayoutPanel();
        lhsNavigation = new LHSDomainNavigation();

        layout.addWest(lhsNavigation.asWidget(), 220);
        layout.add(contentCanvas);
    }

    @Override
    public Widget createWidget() {
        return layout;
    }

    @Override
    public void setInSlot(Object slot, Widget content) {

        if (slot == DomainMgmtApplicationPresenter.TYPE_MainContent) {
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
    public void setProfiles(ProfileRecord[] profileRecords) {
        lhsNavigation.updateFrom(profileRecords);
    }

    @Override
    public void setSubsystems(SubsystemRecord[] subsystemRecords)
    {
        lhsNavigation.updateFrom(subsystemRecords);
    }

    @Override
    public void setServerGroups(ServerGroupRecord[] serverGroupRecords) {
        lhsNavigation.updateFrom(serverGroupRecords);
    }
}
