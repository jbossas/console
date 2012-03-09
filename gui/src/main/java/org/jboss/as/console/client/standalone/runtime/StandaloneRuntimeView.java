package org.jboss.as.console.client.standalone.runtime;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.message.Message;
import org.jboss.as.console.client.shared.model.SubsystemRecord;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/2/11
 */
public class StandaloneRuntimeView extends ViewImpl implements StandaloneRuntimePresenter.MyView{

    private StandaloneRuntimePresenter presenter;

    private SplitLayoutPanel layout;
    private LayoutPanel contentCanvas;
    private StandaloneRuntimeNavigation lhsNavigation;

    public StandaloneRuntimeView() {
        super();

        layout = new SplitLayoutPanel(10);

        contentCanvas = new LayoutPanel();
        lhsNavigation = new StandaloneRuntimeNavigation();

        Widget nav = lhsNavigation.asWidget();
        nav.getElement().setAttribute("role", "navigation");
        nav.getElement().setAttribute("aria-label", "Category Runtime");

        contentCanvas.getElement().setAttribute("role", "main");

        layout.addWest(nav, 180);
        layout.add(contentCanvas);

    }

    @Override
    public Widget asWidget() {
        return layout;
    }

    @Override
    public void setInSlot(Object slot, Widget content) {

        if (slot == StandaloneRuntimePresenter.TYPE_MainContent) {
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
    public void setPresenter(StandaloneRuntimePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setSubsystems(List<SubsystemRecord> result) {
        lhsNavigation.setSubsystems(result);
    }
}
