package org.jboss.mbui.gui.behaviour;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import org.jboss.mbui.model.structure.QName;

/**
 * A system event is created by the framework.
 *
 * @see KernelContract
 *
 * @author Heiko Braun
 * @date 11/15/12
 */
public class SystemEvent extends GwtEvent<SystemEvent.Handler> {


    public static final QName ACTIVATE_ID = QName.valueOf("org.jboss.activate");

    public static final Type TYPE = new Type<Handler>();

    private QName id;
    private Object payload;

    public SystemEvent(QName id) {
        super();
        this.id = id;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public QName getId() {
        return id;
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(Handler listener) {
        if(listener.accepts(this))
            listener.onSystemEvent(this);
    }

    public interface Handler extends EventHandler {
        boolean accepts(SystemEvent event);
        void onSystemEvent(SystemEvent event);
    }

    public static void fire(HasHandlers source, SystemEvent eventInstance) {
        source.fireEvent(eventInstance);
    }
}
