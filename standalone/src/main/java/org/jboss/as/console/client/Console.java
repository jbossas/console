package org.jboss.as.console.client;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.gwtplatform.mvp.client.DelayedBindRegistry;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.core.gin.CoreUI;

/**
 * Main application entry point.
 *
 * @author Heiko Braun
 */
public class Console implements EntryPoint {

    public final static CoreUI MODULES = GWT.create(CoreUI.class);

    public void onModuleLoad() {
        // Defer all application initialisation code to onModuleLoad2() so that the
        // UncaughtExceptionHandler can catch any unexpected exceptions.
        Log.setUncaughtExceptionHandler();

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                onModuleLoad2();
            }
        });
    }

    public void onModuleLoad2() {
        DelayedBindRegistry.bind(MODULES);

        if(!GWT.isScript())
        {
            // Verify the domain API is available
            // Has the server been started?

            BootstrapContext bootstrap = MODULES.getBootstrapContext();
            String url = bootstrap.getProperty(BootstrapContext.DOMAIN_API);
            final String message = "Unable to connect domain API: '"+url+
                    "'. Has the server been started?";

            RequestBuilder rb = new RequestBuilder(
                    RequestBuilder.GET,
                    url
            );
            try {
                rb.sendRequest(null, new RequestCallback()
                {
                    @Override
                    public void onResponseReceived(Request request, Response response) {
                        if(response.getStatusCode()!=200)
                            Window.alert(message);
                        else
                            MODULES.getPlaceManager().revealCurrentPlace();
                    }

                    @Override
                    public void onError(Request request, Throwable exception) {
                            Window.alert(message);
                    }
                });
            }
            catch(Exception e)
            {
                Window.alert(message);
            }
        }
        else
        {
            MODULES.getPlaceManager().revealCurrentPlace();
        }


    }
}
