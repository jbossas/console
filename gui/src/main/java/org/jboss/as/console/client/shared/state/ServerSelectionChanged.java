package org.jboss.as.console.client.shared.state;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Heiko Braun
 * @date 12/10/12
 */
public class ServerSelectionChanged extends GwtEvent<ServerSelectionChanged.ChangeListener> {

    public static final Type TYPE = new Type<ChangeListener>();

    @Override
    public Type<ChangeListener> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ChangeListener listener) {
        listener.onServerSelectionChanged();
    }

    public interface ChangeListener extends EventHandler {
        void onServerSelectionChanged();
    }
}

