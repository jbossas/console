package org.jboss.as.console.client.server.subsys.threads;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.widgets.RHSContentPanel;

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

        LayoutPanel layout = new RHSContentPanel("Thread Management");

        return layout;
    }

}