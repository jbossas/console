package org.jboss.as.console.client.shared.state;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Heiko Braun
 * @date 12/10/12
 */
public class HostSelectionChanged extends GwtEvent<HostSelectionChanged.ChangeListener> {

    public static final String HOSTS = "hosts";
    public static final String SERVER_GROUPS = "server-groups";
    public static final String SERVER_CONFIGURATIONS = "server-configurations";
    public static final String SERVER_INSTANCES = "server-instances";

    public static final Type TYPE = new Type<ChangeListener>();
    private String modelName;


    public HostSelectionChanged(String modelName) {
        super();
        this.modelName= modelName;
    }

    @Override
    public Type<ChangeListener> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ChangeListener listener) {
        listener.onHostSelectionChanged();
    }

    public String getModelName() {
        return modelName;
    }

    public interface ChangeListener extends EventHandler {
        void onHostSelectionChanged();
    }
}

