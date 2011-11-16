package org.jboss.as.console.client.shared.general.validation;

import org.jboss.as.console.client.shared.general.model.Interface;

import java.util.Map;

/**
 * Validates the nic constraints
 *
 * @author Heiko Braun
 * @date 11/15/11
 */
public class NicValidation extends AbstractValidationStep<Interface> {

    private static final String NIC = "nic";
    private static final String NIC_MATCH = "nicMatch";

    @Override
    protected DecisionTree<Interface> buildDecisionTree(Interface entity, Map<String,Object> changedValues) {

        final Map<String, Object> changeset = clearChangeset(changedValues);

        final DecisionTree<Interface> tree =  new DecisionTree<Interface>(entity);

        // INET ADDRESS
        tree.createRoot(1,"Is Nic name set?", new DecisionTree.Decision<Interface>() {
            @Override
            public boolean evaluate(Interface entity) {
                return isSet(entity.getNic());
            }
        });
        tree.yes(1, 2, "Attempt to modify other values?", new DecisionTree.Decision<Interface>() {
            @Override
            public boolean evaluate(Interface entity) {
                changeset.remove(NIC);
                return !changeset.isEmpty();
            }
        });
        tree.no(1, 3, "Is Nic Match set?", new DecisionTree.Decision<Interface>() {
            @Override
            public boolean evaluate(Interface entity) {
                return isSet(entity.getNicMatch());
            }
        });

        tree.no(2, 4, "Success: Nic", SUCCESS);
        tree.yes(2, 5, "When Nic is set, no other values are allowed!", FAILURE);


        tree.yes(3, 6, "Attempt to modify other values?", new DecisionTree.Decision<Interface>() {
            @Override
            public boolean evaluate(Interface entity) {
                changeset.remove(NIC_MATCH);
                return !changeset.isEmpty();
            }
        });
        tree.no(3, 7, "Failure: Neither Nic nor Nic Match set", FAILURE);

        tree.yes(6, 8, "When Nic Match is set, no other values are allowed!", FAILURE);
        tree.no(6, 9, "Success: Nic Match", SUCCESS);

        return tree;
    }

}
