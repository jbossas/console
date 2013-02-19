package org.jboss.mbui.gui.behaviour;

import org.jboss.mbui.model.behaviour.Behaviour;
import org.jboss.mbui.model.behaviour.ResourceType;
import org.jboss.mbui.model.structure.Container;
import org.jboss.mbui.model.structure.InteractionUnit;
import org.jboss.mbui.model.structure.impl.InteractionUnitVisitor;
import org.jboss.mbui.model.behaviour.Resource;

import java.util.Set;

/**
 * TODO: the producer verification is missing ...
 *
 * @author Heiko Braun
 * @date 11/16/12
 */
public class Integrity {

    public static void check(InteractionUnit container, final Set<Behaviour> behaviours)
            throws IntegrityErrors {

        final IntegrityErrors err = new IntegrityErrors();

        container.accept(new InteractionUnitVisitor() {
            @Override
            public void startVisit(Container container) {
                if(container.doesProduce())
                    assertConsumer(container, err);

            }

            @Override
            public void visit(InteractionUnit interactionUnit) {
                if(interactionUnit.doesProduce())
                    assertConsumer(interactionUnit, err);

            }

            @Override
            public void endVisit(Container container) {

            }

            /**
             * Assertion that a consumer exists for the produced resources of an interaction unit.
             *
             * @param unit
             * @param exception
             */
            void assertConsumer(InteractionUnit unit, IntegrityErrors exception)
            {
                // check each declared trigger against existing behaviours
                Set<Resource<ResourceType>> producedTypes = unit.getOutputs();

                for(Resource<ResourceType> event : producedTypes)
                {
                    boolean match = false;
                    for(Behaviour candidate : behaviours)
                    {
                        if(candidate.doesConsume(event))
                        {
                            match = true;
                            break;
                        }
                    }

                    if(!match)
                        exception.add(unit.getId(), "no behaviour for <<"+event.getId()+">>");
                }

            }
        });

        if(err.needsToBeRaised())
            throw err;
    }
}
