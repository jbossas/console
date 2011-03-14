package org.jboss.as.console.client.server.subsys.threads;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.*;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.widgets.RHSHeader;

/**
 * @author Heiko Braun
 * @date 2/9/11
 */
public class ThreadManagementView extends SuspendableViewImpl implements ThreadManagementPresenter.MyView {

    private ThreadManagementPresenter presenter;

    @Override
    public void setPresenter(ThreadManagementPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new LayoutPanel();

        RHSHeader title = new RHSHeader("Thread Management");
        layout.add(title);
        layout.setWidgetTopHeight(title, 0, Style.Unit.PX, 28, Style.Unit.PX);

        VerticalPanel vpanel = new VerticalPanel();
        vpanel.setStyleName("fill-layout");
        vpanel.getElement().setAttribute("style", "padding:15px;");

        // -----------

        TabLayoutPanel tabLayoutpanel = new TabLayoutPanel(25, Style.Unit.PX);
        tabLayoutpanel.addStyleName("default-tabpanel");

        ThreadFactoryList threadFactoryList = new ThreadFactoryList(presenter);
        tabLayoutpanel.add(threadFactoryList, "Default Thread Factories");
        tabLayoutpanel.add(new HTML("Bar"), "Bounded Queue Factories");
        tabLayoutpanel.add(new HTML("Baz"), "Other");

        tabLayoutpanel.selectTab(0);

        vpanel.add(tabLayoutpanel);

        layout.add(vpanel);
        layout.setWidgetTopHeight(vpanel, 35, Style.Unit.PX, 100, Style.Unit.PCT);

        return layout;
    }

}