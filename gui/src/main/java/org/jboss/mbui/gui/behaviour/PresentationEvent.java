package org.jboss.mbui.gui.behaviour;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import org.jboss.mbui.model.structure.QName;

/**
 * A presentation event is the created by a {@link DataDrivenCommand}.
 * It's typically the result of a function call in preparation to present some data.
 *
 * @author Heiko Braun
 * @date 11/15/12
 */
public class PresentationEvent extends GwtEvent<PresentationEvent.Handler> {

    public static final Type TYPE = new Type<Handler>();

    private QName id;

    private Object payload;

    private QName target;

    public PresentationEvent(QName id) {
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

    public QName getTarget() {
        return target;
    }

    public void setTarget(QName target) {
        this.target = target;
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(Handler listener) {
        if(listener.accepts(this))
            listener.onStatementEvent(this);
    }

    public interface Handler extends EventHandler {
        boolean accepts(PresentationEvent event);
        void onStatementEvent(PresentationEvent event);
    }

    public static void fire(HasHandlers source, PresentationEvent eventInstance) {
        source.fireEvent(eventInstance);
    }
}
