package org.jboss.as.console.client.core;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Window;
import com.gwtplatform.mvp.client.proxy.AsyncCallFailEvent;
import com.gwtplatform.mvp.client.proxy.AsyncCallFailHandler;
import com.gwtplatform.mvp.client.proxy.PlaceManager;

/**
 * Handler which is called in case an async call fails. This can happen when a presenter behind a split point should be
 * revealed and there's a network failure. In this case it is essential to unlock the placemanager.
 * <p/>
 * <p>Before navigation the placemanager fires a 'lock' event (see {@com.gwtplatform.mvp.client.proxy.LockInteractionEvent})
 * which causes the {@com.gwtplatform.mvp.client.RootPresenter} to insert an invisible 'glass' container in front of
 * all other elements (to prevent user events during navigation). When the place was revealed a matching 'unlock' event
 * is fired. In case of an unsuccessful async call this 'unlock' event will not be fired and the 'glass' will not be
 * removed. This gives the impression that the UI has crashed.
 * <p/>
 * <p>So this handler displays an error message, unlocks the placemanager and reveals the last successful place again.
 * <p/>
 *
 * @author Harald Pehl
 * @date 03/20/2013
 */
public class AsyncCallHandler implements AsyncCallFailHandler
{
    private final PlaceManager placeManager;

    public AsyncCallHandler(final PlaceManager placeManager) {this.placeManager = placeManager;}

    @Override
    public void onAsyncCallFail(final AsyncCallFailEvent asyncCallFailEvent)
    {
        StringBuilder message = new StringBuilder().append("Async call failed.");
        Throwable caught = asyncCallFailEvent.getCaught();
        if (caught != null)
        {
            message.append(" Reason: ").append(caught.getMessage());
        }
        else
        {
            message.append(" No reason was provided.");
        }
        Log.error(message.toString());
        Window.alert("Lost connection to the server.");

        placeManager.unlock();
        placeManager.revealCurrentPlace();
    }
}
