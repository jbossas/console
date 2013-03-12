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
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.dmr.client.ModelNode;
import org.jboss.gwt.flow.client.Control;
import org.jboss.gwt.flow.client.Function;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 5/19/11
 */
public class ExecutionMode implements Function<BootstrapContext> {


    private DispatchAsync dispatcher;

    public ExecutionMode(DispatchAsync dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void execute(final Control<BootstrapContext> control) {

        // :read-attribute(name=process-type)
        final ModelNode operation = new ModelNode();
        operation.get(OP).set(COMPOSITE);
        operation.get(ADDRESS).setEmptyList();

        List<ModelNode> steps = new ArrayList<ModelNode>();

        // exec type
        ModelNode execType = new ModelNode();
        execType.get(OP).set(READ_ATTRIBUTE_OPERATION);
        execType.get(NAME).set("process-type");
        execType.get(ADDRESS).setEmptyList();
        steps.add(execType);

        // prod version
        ModelNode prodVersion = new ModelNode();
        prodVersion.get(OP).set(READ_ATTRIBUTE_OPERATION);
        prodVersion.get(NAME).set("product-version");
        prodVersion.get(ADDRESS).setEmptyList();
        steps.add(prodVersion);

        // release version
        ModelNode releaseVersion = new ModelNode();
        releaseVersion.get(OP).set(READ_ATTRIBUTE_OPERATION);
        releaseVersion.get(NAME).set("release-version");
        releaseVersion.get(ADDRESS).setEmptyList();
        steps.add(releaseVersion);
        
        operation.get(STEPS).set(steps);

        final BootstrapContext bootstrap = control.getContext();

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {

                bootstrap.setlastError(caught);
                Log.error(caught.getMessage());

                control.abort();
            }

            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode response = result.get();

                if(response.isFailure())
                {
                    bootstrap.setlastError(new RuntimeException(response.getFailureDescription()));
                    control.abort();
                }
                else
                {

                    // capture exec mode
                    ModelNode execResult = response.get(RESULT).get("step-1");
                    boolean isServer = execResult.get(RESULT).asString().equals("Server");
                    bootstrap.setProperty(BootstrapContext.STANDALONE, Boolean.valueOf(isServer).toString());

                    ModelNode prodVersionResult = response.get(RESULT).get("step-2");
                    String prodVersion = prodVersionResult.get(RESULT).isDefined() ?
                            prodVersionResult.get(RESULT).asString() : "";

                    ModelNode releaseResult = response.get(RESULT).get("step-3");
                    String releaseVersion = releaseResult.get(RESULT).isDefined() ?
                            releaseResult.get(RESULT).asString() : "";

                    bootstrap.setReleaseVersion(releaseVersion);
                    bootstrap.setProdVersion(prodVersion);

                    control.proceed();
                }


            }

        });

    }
}
