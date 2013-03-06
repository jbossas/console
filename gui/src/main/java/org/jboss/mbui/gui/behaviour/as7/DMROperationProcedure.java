package org.jboss.mbui.gui.behaviour.as7;

import com.google.gwt.user.client.Window;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;
import org.jboss.mbui.gui.behaviour.ModelDrivenCommand;
import org.jboss.mbui.gui.behaviour.Precondition;
import org.jboss.mbui.gui.behaviour.Procedure;
import org.jboss.mbui.gui.behaviour.StatementContext;
import org.jboss.mbui.model.Dialog;
import org.jboss.mbui.model.behaviour.Resource;
import org.jboss.mbui.model.behaviour.ResourceType;
import org.jboss.mbui.model.mapping.MappingType;
import org.jboss.mbui.model.mapping.as7.AddressMapping;
import org.jboss.mbui.model.mapping.as7.ResourceMapping;
import org.jboss.mbui.model.structure.InteractionUnit;
import org.jboss.mbui.model.structure.QName;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.jboss.dmr.client.ModelDescriptionConstants.OP;

/**
 * Executes an operation on a DMR resource.
 * <p/>
 * The actual entity address is resolved from the {@link ResourceMapping}
 * attached to the {@link InteractionUnit} that triggered this procedure (justification).
 * <p/>
 * The operation name is derived from the suffix of {@link Resource} being produced.
 *
 * @author Heiko Braun
 * @date 1/21/13
 */
public class DMROperationProcedure extends Procedure {

    public final static QName PREFIX = new QName("org.jboss.as", "resource-operation");

    private enum DEFAULT_OPERATION { ADD, REMOVE };

    private final DispatchAsync dispatcher;
    private final Dialog dialog;

    private InteractionUnit unit;
    private AddressMapping address;
    private String operationName;

    public DMROperationProcedure (
            final Dialog dialog,
            final QName id,
            final QName justification,
            DispatchAsync dispatcher) {

        super(id, justification);
        this.dialog = dialog;
        this.dispatcher = dispatcher;

        init();

        setCommand(new ModelDrivenCommand() {
            @Override
            public void execute(Dialog dialog, Object data) {

                boolean matchedDefault = false;

                // TODO: move this part into initialisation phase of the procedure
                // oit doesn't need to be executed every time
                for(DEFAULT_OPERATION op : DEFAULT_OPERATION.values())
                {
                    if(op.name().equalsIgnoreCase(operationName))
                    {
                        matchedDefault = true;
                    }
                }

                if(matchedDefault)
                {
                    invokeDefaultOp(operationName, address);
                }
                else
                {
                    invokeGenericOp(operationName, address);
                }
            }
        });

        // behaviour model meta data
        setInputs(new Resource<ResourceType>(id, ResourceType.Event));

    }

    private void init() {
        unit = dialog.findUnit(getJustification());
        operationName = getId().getSuffix();

        if(operationName==null)
            throw new IllegalArgumentException("Illegal operation name mapping: "+ unit.getId()+ " (suffix required)");

        ResourceMapping resourceMapping = unit.findMapping(MappingType.RESOURCE);
        address = AddressMapping.fromString(resourceMapping.getAddress());


        // TODO: Refactor init procedure into default precondition ...
        // it appears in several procedures

        // check preconditions of the address token
        final Set<String> requiredStatements = new HashSet<String>();
        address.asResource(new StatementContext() {
            @Override
            public String resolve(final String key) {
                // value expressions
                requiredStatements.add(key);
                return "";
            }

            @Override
            public String[] resolveTuple(String key) {
                return new String[]{"", ""};
            }
        });

        // any value expression key becomes a precondition matched against the statement context
        if(requiredStatements.size()>0)
        {
            setPrecondition(new Precondition() {
                @Override
                public boolean isMet(StatementContext statementContext) {
                    boolean isMet = false;
                    for(String key : requiredStatements)
                    {
                        isMet = statementContext.resolve(key)!=null;
                        if(!isMet) {
                            Window.alert("Required statement not given: " + key);
                            break; // exit upon first value expression that cannot be resolved
                        }
                    }
                    return isMet;
                }
            });
        }

    }

    private void invokeDefaultOp(final String operationName, AddressMapping address) {

        System.out.println("default op");

        final ModelNode operation = address.asResource(statementContext);
        operation.get(OP).set(operationName);


        System.out.println("> " + operation);
    }

    private void invokeGenericOp(final String operationName, AddressMapping address) {


        final ModelNode operation = address.asResource(statementContext);
        operation.get(OP).set(operationName);


        System.out.println("> " + operation);
        /*
        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                final  ModelNode response = dmrResponse.get();

                if (response.isFailure())
                    Console.error(Console.MESSAGES.failed("Operation " + operationName), response.getFailureDescription());

                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {

                        PresentationEvent presentation = new PresentationEvent(getJustification());

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

                        // unit and target are the same
                        presentation.setTarget(getJustification());

                        coordinator.fireEvent(presentation);
                    }
                });

            }
        });    */

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

    @Override
    public String toString() {
        return "DMROperation "+ getJustification();
    }
}
