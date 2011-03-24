package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.widgets.RHSContentPanel;

/**
 * @author Heiko Braun
 * @date 3/24/11
 */
public class DatasourceView extends DisposableViewImpl implements DataSourcePresenter.MyView {

    private DataSourcePresenter presenter;

    LayoutPanel layout = null;


    @Override
    public Widget createWidget() {

        layout = new RHSContentPanel("DataSources");
        layout.add(new Label("Needs to be implemented. But should act as a default landing page."));
        return layout;
    }

    @Override
    public void setPresenter(DataSourcePresenter presenter) {
        this.presenter = presenter;
    }
}
