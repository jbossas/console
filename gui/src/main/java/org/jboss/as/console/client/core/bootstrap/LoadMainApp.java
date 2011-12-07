package org.jboss.as.console.client.core.bootstrap;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.dispatch.AsyncCommand;

/**
 * @author Heiko Braun
 * @date 12/7/11
 */
public class LoadMainApp implements AsyncCommand<Boolean> {

    @Override
    public void execute(AsyncCallback<Boolean> callback) {
        Console.MODULES.getPlaceManager().revealDefaultPlace();
        callback.onSuccess(Boolean.TRUE);
    }
}