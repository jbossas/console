package org.jboss.as.console.client.server;

import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.SubsystemRecord;
import org.jboss.as.console.client.util.message.Message;

/**
 * Server management default view implementation.
 * Works on a LHS navigation and a all purpose content panel on the right.
 *
 * <p/>
 * CSS:
 * <ul>
 * <li> 'lhs-navigation-panel', used for the top most horizontal panel
 * </ul>
 * @see LHSServerNavigation
 *
 * @author Heiko Braun
 * @date 2/4/11
 */
public class ServerMgmtApplicationViewImpl extends ViewImpl
        implements ServerMgmtApplicationPresenter.ServerManagementView{

    private HLayout layout;
    private LHSServerNavigation lhsNavigation;
    private Canvas contentCanvas;

    public ServerMgmtApplicationViewImpl() {
        super();

        layout = new HLayout();
        layout.setWidth100();
        layout.setHeight100();
        layout.setStyleName("lhs-navigation-panel");

        lhsNavigation = new LHSServerNavigation();

        contentCanvas = new Canvas();
        contentCanvas.setWidth100();
        contentCanvas.setHeight100();
        contentCanvas.setMargin(0);

        layout.addMember(lhsNavigation.asWidget());
        layout.addMember(contentCanvas);

    }

    @Override
    public Widget asWidget() {
        return layout;
    }

    @Override
    public void updateFrom(SubsystemRecord[] subsystemRecords) {
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

        Canvas[] children = contentCanvas.getChildren();
        if(children.length>0) {
            contentCanvas.removeChild(children[0]);
        }

        contentCanvas.addChild(newContent);
        contentCanvas.markForRedraw();
    }

}
