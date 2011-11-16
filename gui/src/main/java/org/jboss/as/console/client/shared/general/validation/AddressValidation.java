package org.jboss.as.console.client.shared.general.validation;

import org.jboss.as.console.client.shared.general.model.Interface;

import java.util.Map;

/**
 * Validates the primary address part of an interface declaration.
 *
 * @author Heiko Braun
 * @date 11/15/11
 */
public class AddressValidation extends AbstractValidationStep<Interface> {

    private static final String INET_ADDRESS = "inetAddress";
    private static final String ADDRESS_WILDCARD = "addressWildcard";

    @Override
    protected DecisionTree<Interface> buildDecisionTree(Interface entity, Map<String,Object> changedValues) {

        final Map<String, Object> changeset = clearChangeset(changedValues);

        final DecisionTree<Interface> tree =  new DecisionTree<Interface>(entity);

        // INET ADDRESS
        tree.createRoot(1,"Is Inet address set?", new DecisionTree.Decision<Interface>() {
            @Override
            public boolean evaluate(Interface entity) {
                return isSet(entity.getInetAddress());
            }
        });
        tree.yes(1, 2, "Attempt to modify other values?", new DecisionTree.Decision<Interface>() {
            @Override
            public boolean evaluate(Interface entity) {
                changeset.remove(INET_ADDRESS);
                return !changeset.isEmpty();
            }
        });
        tree.no(1, 3, "Is address wildcard set?", new DecisionTree.Decision<Interface>() {
            @Override
            public boolean evaluate(Interface entity) {
                return isSet(entity.getAddressWildcard());
            }
        });

        tree.yes(2, 4, "Error: When Inet address is set, no other values are possible.", FAILURE);
        tree.no(2, 5, "Success: Inet address", SUCCESS);


        // ADDRESS WILDCARD
        tree.yes(3, 6, "Attempt to modify other values?", new DecisionTree.Decision<Interface>() {
            @Override
            public boolean evaluate(Interface entity) {
                changeset.remove(ADDRESS_WILDCARD);
                return !changeset.isEmpty();
            }
        });
        tree.no(3, 7, "Failure : Neither Inet address nor wildcard set!", FAILURE);


        tree.yes(6, 8, "Error: When address wildcard is set, no other values are possible.", FAILURE);
        tree.no(6, 9, "Success: Address Wildcard", SUCCESS);

        return tree;
    }

}
