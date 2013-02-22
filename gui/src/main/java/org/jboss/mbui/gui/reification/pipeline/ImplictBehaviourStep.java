package org.jboss.mbui.gui.reification.pipeline;

import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.mbui.gui.behaviour.BehaviourExecution;
import org.jboss.mbui.gui.behaviour.as7.LoadResourceProcedure;
import org.jboss.mbui.gui.behaviour.as7.SaveChangesetProcedure;
import org.jboss.mbui.gui.reification.Context;
import org.jboss.mbui.gui.reification.ContextKey;
import org.jboss.mbui.gui.reification.ReificationException;
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
public class ImplictBehaviourStep extends ReificationStep
{
    private final DispatchAsync dispatcher;

    public ImplictBehaviourStep(DispatchAsync dispatcher)
    {
        super("implicit behaviour");
        this.dispatcher = dispatcher;
    }

    @Override
    public void execute(final Dialog dialog, final Context context) throws ReificationException
    {
        final BehaviourExecution behaviourExecution = context.get(ContextKey.COORDINATOR);
        InteractionUnit root = dialog.getInterfaceModel();

        root.accept(new InteractionUnitVisitor()
        {
            @Override
            public void startVisit(Container container)
            {
                registerDefaultBehaviour(dialog, container, behaviourExecution);
            }

            @Override
            public void visit(InteractionUnit interactionUnit)
            {
                registerDefaultBehaviour(dialog, interactionUnit, behaviourExecution);
            }

            @Override
            public void endVisit(Container container)
            {

            }
        });
    }

    private void registerDefaultBehaviour(Dialog dialog, InteractionUnit unit, BehaviourExecution behaviourContract) {

        // map consumers to outputs of interaction units
        if(unit.doesProduce())
        {
            for(Resource<ResourceType> resource : unit.getOutputs())
            {
                if(LoadResourceProcedure.ID.equals(resource.getId()))
                    behaviourContract.addProcedure(new LoadResourceProcedure(dialog, unit.getId(), dispatcher));
                else if(SaveChangesetProcedure.ID.equals(resource.getId()))
                    behaviourContract.addProcedure(new SaveChangesetProcedure(dialog, unit.getId(), dispatcher));
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
