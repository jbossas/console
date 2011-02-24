package org.jboss.as.console.client.domain.groups;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.widgets.RHSContentPanel;
import org.jboss.as.console.client.core.SuspendableViewImpl;

/**
 * @author Heiko Braun
 * @date 2/18/11
 */
public class ServerGroupsOverview extends SuspendableViewImpl
        implements ServerGroupOverviewPresenter.MyView {

    private ServerGroupOverviewPresenter presenter;

    @Override
    public void setPresenter(ServerGroupOverviewPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {
        final LayoutPanel layout = new RHSContentPanel("Server Group Overview");

        Label label = new Label("This will contain a list of server groups and hosts (servers) that actually belong to each group.");

        return layout;
    }
}
