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

package org.jboss.as.console.client.shared.model;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import org.jboss.as.console.client.core.ApplicationProperties;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.dmr.client.ModelNode;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 3/18/11
 */
public class SubsystemStoreImpl implements SubsystemStore {

    private DispatchAsync dispatcher;
    private BeanFactory factory;
    private ApplicationProperties bootstrap;

    @Inject
    public SubsystemStoreImpl(DispatchAsync dispatcher, ApplicationProperties bootstrap, BeanFactory factory) {
        this.dispatcher = dispatcher;
        this.bootstrap = bootstrap;
        this.factory = factory;
    }

    @Override
    public void loadSubsystems(final String profileName, final AsyncCallback<List<SubsystemRecord>> callback) {

        assert profileName!=null && !profileName.equals("") : "Illegal profile name: "+profileName;

        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_NAMES_OPERATION);
        operation.get(CHILD_TYPE).set("subsystem");
        operation.get(ADDRESS).setEmptyList();

        if(bootstrap.getProperty(ApplicationProperties.STANDALONE).equals("false"))
        {
            operation.get(ADDRESS).add("profile", profileName);
        }

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if(response.isFailure())
                {
                    callback.onFailure(new RuntimeException(
                            "Failed to load profile "+profileName+": " +response.getFailureDescription()
                    ));
                }
                else
                {
                    List<ModelNode> payload = response.get("result").asList();

                    List<SubsystemRecord> records = new ArrayList<SubsystemRecord>(payload.size());
                    for(int i=0; i<payload.size(); i++)
                    {
                        SubsystemRecord record = factory.subsystem().as();
                        record.setKey(payload.get(i).asString());
                        records.add(record);
                    }

                    callback.onSuccess(records);
                }
            }
        });
    }
}
