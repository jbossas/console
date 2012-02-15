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
import org.jboss.as.console.client.shared.dispatch.AsyncCommand;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.dmr.client.ModelNode;

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

        // :read-attribute(name=process-type)
        final ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_ATTRIBUTE_OPERATION);
        operation.get(NAME).set("process-type");
        operation.get(ADDRESS).setEmptyList();

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                bootstrap.setProperty(BootstrapContext.STANDALONE, "false");
                Log.error(caught.getMessage());
                callback.onSuccess(Boolean.FALSE);
            }

            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode response = result.get();
                boolean isServer = response.get(RESULT).asString().equals("Server");
                bootstrap.setProperty(BootstrapContext.STANDALONE, Boolean.valueOf(isServer).toString());

                callback.onSuccess(Boolean.TRUE);

            }
        });

    }
}
