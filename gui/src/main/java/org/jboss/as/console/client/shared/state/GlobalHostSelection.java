package org.jboss.as.console.client.shared.state;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Heiko Braun
 * @date 12/10/12
 */
public class GlobalHostSelection extends GwtEvent<GlobalHostSelection.HostSelectionListener> {

    public static final Type TYPE = new Type<HostSelectionListener>();

    private String hostName;

    public GlobalHostSelection(String hostName) {
        super();
        this.hostName = hostName;
    }

    @Override
    public Type<HostSelectionListener> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(HostSelectionListener listener) {
        listener.onHostSelection(hostName);
    }

    public String getHostName() {
        return hostName;
    }

    public interface HostSelectionListener extends EventHandler {
        void onHostSelection(String hostName);
    }
}
