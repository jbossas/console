package org.jboss.as.console.client.mbui.cui.behaviour;

import org.jboss.as.console.client.mbui.aui.aim.Behaviour;
import org.jboss.as.console.client.mbui.aui.aim.Container;
import org.jboss.as.console.client.mbui.aui.aim.InteractionUnit;
import org.jboss.as.console.client.mbui.aui.aim.InteractionUnitVisitor;
import org.jboss.as.console.client.mbui.aui.aim.Trigger;
import org.jboss.as.console.client.mbui.aui.aim.TriggerType;

import java.util.Set;

/**
 * @author Heiko Braun
 * @date 11/16/12
 */
public class Integrity {

    public static void check(InteractionUnit container, final Set<Behaviour> behaviours)
            throws IntegrityException {

        final IntegrityException err = new IntegrityException();

        container.accept(new InteractionUnitVisitor() {
            @Override
            public void startVisit(Container container) {
                if(container.doesTrigger())
                    checkDeclared(container, err);

            }

            @Override
            public void visit(InteractionUnit interactionUnit) {
                if(interactionUnit.doesTrigger())
                    checkDeclared(interactionUnit, err);

            }

            @Override
            public void endVisit(Container container) {

            }

            void checkDeclared(InteractionUnit unit, IntegrityException exception)
            {
                // check each declared trigger against existing behaviours
                Set<Trigger<TriggerType>> producedTypes = unit.getOutputs();

                for(Trigger<TriggerType> event : producedTypes)
                {
                    boolean match = false;
                    for(Behaviour candidate : behaviours)
                    {
                        if(candidate.isTriggeredBy(event))
                        {
                            match = true;
                            break;
                        }
                    }

                    if(!match)
                        err.add(unit.getId(), "no behaviour for <<"+event.getId()+">>");
                }

            }
        });

        if(err.needsToBeRaised())
            throw err;
    }
}
