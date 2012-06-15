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

package org.jboss.as.console.client.core.bootstrap;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.shared.Preferences;
import org.jboss.as.console.client.shared.dispatch.AsyncCommand;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.standalone.StandaloneServer;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 5/19/11
 */
public class ExecutionMode implements AsyncCommand<Boolean>{


    private BootstrapContext bootstrap;
    private DispatchAsync dispatcher;

    public ExecutionMode(BootstrapContext bootstrap, DispatchAsync dispatcher) {
        this.bootstrap = bootstrap;
        this.dispatcher = dispatcher;
    }

    @Override
    public void execute(final AsyncCallback<Boolean> callback) {




        ModelNode operation = new ModelNode();
        operation.get(OP).set(COMPOSITE);
        operation.get(ADDRESS).setEmptyList();

        List<ModelNode> steps = new ArrayList<ModelNode>();

        ModelNode execModeOp = new ModelNode();
        execModeOp.get(OP).set(READ_ATTRIBUTE_OPERATION);
        execModeOp.get(NAME).set("process-type");
        execModeOp.get(ADDRESS).setEmptyList();
        steps.add(execModeOp);

        ModelNode envProperties = new ModelNode();
        envProperties.get(OP).set(READ_ATTRIBUTE_OPERATION);
        envProperties.get(ADDRESS).add("core-service", "platform-mbean");
        envProperties.get(ADDRESS).add("type", "runtime");
        envProperties.get(NAME).set("system-properties");
        steps.add(envProperties);

        operation.get(STEPS).set(steps);

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                bootstrap.setProperty(BootstrapContext.STANDALONE, "false");
                bootstrap.setlastError(caught);
                Log.error(caught.getMessage());
                callback.onSuccess(Boolean.FALSE);
            }

            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode response = result.get();
                List<Property> propertyList = response.get(RESULT).asPropertyList();
                boolean outcome = true;

                for(Property step : propertyList)
                {
                    ModelNode stepResult = step.getValue();
                    if(step.getName().equals("step-1"))
                    {
                        boolean isServer = stepResult.get(RESULT).asString().equals("Server");
                        bootstrap.setProperty(BootstrapContext.STANDALONE, Boolean.valueOf(isServer).toString());
                    }
                    else
                    {
                        List<Property> properties = stepResult.get(RESULT).asPropertyList();
                        for(Property prop : properties)
                        {
                            if(prop.getName().equals("user.language"))
                            {
                                final String preferredLang = prop.getValue().asString();
                                if(!preferredLang.equals(Preferences.get("as7_ui_locale", "en")))
                                {
                                    Preferences.set("as7_ui_locale", preferredLang);
                                    bootstrap.setlastError(new Throwable(
                                        "The user.language has changed. Please reload the web interface!"
                                    ));
                                    outcome = false;
                                }

                                break;
                            }
                        }
                    }
                }

                callback.onSuccess(outcome);

            }
        });

    }
}
