package org.jboss.as.console.client.shared.model;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.dmr.client.ModelDescriptionConstants;
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
    private BeanFactory factory = GWT.create(BeanFactory.class);
    private ModelNode operation;

    @Inject
    public SubsystemStoreImpl(DispatchAsync dispatcher) {
        this.dispatcher = dispatcher;

        this.operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_NAMES_OPERATION);
        operation.get(CHILD_TYPE).set("subsystem");
        operation.get(ADDRESS).setEmptyList();
        operation.get(ADDRESS).add("profile", "default");
    }

    @Override
    public void loadSubsystems(String profileName, final AsyncCallback<List<SubsystemRecord>> callback) {
        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                JSONObject root = JSONParser.parseLenient(result.getResponseText()).isObject();
                JSONArray payload = root.get("result").isArray();

                List<SubsystemRecord> records = new ArrayList<SubsystemRecord>(payload.size());
                for(int i=0; i<payload.size(); i++)
                {
                    SubsystemRecord record = factory.subsystem().as();
                    record.setTitle(payload.get(i).isString().stringValue());
                    records.add(record);
                }

                callback.onSuccess(records);
            }
        });
    }
}
