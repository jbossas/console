package org.jboss.mbui.gui.reification.strategy;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import org.jboss.ballroom.client.widgets.InlineLink;
import org.jboss.mbui.gui.behaviour.NavigationEvent;
import org.jboss.mbui.gui.reification.Context;
import org.jboss.mbui.gui.reification.ContextKey;
import org.jboss.mbui.model.structure.InteractionUnit;
import org.jboss.mbui.model.structure.Link;
import org.jboss.mbui.model.structure.QName;

/**
 * @author Heiko Braun
 * @date 2/26/13
 */
public class LinkStrategy implements ReificationStrategy<ReificationWidget> {

    private EventBus eventBus;

    @Override
    public boolean prepare(InteractionUnit interactionUnit, Context context) {
        eventBus = context.get(ContextKey.EVENTBUS);
        //assert eventBus!=null : "Event bus is required to execute TriggerStrategy";

        return eventBus!=null;
    }

    @Override
    public ReificationWidget reify(InteractionUnit interactionUnit, Context context) {
        LinkAdapter adapter = new LinkAdapter(interactionUnit);
        return adapter;
    }

    @Override
    public boolean appliesTo(InteractionUnit interactionUnit) {
        return interactionUnit instanceof Link;
    }

    class LinkAdapter implements ReificationWidget
    {
        private final InteractionUnit unit;
        private final InlineLink widget;

        public LinkAdapter(final InteractionUnit interactionUnit) {
            this.unit = interactionUnit;

            this.widget = new InlineLink(interactionUnit.getLabel());

            this.widget.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {

                    QName target = ((Link)interactionUnit).getTarget();

                    NavigationEvent navigationEvent  = new NavigationEvent(
                            NavigationEvent.ID, target
                    );

                    eventBus.fireEventFromSource(
                            navigationEvent,
                            getInteractionUnit().getId()
                    );
                }
            });

            // NOTE: the output is declared within the constructor of a link unit

        }

        @Override
        public InteractionUnit getInteractionUnit() {
            return unit;
        }

        @Override
        public void add(ReificationWidget widget) {

           throw new RuntimeException("Should not be called on atomic unit");
        }

        @Override
        public Widget asWidget() {
            return widget;
        }
    }
}
