package org.jboss.as.console.client.shared.subsys.jgroups;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.DisposableViewImpl;

/**
 * @author Heiko Braun
 * @date 2/16/12
 */
public class JGroupsSubsystemView extends DisposableViewImpl implements JGroupsPresenter.MyView {


    private JGroupsPresenter presenter;

    @Override
    public void setPresenter(JGroupsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {
        return new HTML();
    }
}
