package org.jboss.as.console.client.debug;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.message.Message;

/**
 * @author Heiko Braun
 * @date 2/4/11
 */
public class DebugToolsView extends ViewImpl
        implements DebugToolsPresenter.MyView{

    private DebugToolsPresenter presenter;

    private SplitLayoutPanel layout;
    private LayoutPanel contentCanvas;
    private LHSDebugToolsNavigation lhsNavigation;

    public DebugToolsView() {
        super();

        layout = new SplitLayoutPanel(4);

        contentCanvas = new LayoutPanel();
        lhsNavigation = new LHSDebugToolsNavigation();

        layout.addWest(lhsNavigation.asWidget(), 180);
        layout.add(contentCanvas);

    }

    @Override
    public void setPresenter(DebugToolsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget asWidget() {
        return layout;
    }

    @Override
    public void setInSlot(Object slot, Widget content) {

        if (slot == DebugToolsPresenter.TYPE_MainContent) {
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
