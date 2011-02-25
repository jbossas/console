package org.jboss.as.console.client.server.subsys.threads;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.widgets.TitleBar;

/**
 * @author Heiko Braun
 * @date 2/9/11
 */
public class ThreadManagementViewImpl extends SuspendableViewImpl implements ThreadManagementPresenter.MyView {

    private ThreadManagementPresenter presenter;

    @Override
    public void setPresenter(ThreadManagementPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new LayoutPanel();

        final TitleBar titleBar = new TitleBar("Thread Management");
        layout.add(titleBar);
        layout.setStyleName("fill-layout-width");
        layout.getElement().setAttribute("style", "padding:15px;");

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 35, Style.Unit.PX);

        TabLayoutPanel tabLayoutpanel = new TabLayoutPanel(25, Style.Unit.PX);
        tabLayoutpanel.addStyleName("default-tabpanel");

        ThreadFactoryList threadFactoryList = new ThreadFactoryList(presenter);
        tabLayoutpanel.add(threadFactoryList, "Default Thread Factories");
        tabLayoutpanel.add(new HTML("Bar"), "Bounded Queue Factories");
        tabLayoutpanel.add(new HTML("Baz"), "Other");

        tabLayoutpanel.selectTab(0);

        layout.add(tabLayoutpanel);
        layout.setWidgetTopHeight(tabLayoutpanel, 50, Style.Unit.PX, 100, Style.Unit.PCT);

        return layout;
    }

}