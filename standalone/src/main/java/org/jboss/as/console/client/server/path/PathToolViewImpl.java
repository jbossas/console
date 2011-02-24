package org.jboss.as.console.client.server.path;

import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import org.jboss.as.console.client.widgets.RHSContentPanel;

/**
 * @author Heiko Braun
 * @date 2/8/11
 */
public class PathToolViewImpl extends ViewImpl implements PathToolPresenter.MyView {

    PathToolPresenter presenter;

    @Override
    public void setPresenter(PathToolPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget asWidget() {
        return new RHSContentPanel("Paths");
    }
}
