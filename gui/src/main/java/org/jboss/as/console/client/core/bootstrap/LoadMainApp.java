package org.jboss.as.console.client.core.bootstrap;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.TokenFormatter;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.dispatch.AsyncCommand;
import org.jboss.ballroom.client.layout.LHSHighlightEvent;

import java.util.List;

/**
 *
 * Either loads the default place or one specified from external context (URL tokens)
 *
 * @author Heiko Braun
 * @date 12/7/11
 */
public class LoadMainApp implements AsyncCommand<Boolean> {

    @Override
    public void execute(AsyncCallback<Boolean> callback) {

        String initialToken = History.getToken();

        final PlaceManager placeManager = Console.MODULES.getPlaceManager();
        TokenFormatter formatter = Console.MODULES.getTokenFormatter();

        if(!initialToken.isEmpty())
        {
            List<PlaceRequest> hierarchy = formatter.toPlaceRequestHierarchy(initialToken);
            final PlaceRequest placeRequest = hierarchy.get(hierarchy.size() - 1);

            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    placeManager.revealPlace(placeRequest, true);
                }
            });

            // the page needs to be rendered otherwise the event listener would not be attached...

            Timer t = new Timer() {
                @Override
                public void run() {
                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            Console.MODULES.getEventBus().fireEvent(
                                    new LHSHighlightEvent(placeRequest.getNameToken())
                            );
                        }
                    });
                }
            };

            t.schedule(2500);

        }
        else {
            placeManager.revealDefaultPlace();
        }

        callback.onSuccess(Boolean.TRUE);
    }
}