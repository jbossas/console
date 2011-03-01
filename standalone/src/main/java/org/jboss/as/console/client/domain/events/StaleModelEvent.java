package org.jboss.as.console.client.domain.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;


/**
 * A generic model event used to indicated changes to a certain, 'named' model.
 *
 * @author Heiko Braun
 * @date 2/7/11
 */
public class StaleModelEvent extends GwtEvent<StaleModelEvent.StaleModelListener> {

    public static final String SERVER_GROUPS = "server-groups";

    public static final Type TYPE = new Type<StaleModelListener>();
    private String modelName;

    public StaleModelEvent(String modelName) {
        super();
        this.modelName= modelName;
    }

    @Override
    public Type<StaleModelListener> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(StaleModelListener listener) {
        listener.onStaleModel(modelName);
    }

    public String getModelName() {
        return modelName;
    }

    public interface StaleModelListener extends EventHandler {
        void onStaleModel(String modelName);
    }
}

