package org.jboss.as.console.client.shared.general.validation;

import org.jboss.as.console.client.shared.general.model.Interface;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The decision tree to run interface constraint validations.
 *
 * @author Heiko Braun
 * @date 11/16/11
 */
public class CompositeDecision extends AbstractValidationStep<Interface>{

    private AddressValidation addressValidation = new AddressValidation();
    private NicValidation nicValidation = new NicValidation();
    private LoopbackValidation loopbackValidation = new LoopbackValidation ();
    private OtherConstraintsValidation otherValidation = new OtherConstraintsValidation();

    private List<String> detailMessages = new LinkedList<String>();

    public List<String> getDetailMessages() {
        return detailMessages;
    }

    @Override
    public boolean doesApplyTo(Interface entity, Map<String, Object> changedValues) {
        return true;
    }

    @Override
    protected DecisionTree<Interface> buildDecisionTree(final Interface entity, final Map<String, Object> changedValues) {

        DecisionTree<Interface> tree = new DecisionTree<Interface>(entity);
        tree.createRoot(1, "Attempt to modify interface values?", new Decision<Interface>() {
            @Override
            public boolean evaluate(Interface entity) {
                return !changedValues.isEmpty();
            }
        });

        tree.no(1, 2, "No changes", SUCCESS);
        tree.yes(1, 3, "Any address constraints modified?", new Decision<Interface>() {
            @Override
            public boolean evaluate(Interface entity) {
                return addressValidation.doesApplyTo(entity, changedValues);
            }
        });

        // --------------------------------

        tree.yes(3, 4, "Valid address criteria?", new Decision<Interface>() {
            @Override
            public boolean evaluate(Interface entity) {
                ValidationResult result = addressValidation.validate(entity, changedValues);
                detailMessages.add(result.asMessageString());
                return result.isValid();
            }
        });
        tree.no(3, 5, "Any nic constraints modified?", new Decision<Interface>() {
            @Override
            public boolean evaluate(Interface entity) {
                return nicValidation.doesApplyTo(entity, changedValues);
            }
        });

        tree.yes(4, 6, "Address criteria is valid.", SUCCESS);
        tree.no(4, 7, "Invalid Address criteria!", FAILURE);

        // --------------------------------

        tree.yes(5, 8, "Valid nic constraints?", new Decision<Interface>() {
            @Override
            public boolean evaluate(Interface entity) {
                ValidationResult result = nicValidation.validate(entity, changedValues);
                detailMessages.add(result.asMessageString());
                return result.isValid();
            }
        });
        tree.no(5, 9, "Any loopback constraints modified?", new Decision<Interface>() {
            @Override
            public boolean evaluate(Interface entity) {
                return loopbackValidation.doesApplyTo(entity, changedValues);
            }
        });


        tree.yes(8, 10, "Nic criteria is valid.", SUCCESS);
        tree.no(8, 11, "Invalid Nic criteria!", FAILURE);

        // --------------------------------

        tree.yes(9, 12, "Valid Loopback criteria?", new Decision<Interface>() {
            @Override
            public boolean evaluate(Interface entity) {
                ValidationResult result = loopbackValidation.validate(entity, changedValues);
                detailMessages.add(result.asMessageString());
                return result.isValid();
            }
        });
        tree.no(9, 13, "Other constraints modified?", new Decision<Interface>() {
            @Override
            public boolean evaluate(Interface entity) {
                return otherValidation.doesApplyTo(entity, changedValues);
            }
        });


        tree.yes(12, 14, "Loopback criteria is valid.", SUCCESS);
        tree.no(12, 15, "Invalid Loopback criteria!", FAILURE);

        // --------------------------------

        tree.yes(13, 16, "Valid other constraints?", new Decision<Interface>() {
            @Override
            public boolean evaluate(Interface entity) {
                ValidationResult result = otherValidation.validate(entity, changedValues);
                detailMessages.add(result.asMessageString());
                return result.isValid();
            }
        });
        tree.no(13, 17, "No interface criteria specified!", FAILURE);

        tree.yes(16, 18, "Other criteria is valid.", SUCCESS);
        tree.no(16, 19, "Invalid other criteria!", FAILURE);

        return tree;
    }
}
