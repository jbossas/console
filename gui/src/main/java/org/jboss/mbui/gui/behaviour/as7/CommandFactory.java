package org.jboss.mbui.gui.behaviour.as7;

import com.google.gwt.core.client.Scheduler;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.dmr.client.ModelNode;
import org.jboss.mbui.gui.behaviour.ModelDrivenCommand;
import org.jboss.mbui.gui.behaviour.StatementEvent;
import org.jboss.mbui.gui.reification.strategy.SelectStrategy;
import org.jboss.mbui.model.Dialog;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 3/6/13
 */
public class CommandFactory {

    private final DispatchAsync dispatcher;

    public CommandFactory(DispatchAsync dispatcher) {
        this.dispatcher = dispatcher;
    }

    ModelDrivenCommand createCommand(
            String operationName,
            OperationContext context)
    {
        if(operationName.equalsIgnoreCase("add"))
        {
            return createAddCmd(context);
        }
        else if (operationName.equals("remove"))
        {
            return createRemoveCmd(context);
        }

        throw new RuntimeException("Cannot resolve command to operation name: "+ operationName);

    }

    private ModelDrivenCommand createRemoveCmd(final OperationContext context) {
        return new ModelDrivenCommand() {
            @Override
            public void execute(Dialog dialog, Object data) {

                final ModelNode operation = context.getAddress().asResource(context.getStatementContext());
                operation.get(OP).set(REMOVE);
                final String label = operation.get(ADDRESS).asString();   // TODO

                Feedback.confirm(
                        Console.MESSAGES.deleteTitle("Resource"),
                        Console.MESSAGES.deleteConfirm(label),
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean confirmed) {

                                if(confirmed)
                                {
                                    dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
                                        @Override
                                        public void onSuccess(DMRResponse dmrResponse) {
                                            ModelNode response = dmrResponse.get();
                                            if(response.isFailure())
                                            {
                                                Console.error(Console.MESSAGES.deletionFailed(label), response.getFailureDescription());
                                            }
                                            else
                                            {
                                                Console.info(Console.MESSAGES.deleted(label));

                                                clearReset(context);

                                            }
                                        }
                                    });
                                }
                            }
                        });
            }
        };
    }

    /**
     * TODO: This is considered a temporary solution.
     *
     * It's difficult to manage the states of all interaction units after modification to the model.
     * This is a very naiv and pragmatic approach with certain (usability) drawbacks.
     *
     * @param context
     */
    private void clearReset(final OperationContext context) {
        // clear the select statement
        context.getCoordinator().fireEvent(
                new StatementEvent(
                        SelectStrategy.SELECT_ID,
                        "selected.entity",
                        null)
        );

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                context.getCoordinator().onReset();
            }
        });
    }

    private ModelDrivenCommand createAddCmd(OperationContext context) {
        return null;
    }

    public ModelDrivenCommand createGenericCommand(final String operationName, final OperationContext context) {


        // TODO: analyse the operation meta data and request users input (parameters) if necessary

        return new ModelDrivenCommand() {
            @Override
            public void execute(Dialog dialog, Object data) {

                final ModelNode operation = context.getAddress().asResource(context.getStatementContext());
                operation.get(OP).set(operationName);
                final String label = operation.get(ADDRESS).asString();

                Feedback.confirm(
                        "Operation: " + operationName ,
                        "Invoke operation " +operationName+ " on " + label + "?",
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean confirmed) {
                                if(confirmed)
                                {
                                    dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
                                        @Override
                                        public void onSuccess(DMRResponse dmrResponse) {
                                            ModelNode response = dmrResponse.get();

                                            String msg = "Operation " +operationName+ " on " + label;

                                            if(response.isFailure())
                                            {
                                                Console.error(Console.MESSAGES.failed(msg), response.getFailureDescription());
                                            }
                                            else
                                            {
                                                Console.info(Console.MESSAGES.successful(msg));

                                                clearReset(context);

                                            }
                                        }
                                    });
                                }
                            }
                        }
                );
            }
        };
    }
}
