package org.jboss.mbui.gui.behaviour.as7;

import org.jboss.mbui.gui.behaviour.BehaviourExecution;
import org.jboss.mbui.model.Dialog;
import org.jboss.mbui.model.behaviour.Resource;
import org.jboss.mbui.model.behaviour.ResourceType;
import org.jboss.mbui.model.structure.Container;
import org.jboss.mbui.model.structure.InteractionUnit;
import org.jboss.mbui.model.structure.impl.InteractionUnitVisitor;

/**
 * Naive implementation to register the implicit behaviour (Procedure) with the coordinator.
 *
 * @author Heiko Braun
 * @date 2/21/13
 */
public class ImplictBehaviour {

    private ProcedureContract contract;
    private final Dialog dialog;

    public ImplictBehaviour(final Dialog dialog, ProcedureContract contract) {
        this.dialog = dialog;
        this.contract = contract;
    }

    public void register(final BehaviourExecution behaviourExecution) {

        InteractionUnit root = dialog.getInterfaceModel();

        root.accept(new InteractionUnitVisitor() {
            @Override
            public void startVisit(Container container) {
                registerDefaultBehaviour(container, behaviourExecution);
            }

            @Override
            public void visit(InteractionUnit interactionUnit) {
                registerDefaultBehaviour(interactionUnit, behaviourExecution);
            }

            @Override
            public void endVisit(Container container) {

            }
        });
    }

    private void registerDefaultBehaviour(InteractionUnit unit, BehaviourExecution behaviourContract) {

        // map consumers to outputs of interaction units
        if(unit.doesProduce())
        {
            for(Resource<ResourceType> resource : unit.getOutputs())
            {
                if(LoadResourceProcedure.ID.equals(resource.getId()))
                    behaviourContract.registerProcedure(new LoadResourceProcedure(dialog, unit.getId(), contract.getDispatcher()));
                else if(SaveChangesetProcedure.ID.equals(resource.getId()))
                    behaviourContract.registerProcedure(new SaveChangesetProcedure(dialog, unit.getId(), contract.getDispatcher()));
            }
        }

        // map producers to inputs of interaction units
        if(unit.doesConsume())
        {
            for(Resource<ResourceType> resource : unit.getInputs())
            {
                // TODO: Some of these inout are implicitly satisfied with the procedures registered as consumers above ...
                System.out.println("Unit "+unit.getId()+" lacks producer for " + resource);
                // currently none available ...
            }
        }
    }
}
