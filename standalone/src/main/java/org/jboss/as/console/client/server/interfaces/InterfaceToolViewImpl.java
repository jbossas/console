package org.jboss.as.console.client.server.interfaces;

import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import com.smartgwt.client.widgets.layout.VLayout;
import org.jboss.as.console.client.components.sgwt.TitleBar;

/**
 * @author Heiko Braun
 * @date 2/8/11
 */
public class InterfaceToolViewImpl extends ViewImpl
        implements InterfaceToolPresenter.MyView {

    private InterfaceToolPresenter presenter;

    @Override
    public Widget asWidget() {

        VLayout layout = new VLayout();
        layout.setWidth100();
        layout.setHeight100();

        TitleBar titleBar = new TitleBar("Interfaces");
        layout.addMember(titleBar);

        return layout;
    }

    @Override
    public void setPresenter(InterfaceToolPresenter presenter) {
        this.presenter = presenter;
    }
}
