package org.jboss.as.console.client.domain.groups;

import com.google.gwt.user.client.ui.Widget;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.VLayout;
import org.jboss.as.console.client.components.SuspendableViewImpl;
import org.jboss.as.console.client.components.sgwt.TitleBar;

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
        final VLayout layout = new VLayout();
        layout.setWidth100();
        layout.setHeight100();

        TitleBar titleBar = new TitleBar("Server Groups Overview");
        layout.addMember(titleBar);

        Label label = new Label("This will contain a list of server groups and hosts (servers) that actually belong to each group.");
        label.setMargin(15);
        layout.addMember(label);

        return layout;
    }
}
