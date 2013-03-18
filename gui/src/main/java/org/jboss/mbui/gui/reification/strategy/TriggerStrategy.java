package org.jboss.mbui.gui.reification.strategy;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.mbui.gui.behaviour.InteractionEvent;
import org.jboss.mbui.gui.reification.Context;
import org.jboss.mbui.gui.reification.ContextKey;
import org.jboss.mbui.model.behaviour.Resource;
import org.jboss.mbui.model.behaviour.ResourceType;
import org.jboss.mbui.model.structure.InteractionUnit;
import org.jboss.mbui.model.structure.QName;
import org.jboss.mbui.model.structure.Trigger;
import org.jboss.mbui.model.structure.as7.StereoTypes;

/**
 * @author Heiko Braun
 * @date 2/26/13
 */
public class TriggerStrategy implements ReificationStrategy<ReificationWidget> {

    @Override
    public boolean prepare(InteractionUnit interactionUnit, Context context) {
        return true;
    }

    @Override
    public ReificationWidget reify(InteractionUnit interactionUnit, Context context) {
        TriggerAdapter adapter = null;

        // requirement, see Trigger implementation
        assert interactionUnit.doesProduce();

        if (interactionUnit != null)
        {
            EventBus eventBus = context.get(ContextKey.EVENTBUS);
            assert eventBus!=null : "Event bus is required to execute TriggerStrategy";

            adapter = new TriggerAdapter(interactionUnit, eventBus);
        }
        return adapter;
    }

    @Override
    public boolean appliesTo(InteractionUnit interactionUnit) {
        return interactionUnit instanceof Trigger;
    }

    class TriggerAdapter implements ReificationWidget
    {
        private final InteractionUnit unit;
        private final EventBus eventBus;
        private final ToolButton button;

        public TriggerAdapter(final InteractionUnit<StereoTypes> interactionUnit, final EventBus eventBus) {
            this.unit = interactionUnit;
            this.eventBus = eventBus;

            this.button = new org.jboss.ballroom.client.widgets.tools.ToolButton(interactionUnit.getLabel());

            this.button.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {

                    QName trigger = interactionUnit.getOutputs().iterator().next().getId();
                    QName justification = interactionUnit.getId();

                    InteractionEvent triggerEvent = new InteractionEvent(trigger);
                    triggerEvent.setPayload(trigger);

                    eventBus.fireEventFromSource(
                            triggerEvent,
                            justification
                    );
                }
            });

            // NOTE: the output is declared within the constructor of a trigger unit

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
            return button;
        }
    }
}
