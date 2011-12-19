package org.jboss.as.console.client.shared.runtime.ds;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.dispatch.AsyncCommand;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.runtime.RuntimeBaseAddress;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.dmr.client.ModelNode;

import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;
import static org.jboss.dmr.client.ModelDescriptionConstants.RESULT;

/**
 * @author Heiko Braun
 * @date 12/19/11
 */
public class LoadDataSourceCmd implements AsyncCommand<List<DataSource>>{

    private DispatchAsync dispatcher;
    private ApplicationMetaData  metaData;
    private EntityAdapter<DataSource> adapter;

    public LoadDataSourceCmd(DispatchAsync dispatcher, ApplicationMetaData metaData) {
        this.dispatcher = dispatcher;
        this.metaData = metaData;
        this.adapter = new EntityAdapter<DataSource>(DataSource.class, metaData);
    }

    @Override
    public void execute(final AsyncCallback<List<DataSource>> callback) {
        ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem", "datasources");

        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(ADDRESS).set(address);
        operation.get(CHILD_TYPE).set("data-source");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode response  = result.get();
                List<DataSource> datasources = adapter.fromDMRList(response.get(RESULT).asList());
                callback.onSuccess(datasources);
            }
        });
    }
}
