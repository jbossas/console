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

package org.jboss.as.console.client.core;

import com.google.gwt.user.client.rpc.AsyncCallback;
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
public class BootstrapCmd implements AsyncCommand<Boolean>{


    private BootstrapContext bootstrap;
    private DispatchAsync dispatcher;

    public BootstrapCmd(BootstrapContext bootstrap, DispatchAsync dispatcher) {
        this.bootstrap = bootstrap;
        this.dispatcher = dispatcher;
    }

    @Override
    public void execute(final AsyncCallback<Boolean> callback) {

        final ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_NAMES_OPERATION);
        operation.get(CHILD_TYPE).set("subsystem");
        operation.get(ADDRESS).setEmptyList();

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                bootstrap.setProperty(BootstrapContext.STANDALONE, "false");
                callback.onSuccess(false);
            }

            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                boolean outcome = response.get("outcome").asString().equals("success");
                bootstrap.setProperty(BootstrapContext.STANDALONE, Boolean.valueOf(outcome).toString());

                callback.onSuccess(outcome);

            }
        });

    }
}
