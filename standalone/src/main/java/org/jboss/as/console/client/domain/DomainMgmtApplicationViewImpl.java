package org.jboss.as.console.client.domain;

import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.components.sgwt.LHSNavigation;
import org.jboss.as.console.client.server.ServerMgmtApplicationPresenter;
import org.jboss.as.console.client.util.message.Message;

/**
 * Domain management default view implementation.
 * Works on a LHS navigation and a all purpose content panel on the right.
 *
 * <p/>
 * CSS:
 * <ul>
 * <li> 'lhs-navigation-panel', used for the top most horizontal panel
 * </ul>
 * @see LHSNavigation
 *
 * @author Heiko Braun
 * @date 2/4/11
 */
public class DomainMgmtApplicationViewImpl extends ViewImpl
        implements DomainMgmtApplicationPresenter.MyView{

    private HLayout layout;

    private Canvas contentCanvas;
    private DomainMgmtApplicationPresenter presenter;

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

        layout.addMember(new TreeLHSDomainNavigation().asWidget());
        layout.addMember(contentCanvas);

    }

    @Override
    public void setPresenter(DomainMgmtApplicationPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget asWidget() {
        return layout;
    }

    @Override
    public void setInSlot(Object slot, Widget content) {

        if (slot == ServerMgmtApplicationPresenter.TYPE_SetToolContent) {
            if(content!=null)
                setContent(content);

        } else {
            Console.MODULES.getMessageCenter().notify(
                    new Message("Unknown slot requested:" + slot)
            );
        }
    }

    @Override
    public void removeFromSlot(Object slot, Widget content) {
        super.removeFromSlot(slot, content);
        System.out.println("remove "+content.getElement().getId());
    }

    public void setContent(Widget newContent) {

        Canvas[] children = contentCanvas.getChildren();
        if(children.length>0) {
            contentCanvas.removeChild(children[0]);
        }

        contentCanvas.addChild(newContent);
        contentCanvas.markForRedraw();
    }

}
