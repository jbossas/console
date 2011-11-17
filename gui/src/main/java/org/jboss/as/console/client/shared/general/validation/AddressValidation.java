package org.jboss.as.console.client.shared.general.validation;

import org.jboss.as.console.client.shared.general.model.Interface;

import java.util.Map;
import java.util.Set;

/**
 * Validates the primary address part of an interface declaration.
 *
 * @author Heiko Braun
 * @date 11/15/11
 */
class AddressValidation extends AbstractValidationStep<Interface> {

    private static final String INET_ADDRESS = "inetAddress";
    private static final String ADDRESS_WILDCARD = "addressWildcard";

    @Override
    public boolean doesApplyTo(Interface entity, Map<String, Object> changedValues) {

        Map<String, Object> clean = clearChangeset(changedValues);

        boolean hasSetValues = isSet(entity.getInetAddress()) || isSet(entity.getAddressWildcard());
        boolean relevantChanges = false;

        Set<String> keys = clean.keySet();
        for(String key : keys)
        {
            if(key.equals(INET_ADDRESS) || key.equals(ADDRESS_WILDCARD))
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
        tree.createRoot(1,"Is Inet address set?", new Decision<Interface>() {
            @Override
            public boolean evaluate(Interface entity) {
                return isSet(entity.getInetAddress());
            }
        });
        tree.yes(1, 2, "Anything conflicts with Inet Address?", new Decision<Interface>() {
            @Override
            public boolean evaluate(Interface entity) {
                Map<String,Object> properties = asProperties(entity);
                properties.remove(INET_ADDRESS);
                return !isEmpty(properties);
            }
        });
        tree.no(1, 3, "Is address wildcard set?", new Decision<Interface>() {
            @Override
            public boolean evaluate(Interface entity) {
                return isSet(entity.getAddressWildcard());
            }
        });

        tree.yes(2, 4, "When Inet address is set, no other values are possible.", FAILURE);
        tree.no(2, 5, "Valid Inet address", SUCCESS);


        // ADDRESS WILDCARD
        tree.yes(3, 6, "Anything conflicts with address wildcard?", new Decision<Interface>() {
            @Override
            public boolean evaluate(Interface entity) {
                Map<String,Object> properties = asProperties(entity);
                properties.remove(ADDRESS_WILDCARD);
                return !isEmpty(properties);
            }
        });
        tree.no(3, 7, "Neither Inet address nor wildcard set!", FAILURE);


        tree.yes(6, 8, "When address wildcard is set, no other values are possible.", FAILURE);
        tree.no(6, 9, "Valid Address Wildcard", SUCCESS);

        return tree;
    }

}
