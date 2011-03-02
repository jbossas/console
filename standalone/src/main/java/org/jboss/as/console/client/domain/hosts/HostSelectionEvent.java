package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;


/**
 * @author Heiko Braun
 * @date 2/7/11
 */
public class HostSelectionEvent extends GwtEvent<HostSelectionEvent.HostSelectionListener> {

    public static final Type TYPE = new Type<HostSelectionListener>();

    private String hostName;

    public HostSelectionEvent(String hostName) {
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
        void onHostSelection(String HostName);
    }
}

