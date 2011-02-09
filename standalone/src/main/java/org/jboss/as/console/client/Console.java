package org.jboss.as.console.client;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.gwtplatform.mvp.client.DelayedBindRegistry;
import org.jboss.as.console.client.gin.CoreUI;

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

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                onModuleLoad2();
            }
        });
      }


    public void onModuleLoad2() {
        DelayedBindRegistry.bind(MODULES);
        MODULES.getPlaceManager().revealCurrentPlace();
    }
}
