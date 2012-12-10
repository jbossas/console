package org.jboss.as.console.client.shared.state;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Heiko Braun
 * @date 12/10/12
 */
public class StaleGlobalModel extends GwtEvent<StaleGlobalModel.StaleModelListener> {

    public static final String HOSTS = "hosts";
    public static final String SERVER_GROUPS = "server-groups";
    public static final String SERVER_CONFIGURATIONS = "server-configurations";
    public static final String SERVER_INSTANCES = "server-instances";

    public static final Type TYPE = new Type<StaleModelListener>();
    private String modelName;


    public StaleGlobalModel(String modelName) {
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


