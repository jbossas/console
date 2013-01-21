package org.jboss.mbui.gui.behaviour.as7;

import com.google.gwt.core.client.Scheduler;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.widgets.forms.AddressBinding;
import org.jboss.dmr.client.ModelNode;
import org.jboss.mbui.gui.behaviour.InteractionCoordinator;
import org.jboss.mbui.gui.behaviour.ModelDrivenCommand;
import org.jboss.mbui.gui.behaviour.PresentationEvent;
import org.jboss.mbui.gui.behaviour.Procedure;
import org.jboss.mbui.model.mapping.MappingType;
import org.jboss.mbui.model.mapping.as7.ResourceMapping;
import org.jboss.mbui.model.structure.Dialog;
import org.jboss.mbui.model.structure.InteractionUnit;
import org.jboss.mbui.model.structure.QName;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 1/21/13
 */
public class LoadResourceProcedure extends Procedure {
    private final static QName ID = new QName("org.jboss.as", "load");
    private final static QName RESULT_ID = QName.valueOf("org.jboss.as:form-update");

    private final DispatchAsync dispatcher;
    private final InteractionCoordinator coordinator;
    private final AddressContext addressContext;

    public LoadResourceProcedure (
            final QName source,
            InteractionCoordinator coordinator,
            DispatchAsync dispatcher, AddressContext addressContext) {

        super(ID, source);
        this.coordinator = coordinator;
        this.dispatcher = dispatcher;
        this.addressContext = addressContext;

        setCommand(new ModelDrivenCommand() {
            @Override
            public void execute(Dialog dialog, Object data) {

                InteractionUnit source = dialog.findUnit(getRequiredSource());

                ResourceMapping resourceMapping = source.getMapping(MappingType.RESOURCE);
                AddressBinding address = AddressBinding.fromString(resourceMapping.getAddress());

                loadResource(source.getName(), address);
            }
        });
    }

    private void loadResource(final String name, AddressBinding address) {

        String[] args = addressContext.resolve();
        ModelNode operation = address.asResource(args);
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(INCLUDE_RUNTIME).set(true);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                final  ModelNode response = dmrResponse.get();

                if (response.isFailure())
                    Console.error(Console.MESSAGES.modificationFailed(name), response.getFailureDescription());
                else
                    Console.info(Console.MESSAGES.modified(name));

                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {

                        PresentationEvent presentation = new PresentationEvent(RESULT_ID);
                        presentation.setPayload(response.get(RESULT));
                        // source and target are the same
                        presentation.setTarget(getRequiredSource());

                        coordinator.fireEvent(presentation);
                    }
                });

            }
        });

    }
}
