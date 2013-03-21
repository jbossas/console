/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jboss.as.console.client;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.DelayedBindRegistry;
import com.gwtplatform.mvp.client.proxy.AsyncCallFailEvent;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import org.jboss.as.console.client.core.AsyncCallHandler;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.core.LoadingPanel;
import org.jboss.as.console.client.core.UIConstants;
import org.jboss.as.console.client.core.UIDebugConstants;
import org.jboss.as.console.client.core.UIMessages;
import org.jboss.as.console.client.core.bootstrap.ChoseProcessor;
import org.jboss.as.console.client.core.bootstrap.EagerLoadHosts;
import org.jboss.as.console.client.core.bootstrap.EagerLoadProfiles;
import org.jboss.as.console.client.core.bootstrap.ExecutionMode;
import org.jboss.as.console.client.core.bootstrap.LoadCompatMatrix;
import org.jboss.as.console.client.core.bootstrap.LoadMainApp;
import org.jboss.as.console.client.core.bootstrap.RegisterSubsystems;
import org.jboss.as.console.client.core.bootstrap.TrackExecutionMode;
import org.jboss.as.console.client.core.gin.Composite;
import org.jboss.as.console.client.core.message.Message;
import org.jboss.as.console.client.core.message.MessageCenter;
import org.jboss.as.console.client.plugins.RuntimeExtensionRegistry;
import org.jboss.as.console.client.plugins.SubsystemRegistry;
import org.jboss.as.console.client.shared.Preferences;
import org.jboss.as.console.client.shared.help.HelpSystem;
import org.jboss.gwt.flow.client.Async;
import org.jboss.gwt.flow.client.Outcome;

import java.util.EnumSet;

/**
 * Main application entry point.
 * Executes a two phased init process:
 * <ol>
 *     <li>Identify management model (standalone vs. domain)
 *     <li>Load main application
 * </ol>
 *
 * @author Heiko Braun
 */
public class Console implements EntryPoint {

    public final static Composite MODULES = GWT.create(Composite.class);
    public final static UIConstants CONSTANTS = GWT.create(UIConstants.class);
    public final static UIDebugConstants DEBUG_CONSTANTS = GWT.create(UIDebugConstants.class);
    public final static UIMessages MESSAGES = GWT.create(UIMessages.class);

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

        // register global code split call handler
        MODULES.getEventBus().addHandler(AsyncCallFailEvent.getType(), new AsyncCallHandler(MODULES.getPlaceManager()));

        // load console css bundle
        ConsoleResources.INSTANCE.css().ensureInjected();

        // display the loading panel
        final Widget loadingPanel = new LoadingPanel().asWidget();
        RootLayoutPanel.get().add(loadingPanel);

        GWT.runAsync(new RunAsyncCallback() {
            public void onFailure(Throwable caught) {
                Window.alert("Failed to load application components!");
            }

            public void onSuccess() {
                DelayedBindRegistry.bind(MODULES);


                // dump prefs
                for(Preferences.Key key : Preferences.Key.values())
                {
                    String prefValue = Preferences.get(key) !=null ? Preferences.get(key) : "n/a";
                    System.out.println(key.getTitle()+": "+ prefValue);
                }

                // Bootstrap outcome: Load main application or display error message

                Outcome<BootstrapContext> bootstrapOutcome = new Outcome<BootstrapContext>() {
                    @Override
                    public void onFailure() {
                        // currently we only deal with authentication errors
                        RootLayoutPanel.get().remove(loadingPanel);

                        String cause = "";
                        if(MODULES.getBootstrapContext().getLastError()!=null)
                            cause = MODULES.getBootstrapContext().getLastError().getMessage();

                        HTMLPanel explanation = new HTMLPanel("<div style='padding-top:150px;padding-left:120px;'><h2>The management interface could not be loaded.</h2><pre>"+cause+"</pre></div>");
                        RootLayoutPanel.get().add(explanation);
                    }

                    @Override
                    public void onSuccess(BootstrapContext context) {

                        RootLayoutPanel.get().remove(loadingPanel);

                        new LoadMainApp(
                                MODULES.getBootstrapContext(),
                                MODULES.getPlaceManager(),
                                MODULES.getTokenFormatter()).execute();
                    }
                };

                // Ordered execution: if any of these fail, the interface wil not be loaded

                new Async<BootstrapContext>().waterfall(
                        bootstrapOutcome, // outcome
                        MODULES.getBootstrapContext(), // shared context

                        // bootstrap functions
                        new ExecutionMode(MODULES.getDispatchAsync()),
                        new TrackExecutionMode(MODULES.getAnalytics()),
                        new LoadCompatMatrix(MODULES.modelVersions()),
                        new RegisterSubsystems(MODULES.getSubsystemRegistry()),
                        new ChoseProcessor(),
                        new EagerLoadProfiles(MODULES.getProfileStore(), MODULES.getCurrentSelectedProfile()),
                        new EagerLoadHosts(MODULES.getDomainEntityManager())
                );

            }

        });
    }

    public static void info(String message) {
        getMessageCenter().notify(
                new Message(message, Message.Severity.Info)
        );
    }

    public static void error(String message) {
        getMessageCenter().notify(
                new Message(message, Message.Severity.Error)
        );
    }

    public static void error(String message, String detail) {
        getMessageCenter().notify(
                new Message(message, detail, Message.Severity.Error)
        );
    }

    public static void warning(String message) {
        getMessageCenter().notify(
                new Message(message, Message.Severity.Warning)
        );
    }

    public static void warning(String message, boolean sticky) {
        Message msg = sticky ?
                new Message(message, Message.Severity.Warning, EnumSet.of(Message.Option.Sticky)) :
                new Message(message, Message.Severity.Warning);

        getMessageCenter().notify(msg);
    }

    public static void warning(String message, String detail, boolean sticky) {
        Message msg = sticky ?
                new Message(message, detail, Message.Severity.Warning, EnumSet.of(Message.Option.Sticky)) :
                new Message(message, detail, Message.Severity.Warning);


        getMessageCenter().notify(msg);
    }

    public static void warning(String message, String detail) {
        getMessageCenter().notify(
                new Message(message, detail, Message.Severity.Warning)
        );
    }

    public static void schedule(final Command cmd)
    {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                cmd.execute();
            }
        });
    }

    public static EventBus getEventBus() {
        return MODULES.getEventBus();
    }

    @Deprecated
    public static MessageCenter getMessageCenter() {
        return MODULES.getMessageCenter();
    }

    public static PlaceManager getPlaceManager() {
        return MODULES.getPlaceManager();
    }

    public static BootstrapContext getBootstrapContext()
    {
        return MODULES.getBootstrapContext();
    }

    @Deprecated
    public static HelpSystem getHelpSystem() {
        return MODULES.getHelpSystem();
    }


    @Deprecated
    public static native boolean visAPILoaded() /*-{
        return false;
    }-*/;


    public static SubsystemRegistry getSubsystemRegistry() {
        return MODULES.getSubsystemRegistry();
    }

    public static RuntimeExtensionRegistry getRuntimeLHSItemExtensionRegistry() {
        return MODULES.getRuntimeLHSItemExtensionRegistry();
    }
}
