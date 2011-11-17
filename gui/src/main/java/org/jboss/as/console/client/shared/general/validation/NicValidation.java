package org.jboss.as.console.client.shared.general.validation;

import org.jboss.as.console.client.shared.general.model.Interface;

import java.util.Map;
import java.util.Set;

/**
 * Validates the nic constraints
 *
 * @author Heiko Braun
 * @date 11/15/11
 */
class NicValidation extends AbstractValidationStep<Interface> {

    private static final String NIC = "nic";
    private static final String NIC_MATCH = "nicMatch";

    @Override
    public boolean doesApplyTo(Interface entity, Map<String, Object> changedValues) {
        Map<String, Object> clean = clearChangeset(changedValues);

        boolean hasSetValues = isSet(entity.getNic()) || isSet(entity.getNicMatch());
        boolean relevantChanges = false;

        Set<String> keys = clean.keySet();
        for(String key : keys)
        {
            if(key.equals(NIC) || key.equals(NIC_MATCH))
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
        tree.createRoot(1,"Is Nic name set?", new Decision<Interface>() {
            @Override
            public boolean evaluate(Interface entity) {
                return isSet(entity.getNic());
            }
        });
        tree.yes(1, 2, "Anything conflicts with Nic name?", new Decision<Interface>() {
            @Override
            public boolean evaluate(Interface entity) {
                Map<String,Object> properties = asProperties(entity);
                properties.remove(NIC);
                return !isEmpty(properties);
            }
        });
        tree.no(1, 3, "Is Nic Match set?", new Decision<Interface>() {
            @Override
            public boolean evaluate(Interface entity) {
                return isSet(entity.getNicMatch());
            }
        });

        tree.no(2, 4, "Success: Nic", SUCCESS);
        tree.yes(2, 5, "When Nic is set, no other values are allowed!", FAILURE);


        tree.yes(3, 6, "Anything conflicts with Nic Match?", new Decision<Interface>() {
            @Override
            public boolean evaluate(Interface entity) {
                 Map<String,Object> properties = asProperties(entity);
                properties.remove(NIC_MATCH);
                return !isEmpty(properties);
            }
        });
        tree.no(3, 7, "Failure: Neither Nic nor Nic Match set", FAILURE);

        tree.yes(6, 8, "When Nic Match is set, no other values are allowed!", FAILURE);
        tree.no(6, 9, "Success: Nic Match", SUCCESS);

        return tree;
    }

}
