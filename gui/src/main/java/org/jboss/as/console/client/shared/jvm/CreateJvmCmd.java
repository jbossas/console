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

package org.jboss.as.console.client.shared.jvm;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.AddressableModelCmd;
import org.jboss.as.console.client.shared.dispatch.AsyncCommand;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.dmr.client.ModelNode;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 5/18/11
 */
public class CreateJvmCmd extends AddressableModelCmd implements AsyncCommand<Boolean>{

    public CreateJvmCmd(DispatchAsync dispatcher, BeanFactory factory, ModelNode address) {
        super(dispatcher, factory, address);
    }

    @Override
    public void execute(AsyncCallback<Boolean> callback) {
        throw new RuntimeException();
    }

    public void execute(final Jvm jvm, final AsyncCallback<Boolean> callback) {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(ADD);
        operation.get(ADDRESS).set(address);

        //ModelNode jvmModel = new ModelNode();
        operation.get("heap-size").set(jvm.getHeapSize());
        operation.get("max-heap-size").set(jvm.getMaxHeapSize());
        operation.get("debug-enabled").set(jvm.isDebugEnabled());

        //operation.get("jvm").set(jvm.getName(), jvmModel);

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                boolean success = response.get(OUTCOME).asString().equals(SUCCESS);

                if(success)
                    Console.info("Success : Create JVM settings");
                else
                    Console.error("Failed: Create JVM settings", response.toString());

                callback.onSuccess(success);
            }
        });

    }

}
