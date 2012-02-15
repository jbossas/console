package org.jboss.as.console.client.core.bootstrap;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.dispatch.AsyncCommand;

import java.util.LinkedList;

/**
 * @author Heiko Braun
 * @date 12/7/11
 */
public class BootstrapProcess {

    private LinkedList<AsyncCommand> hooks = new LinkedList<AsyncCommand>();
    private int index = 0;

    public void addHook(AsyncCommand hook) {
        hooks.add(hook);
    }

    public void execute(AsyncCallback<Boolean> outcome) {
        index = 0;
        executeNext(outcome);
    }

    private void executeNext(final AsyncCallback<Boolean> outcome) {
        if(index < hooks.size())
        {
            final AsyncCommand nextHook = hooks.get(index);
            index++;

            Window.setStatus(index + ": " + nextHook.getClass().getName());

            nextHook.execute(new AsyncCallback<Boolean>() {
                @Override
                public void onFailure(Throwable caught) {
                    Console.error("Bootstrap failed", caught.getMessage());
                }

                @Override
                public void onSuccess(Boolean successful) {
                    if(successful)
                    {
                        executeNext(outcome);
                    }
                    else
                    {
                        Console.error("Failed to execute "+nextHook.getClass().getName());
                        outcome.onSuccess(Boolean.FALSE);
                    }
                }
            });
        }

        outcome.onSuccess(Boolean.TRUE);
        Window.setStatus("");
    }
}
