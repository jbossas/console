/*
 * JBoss, Home of Professional Open Source
 * Copyright <YEAR> Red Hat Inc. and/or its affiliates and other contributors
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
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.mvp.client.DelayedBindRegistry;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.core.gin.CoreUI;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.dmr.client.ModelNode;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

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
                            identifyManagementModel();
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
            identifyManagementModel();
        }


    }

    private void identifyManagementModel() {
        // distinguish standalone and domain mode
        final BootstrapContext bootstrap = MODULES.getBootstrapContext();
        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_NAMES_OPERATION);
        operation.get(CHILD_TYPE).set("subsystem");
        operation.get(ADDRESS).setEmptyList();

        MODULES.getDispatchAsync().execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                bootstrap.setProperty(BootstrapContext.STANDALONE, "false");
                loadMainApp();
            }

            @Override
            public void onSuccess(DMRResponse result) {
                bootstrap.setProperty(BootstrapContext.STANDALONE, "true");
                loadMainApp();
            }
        });

    }

    private void loadMainApp() {
        MODULES.getPlaceManager().revealCurrentPlace();
    }
}
