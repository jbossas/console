package org.jboss.as.console.client.domain.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;


/**
 * @author Heiko Braun
 * @date 2/7/11
 */
public class ServerGroupSelectionEvent extends GwtEvent<ServerGroupSelectionEvent.ServerGroupSelectionListener> {

    public static final Type TYPE = new Type<ServerGroupSelectionListener>();

    private String serverGroupName;

    public ServerGroupSelectionEvent(String ServerGroupName) {
        super();
        this.serverGroupName = ServerGroupName;
    }

    @Override
    public Type<ServerGroupSelectionListener> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ServerGroupSelectionListener listener) {
        listener.onServerGroupSelection(serverGroupName);
    }

    public String getServerGroupName() {
        return serverGroupName;
    }

    public interface ServerGroupSelectionListener extends EventHandler {
        void onServerGroupSelection(String ServerGroupName);
    }
}

