package org.jboss.as.console.client.core.bootstrap;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.dispatch.AsyncCommand;

import java.util.Iterator;

/**
 * @author Heiko Braun
 * @date 12/7/11
 */
public class RemoveLoadingPanel extends BoostrapStep {
    private Widget widget;

    public RemoveLoadingPanel(Widget loadingPanel) {
        this.widget = loadingPanel;
    }

    @Override
    public void execute(Iterator<BoostrapStep> iterator, AsyncCallback<Boolean> outcome) {

        RootLayoutPanel.get().remove(widget);
        outcome.onSuccess(Boolean.TRUE);

        next(iterator, outcome);
    }
}
