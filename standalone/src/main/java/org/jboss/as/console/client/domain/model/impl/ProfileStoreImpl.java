package org.jboss.as.console.client.domain.model.impl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ProfileStore;
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
    private BeanFactory factory = GWT.create(BeanFactory.class);
    private ModelNode operation;

    @Inject
    public ProfileStoreImpl(DispatchAsync dispatcher) {
        this.dispatcher = dispatcher;

        this.operation = new ModelNode();
        operation.get(OP).set(ModelDescriptionConstants.READ_CHILDREN_NAMES_OPERATION);
        operation.get("child-type").set("profile");
        operation.get(ModelDescriptionConstants.ADDRESS).setEmptyList();
    }

    @Override
    public void loadProfiles(final AsyncCallback<List<ProfileRecord>> callback) {

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                List<ModelNode> payload = response.get("result").asList();

                List<ProfileRecord> records = new ArrayList<ProfileRecord>(payload.size());
                for(int i=0; i<payload.size(); i++)
                {
                    ProfileRecord record = factory.profile().as();
                    record.setName(payload.get(i).asString());
                    records.add(record);
                }

                callback.onSuccess(records);
            }
        });
    }
}
