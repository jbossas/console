package org.jboss.as.console.client.domain.profiles;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.core.message.Message;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.SubsystemRecord;

import java.util.List;

/**
 * Domain management default view implementation.
 * Works on a LHS navigation and a all purpose content panel on the right.
 *
 * @author Heiko Braun
 * @date 2/4/11
 */
public class ProfileMgmtView extends SuspendableViewImpl
        implements ProfileMgmtPresenter.MyView{

    private ProfileMgmtPresenter presenter;
    private SplitLayoutPanel layout;
    private LayoutPanel contentCanvas;
    private LHSProfileNavigation lhsNavigation;

    public ProfileMgmtView() {
        super();

        layout = new SplitLayoutPanel(4);

        contentCanvas = new LayoutPanel();
        lhsNavigation = new LHSProfileNavigation();

        layout.addWest(lhsNavigation.asWidget(), 180);
        layout.add(contentCanvas);
    }

    @Override
    public Widget createWidget() {
        return layout;
    }

    @Override
    public void setInSlot(Object slot, Widget content) {

        if (slot == ProfileMgmtPresenter.TYPE_MainContent) {
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
    public void setProfiles(List<ProfileRecord> profileRecords) {
        lhsNavigation.updateProfiles(profileRecords);
    }

    @Override
    public void setSubsystems(List<SubsystemRecord> subsystemRecords)
    {
        lhsNavigation.updateFrom(subsystemRecords);
    }

    @Override
    public void setServerGroups(List<ServerGroupRecord> serverGroupRecords) {
        lhsNavigation.updateServerGroups(serverGroupRecords);
    }
}
