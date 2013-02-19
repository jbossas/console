package org.jboss.mbui.gui.behaviour;

import org.jboss.mbui.model.behaviour.Behaviour;
import org.jboss.mbui.model.behaviour.ResourceType;
import org.jboss.mbui.model.structure.Container;
import org.jboss.mbui.model.structure.InteractionUnit;
import org.jboss.mbui.model.structure.impl.InteractionUnitVisitor;
import org.jboss.mbui.model.behaviour.Resource;

import java.util.Set;

/**
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
                if (container.doesProduce())
                    assertConsumer(container, behaviours, err);

                if (container.doesConsume())
                    assertProducer(container, behaviours, err);

            }

            @Override
            public void visit(InteractionUnit interactionUnit) {
                if (interactionUnit.doesProduce())
                    assertConsumer(interactionUnit, behaviours, err);

                if (interactionUnit.doesConsume())
                    assertProducer(interactionUnit, behaviours, err);

            }

            @Override
            public void endVisit(Container container) {

            }
        });

        if(err.needsToBeRaised())
            throw err;
    }

    /**
     * Assertion that a consumer exists for the produced resources of an interaction unit.
     *
     * @param unit
     * @param err
     */
    private static void assertConsumer(InteractionUnit unit, Set<Behaviour> behaviours, IntegrityErrors err) {

        Set<Resource<ResourceType>> producedTypes = unit.getOutputs();

        for (Resource<ResourceType> resource : producedTypes) {
            boolean match = false;
            for (Behaviour candidate : behaviours) {
                if (candidate.doesConsume(resource)) {
                    match = true;
                    break;
                }
            }

            if (!match)
                err.add(unit.getId(), "Missing consumer for <<" + resource + ">>");
        }

    }

    private static void assertProducer(InteractionUnit unit, Set<Behaviour> behaviours, IntegrityErrors err) {
        Set<Resource<ResourceType>> consumedTypes = unit.getInputs();

        for (Resource<ResourceType> resource : consumedTypes) {
            boolean match = false;
            for (Behaviour candidate : behaviours) {
                if (candidate.doesProduce(resource)) {
                    match = true;
                    break;
                }
            }

            if (!match)
                err.add(unit.getId(), "Missing producer for <<" + resource + ">>");
        }
    }
}
