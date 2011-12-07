package org.jboss.as.console.client.core.bootstrap;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.dispatch.AsyncCommand;

/**
 * @author Heiko Braun
 * @date 12/7/11
 */
public class RemoveLoadingPanel implements AsyncCommand<Boolean>{
    private Widget widget;

    public RemoveLoadingPanel(Widget loadingPanel) {
        this.widget = loadingPanel;
    }

    @Override
    public void execute(AsyncCallback<Boolean> callback) {
        RootLayoutPanel.get().remove(widget);
        callback.onSuccess(Boolean.TRUE);
    }
}
