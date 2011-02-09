package org.jboss.as.console.client.server.properties;

import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import com.smartgwt.client.widgets.layout.VLayout;
import org.jboss.as.console.client.components.TitleBar;

/**
 * @author Heiko Braun
 * @date 2/8/11
 */
public class PropertyToolViewImpl extends ViewImpl
        implements PropertyToolPresenter.MyView {

    PropertyToolPresenter presenter;

    @Override
    public void setPresenter(PropertyToolPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget asWidget() {
        VLayout layout = new VLayout();
        layout.setWidth100();
        layout.setHeight100();

        TitleBar titleBar = new TitleBar("System Properties");
        layout.addMember(titleBar);

        return layout;
    }
}
