package org.jboss.as.console.client.domain;

import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import org.jboss.as.console.client.Console;
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
public class DomainMgmtApplicationViewImpl extends ViewImpl
        implements DomainMgmtApplicationPresenter.MyView{

    private HLayout layout;
    private Canvas contentCanvas;

    private DomainMgmtApplicationPresenter presenter;

    private LHSDomainNavigation lhsNavigation;

    VLayout vlayout;
    Canvas tools;

    public DomainMgmtApplicationViewImpl() {
        super();

        layout = new HLayout();
        layout.setWidth100();
        layout.setHeight100();
        layout.setStyleName("lhs-navigation-panel");

        contentCanvas = new Canvas();
        contentCanvas.setWidth100();
        contentCanvas.setHeight100();
        contentCanvas.setMargin(0);

        lhsNavigation = new LHSDomainNavigation();
        layout.addMember(lhsNavigation.asWidget());
        layout.addMember(contentCanvas);

    }

    @Override
    public Widget asWidget() {
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

        Canvas[] children = contentCanvas.getChildren();
        if(children.length>0) {
            contentCanvas.removeChild(children[0]);
        }

        contentCanvas.addChild(newContent);
        contentCanvas.markForRedraw();
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
