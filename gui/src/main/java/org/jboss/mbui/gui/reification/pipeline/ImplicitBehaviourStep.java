package org.jboss.mbui.gui.reification.pipeline;

import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.mbui.gui.behaviour.BehaviourExecution;
import org.jboss.mbui.gui.behaviour.Procedure;
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
import org.jboss.mbui.model.structure.QName;
import org.jboss.mbui.model.structure.impl.InteractionUnitVisitor;

import java.util.Map;
import java.util.Set;

/**
 * Naive implementation to register the implicit behaviour (Procedure) with the coordinator.
 *
 * @author Heiko Braun
 * @date 2/21/13
 */
public class ImplicitBehaviourStep extends ReificationStep
{
    private final DispatchAsync dispatcher;

    public ImplicitBehaviourStep(DispatchAsync dispatcher)
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

    private void registerDefaultBehaviour(Dialog dialog, InteractionUnit unit, BehaviourExecution behaviourExecution) {

        // map consumers to outputs of interaction units
        if(unit.doesProduce())
        {
            for(Resource<ResourceType> resource : unit.getOutputs())
            {
                if(LoadResourceProcedure.ID.equals(resource.getId()))
                    behaviourExecution.addProcedure(new LoadResourceProcedure(dialog, unit.getId(), dispatcher));
                else if(SaveChangesetProcedure.ID.equals(resource.getId()))
                    behaviourExecution.addProcedure(new SaveChangesetProcedure(dialog, unit.getId(), dispatcher));
            }
        }

        // map producers to inputs of interaction units
        if(unit.doesConsume())
        {
            for(Resource<ResourceType> input : unit.getInputs())
            {
                // Some of these inputs are implicitly satisfied with the procedures registered as consumers above ...
                // Match input requirements against existing behaviours
                // TODO: Does this catch all ?
                /*boolean matchedOutput = false;
                Map<QName,Set<Procedure>> existing  = behaviourExecution.listProcedures();
                for(QName id : existing.keySet())
                {
                    Set<Procedure> procedures = existing.get(id);
                    for(Procedure proc : procedures)
                    {
                        if(proc.doesProduce())
                        {
                            for(Resource<ResourceType> output : proc.getOutputs())
                            {
                                if(output.equals(input))
                                {
                                    matchedOutput = true;
                                    break;
                                }
                            }
                        }
                    }
                }

                if(!matchedOutput)
                    System.out.println("Unit "+unit.getId()+" lacks producer for " + input);
                    */
            }
        }
    }
}
