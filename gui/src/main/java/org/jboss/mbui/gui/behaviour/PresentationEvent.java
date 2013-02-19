package org.jboss.mbui.gui.behaviour;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import org.jboss.mbui.model.structure.QName;

/**
 * A presentation event is the created by a {@link ModelDrivenCommand}.
 * It's typically the result of a function call in preparation to present some data.
 *
 * @author Heiko Braun
 * @date 11/15/12
 */
public class PresentationEvent extends GwtEvent<PresentationEvent.PresentationHandler> {

    public static final Type TYPE = new Type<PresentationHandler>();

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
    public Type<PresentationHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(PresentationHandler listener) {
        if(listener.accepts(this))
            listener.onPresentationEvent(this);
    }

    public interface PresentationHandler extends EventHandler {
        boolean accepts(PresentationEvent event);
        void onPresentationEvent(PresentationEvent event);
    }

    public static void fire(HasHandlers source, PresentationEvent eventInstance) {
        source.fireEvent(eventInstance);
    }

    @Override
    public String toString() {
        return "PresentationEvent{" +
                "id=" + id +
                ", target=" + target +
                '}';
    }
}
