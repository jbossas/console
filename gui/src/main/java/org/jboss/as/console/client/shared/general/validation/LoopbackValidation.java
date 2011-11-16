package org.jboss.as.console.client.shared.general.validation;

import org.jboss.as.console.client.shared.general.model.Interface;

import java.util.Map;
import java.util.Set;

/**
 * Validates the loopback constraints
 *
 * @author Heiko Braun
 * @date 11/15/11
 */
class LoopbackValidation extends AbstractValidationStep<Interface> {

    private static final String LOOPBACK = "loopback";
    private static final String LOOPBACK_ADDRESS = "loopbackAddress";

    @Override
    public boolean doesApplyTo(Interface entity, Map<String, Object> changedValues) {
        Map<String, Object> clean = clearChangeset(changedValues);

        boolean hasSetValues = entity.isLoopback() || isSet(entity.getLoopbackAddress());
        boolean relevantChanges = false;

        Set<String> keys = clean.keySet();
        for(String key : keys)
        {
            if(key.equals(LOOPBACK) || key.equals(LOOPBACK_ADDRESS))
            {
                relevantChanges = true;
                break;
            }
        }

        return hasSetValues || relevantChanges;
    }

    @Override
    protected DecisionTree<Interface> buildDecisionTree(Interface entity, Map<String,Object> changedValues) {

        final Map<String, Object> changeset = clearChangeset(changedValues);

        final DecisionTree<Interface> tree =  new DecisionTree<Interface>(entity);

        // INET ADDRESS
        tree.createRoot(1,"Is Loopback set?", new Decision<Interface>() {
            @Override
            public boolean evaluate(Interface entity) {
                return entity.isLoopback();
            }
        });
        tree.yes(1, 2, "Attempt to modify other values?", new Decision<Interface>() {
            @Override
            public boolean evaluate(Interface entity) {
                changeset.remove(LOOPBACK);
                return !changeset.isEmpty();
            }
        });
        tree.no(1, 3, "Is Loopback Address set?", new Decision<Interface>() {
            @Override
            public boolean evaluate(Interface entity) {
                return isSet(entity.getLoopbackAddress());
            }
        });

        tree.no(2, 4, "Success: Loopback", SUCCESS);
        tree.yes(2, 5, "When Loopback is set, no other values are allowed!", FAILURE);


        tree.yes(3, 6, "Attempt to modify other values?", new Decision<Interface>() {
            @Override
            public boolean evaluate(Interface entity) {
                changeset.remove(LOOPBACK_ADDRESS);
                return !changeset.isEmpty();
            }
        });
        tree.no(3, 7, "Failure: Neither Loopback nor Loopback address set", FAILURE);

        tree.yes(6, 8, "When Loopback address is set, no other values are allowed!", FAILURE);
        tree.no(6, 9, "Success: Loopback address", SUCCESS);

        return tree;
    }

}
