package org.jboss.as.console.client.server.properties;

import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import org.jboss.as.console.client.widgets.RHSContentPanel;

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
        return new RHSContentPanel("System Properties");
    }
}
