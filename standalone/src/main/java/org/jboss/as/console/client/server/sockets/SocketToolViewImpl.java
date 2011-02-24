package org.jboss.as.console.client.server.sockets;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.components.DisposableViewImpl;
import org.jboss.as.console.client.components.RHSContentPanel;

/**
 * @author Heiko Braun
 * @date 2/8/11
 */
public class SocketToolViewImpl extends DisposableViewImpl implements SocketToolPresenter.MyView {

    SocketToolPresenter presenter;

    @Override
    public void setPresenter(SocketToolPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        return new RHSContentPanel("Socket Binding Groups");
    }
}
