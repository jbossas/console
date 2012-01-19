package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.dispatch.AsyncCommand;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaWorkmanager;
import org.jboss.as.console.client.shared.subsys.jca.model.WorkmanagerPool;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 12/1/11
 */
public class LoadWorkmanagerCmd implements AsyncCommand<List<JcaWorkmanager>>{


    private DispatchAsync dispatcher;
    private EntityAdapter<JcaWorkmanager> adapter;
    private EntityAdapter<WorkmanagerPool> poolAdapter;

    public LoadWorkmanagerCmd(
            DispatchAsync dispatcher,
            ApplicationMetaData metaData) {

        this.dispatcher = dispatcher;
        this.adapter= new EntityAdapter<JcaWorkmanager>(JcaWorkmanager.class, metaData);
        this.poolAdapter = new EntityAdapter<WorkmanagerPool>(WorkmanagerPool.class, metaData);

    }

    @Override
    public void execute(final AsyncCallback<List<JcaWorkmanager>> callback) {


        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "jca");
        operation.get(CHILD_TYPE).set("workmanager");
        operation.get(RECURSIVE).set(true);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                List<Property> children = response.get(RESULT).asPropertyList();
                List<JcaWorkmanager> managers = new ArrayList<JcaWorkmanager>(children.size());

                for(Property child : children)
                {
                    ModelNode value = child.getValue();

                    JcaWorkmanager entity = adapter.fromDMR(value);

                    if(value.hasDefined("long-running-threads"))
                    {
                        List<WorkmanagerPool> pools = parseThreadPool(value.get("long-running-threads").asPropertyList(), false);
                        entity.setLongRunning(pools);
                    }
                    else {
                        entity.setLongRunning(Collections.EMPTY_LIST);
                    }

                    if(value.hasDefined("short-running-threads"))
                    {
                        List<WorkmanagerPool> pools = parseThreadPool(value.get("short-running-threads").asPropertyList(), true);
                        entity.setShortRunning(pools);
                    }
                    else
                    {
                        entity.setShortRunning(Collections.EMPTY_LIST);
                    }

                    managers.add(entity);

                }

                callback.onSuccess(managers);
            }
        });
    }

    private List<WorkmanagerPool> parseThreadPool(List<Property> values, boolean shortRunning) {
        List<WorkmanagerPool> result = new ArrayList<WorkmanagerPool>();

        for(Property value : values)
        {
            WorkmanagerPool pool = poolAdapter.fromDMR(value.getValue());

            pool.setShortRunning(shortRunning);
            result.add(pool);
        }

        return result;
    }
}
