package org.jboss.as.console.client.domain.groups;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.core.message.Message;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/28/11
 */
public class ServerGroupMgmtView extends SuspendableViewImpl implements ServerGroupMgmtPresenter.MyView {


    private ServerGroupMgmtPresenter presenter;
    private SplitLayoutPanel layout;
    private LayoutPanel contentCanvas;
    private LHSServerGroupNavigation lhsNavigation;

    public ServerGroupMgmtView() {
        super();

        layout = new SplitLayoutPanel(4);

        contentCanvas = new LayoutPanel();
        lhsNavigation = new LHSServerGroupNavigation();

        layout.addWest(lhsNavigation.asWidget(), 180);
        layout.add(contentCanvas);
    }


    @Override
    public void setPresenter(ServerGroupMgmtPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {
        return layout;
    }

     @Override
    public void setInSlot(Object slot, Widget content) {

        if (slot == ServerGroupMgmtPresenter.TYPE_MainContent) {
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
    public void updateServerGroups(List<ServerGroupRecord> serverGroupRecords) {
        lhsNavigation.updateFrom(serverGroupRecords);
    }
}
