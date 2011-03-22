package org.jboss.as.console.client.domain.model.impl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerGroupStore;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.dmr.client.ModelDescriptionConstants;
import org.jboss.dmr.client.ModelNode;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.OP;

/**
 * @author Heiko Braun
 * @date 3/18/11
 */
public class ServerGroupStoreImpl implements ServerGroupStore {

    private DispatchAsync dispatcher;
    private BeanFactory factory = GWT.create(BeanFactory.class);

    @Inject
    public ServerGroupStoreImpl(DispatchAsync dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void loadServerGroups(final AsyncCallback<List<ServerGroupRecord>> callback) {
        final ModelNode operation = new ModelNode();
        operation.get(OP).set(ModelDescriptionConstants.READ_CHILDREN_NAMES_OPERATION);
        operation.get("child-type").set("server-group");
        operation.get(ModelDescriptionConstants.ADDRESS).setEmptyList();

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                List<ModelNode> payload = response.get("result").asList();

                List<ServerGroupRecord> records = new ArrayList<ServerGroupRecord>(payload.size());

                for(int i=0; i<payload.size(); i++)
                {
                    ServerGroupRecord record = factory.serverGroup().as();
                    record.setGroupName(payload.get(i).asString());
                    record.setProfileName("default");  // TODO: remaining properties
                    records.add(record);
                }

                callback.onSuccess(records);
            }
        });

    }

    @Override
    public void persist(ServerGroupRecord updatedEntity, final AsyncCallback<Boolean> callback) {

    }

    @Override
    public void deleteGroup(ServerGroupRecord selectedRecord, final AsyncCallback<Boolean> callback) {

    }
}
