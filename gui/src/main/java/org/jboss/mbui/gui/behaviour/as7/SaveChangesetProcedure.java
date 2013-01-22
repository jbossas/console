package org.jboss.mbui.gui.behaviour.as7;

import com.google.gwt.core.client.Scheduler;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.widgets.forms.AddressBinding;
import org.jboss.dmr.client.ModelNode;
import org.jboss.mbui.gui.behaviour.ModelDrivenCommand;
import org.jboss.mbui.gui.behaviour.Procedure;
import org.jboss.mbui.model.mapping.MappingType;
import org.jboss.mbui.model.mapping.as7.ResourceMapping;
import org.jboss.mbui.model.structure.Dialog;
import org.jboss.mbui.model.structure.InteractionUnit;
import org.jboss.mbui.model.structure.QName;

import java.util.HashMap;

/**
 * A default procedure that writes attributes change sets to resource.
 * It creates a composite operation with write-attribute operations for each changed attribute of a resource.
 * <p/>
 * The {@link org.jboss.mbui.gui.behaviour.StatementContext} is used to resolve the resource parent context (i.e. profile, server, host).
 * <p/>
 * The actual address is resolved from the {@link ResourceMapping} attached to the {@link InteractionUnit} that triggered this procedure.
 *
 * @see org.jboss.mbui.gui.behaviour.InteractionEvent#getSource()
 *
 * @author Heiko Braun
 * @date 1/21/13
 */
public class SaveChangesetProcedure extends Procedure {

    private final static QName ID = new QName("org.jboss.as", "save");
    private final DispatchAsync dispatcher;

    public SaveChangesetProcedure(
            final QName source,
            DispatchAsync dispatcher) {

        super(ID, source);
        this.dispatcher = dispatcher;

        setCommand(new ModelDrivenCommand<HashMap>() {
            @Override
            public void execute(Dialog dialog, HashMap data) {

                InteractionUnit source = dialog.findUnit(getRequiredSource());

                ResourceMapping resourceMapping = source.getMapping(MappingType.RESOURCE);
                AddressBinding address = AddressBinding.fromString(resourceMapping.getAddress());

                saveResource(source.getName(), address, data);
            }
        });
    }

    private void saveResource(final String name, AddressBinding address, HashMap<String, Object> changeset) {
        ModelNodeAdapter adapter = new ModelNodeAdapter();

        ModelNode operation = adapter.fromChangeset(
                changeset,
                address.asResource(statementContext));

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode response = dmrResponse.get();

                if (response.isFailure())
                    Console.error(Console.MESSAGES.modificationFailed(name), response.getFailureDescription());
                else
                    Console.info(Console.MESSAGES.modified(name));

                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        coordinator.onReset();
                    }
                });

            }
        });

    }
}
