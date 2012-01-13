package org.jboss.as.console.client.core.bootstrap;

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

        //System.out.println("init from: " +initialToken);

        PlaceManager placeManager = Console.MODULES.getPlaceManager();
        TokenFormatter formatter = Console.MODULES.getTokenFormatter();

        if(!initialToken.isEmpty())
        {
            List<PlaceRequest> hierarchy = formatter.toPlaceRequestHierarchy(initialToken);
            final PlaceRequest placeRequest = hierarchy.get(hierarchy.size() - 1);
            placeManager.revealPlace(placeRequest, true);

            /*
            Timer t = new Timer() {
                @Override
                public void run() {
                    Console.MODULES.getEventBus().fireEvent(
                            new LHSHighlightEvent(null, placeRequest.getNameToken(), "*")
                    );
                }
            };

            t.schedule(1500);*/


        }
        else {
            placeManager.revealDefaultPlace();
        }

        callback.onSuccess(Boolean.TRUE);
    }
}