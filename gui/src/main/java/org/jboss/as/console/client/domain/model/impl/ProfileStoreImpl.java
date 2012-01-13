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

package org.jboss.as.console.client.domain.model.impl;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ProfileStore;
import org.jboss.as.console.client.domain.profiles.CurrentProfileSelection;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.dmr.client.ModelDescriptionConstants;
import org.jboss.dmr.client.ModelNode;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.OP;

/**
 * @author Heiko Braun
 * @date 3/18/11
 */
public class ProfileStoreImpl implements ProfileStore {

    private DispatchAsync dispatcher;
    private BeanFactory factory;
    private ModelNode operation;
    private CurrentProfileSelection currentProfile;
    private List<ProfileRecord> cachedRecords = null;

    @Inject
    public ProfileStoreImpl(DispatchAsync dispatcher, BeanFactory factory, CurrentProfileSelection currentProfile) {
        this.dispatcher = dispatcher;
        this.factory = factory;
        this.currentProfile = currentProfile;

        this.operation = new ModelNode();
        operation.get(OP).set(ModelDescriptionConstants.READ_CHILDREN_NAMES_OPERATION);
        operation.get("child-type").set("profile");
        operation.get(ModelDescriptionConstants.ADDRESS).setEmptyList();
    }

    @Override
    public void loadProfiles(final AsyncCallback<List<ProfileRecord>> callback) {

        if(null==this.cachedRecords)
        {
            dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
                @Override
                public void onFailure(Throwable caught) {
                    callback.onFailure(caught);
                }

                @Override
                public void onSuccess(DMRResponse result) {
                    ModelNode response = result.get();
                    List<ModelNode> payload = response.get("result").asList();

                    List<ProfileRecord> records = new ArrayList<ProfileRecord>(payload.size());
                    for(int i=0; i<payload.size(); i++)
                    {
                        ProfileRecord record = factory.profile().as();
                        record.setName(payload.get(i).asString());
                        records.add(record);
                    }

                    ProfileStoreImpl.this.cachedRecords = records;
                    callback.onSuccess(records);
                }
            });
        }
        else
        {
            // provide cached results
            callback.onSuccess(cachedRecords);
        }
    }
}
