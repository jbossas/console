package org.jboss.as.console.client.system;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import org.jboss.as.console.client.widgets.RHSContentPanel;

/**
 * @author Heiko Braun
 * @date 2/4/11
 */
public class SystemApplicationViewImpl extends ViewImpl
    implements SystemApplicationPresenter.SystemAppView{

    @Override
    public Widget asWidget() {
        return new SystemAppCanvas().asWidget();
    }

    // dummy implementation
    class SystemAppCanvas
    {
        Widget asWidget() {
            LayoutPanel layout = new RHSContentPanel("System Overview");
            Label label = new Label("Quick glance at the system status. I.e. number of active service instances, etc.");
            layout.add(label);

            return layout;
        }
    }
}
