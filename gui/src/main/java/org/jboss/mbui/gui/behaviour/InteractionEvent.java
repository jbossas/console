package org.jboss.mbui.gui.behaviour;

/**
 * @author Heiko Braun
 * @date 11/15/12
 */

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import org.jboss.mbui.model.structure.QName;

/**
 * An interaction event is created by interaction units.
 * It leads to the execution of {@link Procedure}.
 *
 * @author Heiko Braun
 * @date 11/15/12
 */
public class InteractionEvent extends GwtEvent<InteractionEvent.InteractionHandler> {

    public static final Type TYPE = new Type<InteractionHandler>();

    private QName id;
    public Object payload;

    public InteractionEvent(QName id) {
        super();
        this.id = id;
    }

    public QName getId() {
        return id;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    @Override
    public Type<InteractionHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(InteractionHandler listener) {
        if(listener.accepts(this))
            listener.onInteractionEvent(this);
    }

    public interface InteractionHandler extends EventHandler {
        boolean accepts(InteractionEvent kind);
        void onInteractionEvent(InteractionEvent event);
    }

    public static void fire(HasHandlers source, InteractionEvent eventInstance) {
        source.fireEvent(eventInstance);
    }

    @Override
    public String toString() {
        return "InteractionEvent{" +
                "id=" +getId()+
                ", source=" + getSource() +
                '}';
    }
}

