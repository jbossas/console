package org.jboss.as.console.client.shared.state;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import org.jboss.as.console.client.domain.model.ServerInstance;

/**
 * @author Heiko Braun
 * @date 12/10/12
 */
public class GlobalServerSelection extends GwtEvent<GlobalServerSelection.ServerSelectionListener> {

    public static final Type TYPE = new Type<ServerSelectionListener>();

    private ServerInstance server;

    public GlobalServerSelection(ServerInstance server) {
        super();
        this.server = server;
    }

    @Override
    public Type<ServerSelectionListener> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ServerSelectionListener listener) {
        listener.onServerSelection(server);
    }

    public ServerInstance getServerName() {
        return server;
    }

    public interface ServerSelectionListener extends EventHandler {
        void onServerSelection(ServerInstance server);
    }
}
