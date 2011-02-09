package org.jboss.as.console.client.server.subsys;

import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import com.smartgwt.client.widgets.layout.VLayout;
import org.jboss.as.console.client.components.TitleBar;

/**
 * @author Heiko Braun
 * @date 2/8/11
 */
public class SubsystemToolViewImpl extends ViewImpl implements SubsystemToolPresenter.MyView {

    SubsystemToolPresenter presenter;

    @Override
    public void setPresenter(SubsystemToolPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget asWidget() {
        VLayout layout = new VLayout();
        layout.setWidth100();
        layout.setHeight100();

        TitleBar titleBar = new TitleBar("Subsystems");
        layout.addMember(titleBar);

        return layout;
    }
}
