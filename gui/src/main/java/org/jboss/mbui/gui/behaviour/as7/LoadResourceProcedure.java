package org.jboss.mbui.gui.behaviour.as7;

import com.google.gwt.core.client.Scheduler;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.widgets.forms.AddressBinding;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.ModelType;
import org.jboss.dmr.client.Property;
import org.jboss.mbui.gui.behaviour.ModelDrivenCommand;
import org.jboss.mbui.gui.behaviour.PresentationEvent;
import org.jboss.mbui.gui.behaviour.Procedure;
import org.jboss.mbui.model.mapping.MappingType;
import org.jboss.mbui.model.mapping.as7.ResourceMapping;
import org.jboss.mbui.model.structure.Dialog;
import org.jboss.mbui.model.structure.InteractionUnit;
import org.jboss.mbui.model.structure.QName;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 1/21/13
 */
public class LoadResourceProcedure extends Procedure {
    private final static QName ID = new QName("org.jboss.as", "load");
    private final static QName RESULT_ID = QName.valueOf("org.jboss.as:form-update");

    private final DispatchAsync dispatcher;

    public LoadResourceProcedure (
            final QName source,
            DispatchAsync dispatcher) {

        super(ID, source);
        this.dispatcher = dispatcher;

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


        final ModelNode operation = address.asResource(statementContext);
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(INCLUDE_RUNTIME).set(true);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                final  ModelNode response = dmrResponse.get();

                if (response.isFailure())
                    Console.error(Console.MESSAGES.modificationFailed(name), response.getFailureDescription());

                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {

                        PresentationEvent presentation = new PresentationEvent(RESULT_ID);

                        // the result is either a single resource or a collection
                        ModelNode result = response.get(RESULT);
                        if(ModelType.LIST==result.getType())
                        {
                            List<ModelNode> collection = result.asList();
                            List normalized = new ArrayList<ModelNode>(collection.size());
                            for(ModelNode model : collection)
                            {
                                ModelNode payload = model.get(RESULT).asObject();
                                assignKeyFromAddressNode(payload, model.get(ADDRESS));
                                normalized.add(payload);
                            }
                            presentation.setPayload(normalized);
                        }
                        else
                        {
                            ModelNode payload = result.asObject();
                            assignKeyFromAddressNode(payload, operation.get(ADDRESS));
                            presentation.setPayload(payload);
                        }

                        // source and target are the same
                        presentation.setTarget(getRequiredSource());

                        coordinator.fireEvent(presentation);
                    }
                });

            }
        });

    }

    /**
     * the model representations we use internally carry along the entity keys.
     * these are derived from the resource address, but will be available as synthetic resource attributes.
     *
     * @param payload
     * @param address
     */
    private static void assignKeyFromAddressNode(ModelNode payload, ModelNode address) {
        List<Property> props = address.asPropertyList();
        Property lastToken = props.get(props.size()-1);
        payload.get("entity.key").set(lastToken.getValue().asString());
    }
}
