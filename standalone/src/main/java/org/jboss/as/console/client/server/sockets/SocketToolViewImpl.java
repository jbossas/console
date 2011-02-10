package org.jboss.as.console.client.server.sockets;

import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import com.smartgwt.client.widgets.layout.VLayout;
import org.jboss.as.console.client.components.sgwt.TitleBar;

/**
 * @author Heiko Braun
 * @date 2/8/11
 */
public class SocketToolViewImpl extends ViewImpl implements SocketToolPresenter.MyView {

    SocketToolPresenter presenter;

    @Override
    public void setPresenter(SocketToolPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget asWidget() {
        VLayout layout = new VLayout();
        layout.setWidth100();
        layout.setHeight100();

        TitleBar titleBar = new TitleBar("Sockets");
        layout.addMember(titleBar);

        return layout;
    }
}
