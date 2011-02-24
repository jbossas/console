package org.jboss.as.console.client.server.interfaces;

import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import org.jboss.as.console.client.widgets.RHSContentPanel;

/**
 * @author Heiko Braun
 * @date 2/8/11
 */
public class InterfaceToolViewImpl extends ViewImpl
        implements InterfaceToolPresenter.MyView {

    private InterfaceToolPresenter presenter;

    @Override
    public Widget asWidget() {

        return new RHSContentPanel("Interfaces");
    }

    @Override
    public void setPresenter(InterfaceToolPresenter presenter) {
        this.presenter = presenter;
    }
}
