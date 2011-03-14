package org.jboss.as.console.client.server.deployment;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.core.message.Message;

/**
 * @author Heiko Braun
 * @date 1/31/11
 */
public class DeploymentMgmtView
        extends SuspendableViewImpl implements DeploymentMgmtPresenter.DeploymentToolView {

    private DeploymentMgmtPresenter presenter;

    private SplitLayoutPanel layout;
    private LayoutPanel contentCanvas;
    private LHSDeploymentNavigation lhsNavigation;

    @Override
    public void setPresenter(DeploymentMgmtPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        layout = new SplitLayoutPanel(4);

        contentCanvas = new LayoutPanel();
        lhsNavigation = new LHSDeploymentNavigation();

        layout.addWest(lhsNavigation.asWidget(), 180);
        layout.add(contentCanvas);

        return layout;
    }

    @Override
    public void setInSlot(Object slot, Widget content) {

        if (slot == DeploymentMgmtPresenter.TYPE_MainContent) {
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
