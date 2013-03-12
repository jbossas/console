package org.jboss.as.console.client.core.bootstrap;

import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.gwt.flow.client.Control;
import org.jboss.gwt.flow.client.Function;

/**
 * @author Heiko Braun
 * @date 12/7/11
 */
public class RemoveLoadingPanel implements Function<BootstrapContext> {
    private Widget widget;

    public RemoveLoadingPanel(Widget loadingPanel) {
        this.widget = loadingPanel;
    }

    @Override
    public void execute(Control<BootstrapContext> control) {

        RootLayoutPanel.get().remove(widget);
        control.proceed();
    }
}
