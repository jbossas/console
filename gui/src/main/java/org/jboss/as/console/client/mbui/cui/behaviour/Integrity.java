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
                if(container.doesTrigger() && isBehaviourDeclared(container))
                    err.add(container.getId());

            }

            @Override
            public void visit(InteractionUnit interactionUnit) {
                if(interactionUnit.doesTrigger() && isBehaviourDeclared(interactionUnit))
                    err.add(interactionUnit.getId());
            }

            @Override
            public void endVisit(Container container) {

            }

            boolean isBehaviourDeclared(InteractionUnit unit)
            {
                boolean isDeclared = false;

                for(Behaviour candidate : behaviours)
                {
                    Set<Trigger<TriggerType>> producedTypes = unit.getOutputs();
                    for(Trigger<TriggerType> event : producedTypes)
                    {
                        if(candidate.isTriggeredBy(event))
                        {
                            isDeclared = true;
                            break;
                        }
                    }

                    if(isDeclared) break;
                }

                return isDeclared;
            }
        });

        if(err.needsToBeRaised())
            throw err;
    }
}
