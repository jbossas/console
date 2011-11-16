package org.jboss.as.console.client.shared.general;

import org.jboss.as.console.client.shared.general.model.Interface;
import org.jboss.ballroom.client.widgets.forms.FormItem;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Heiko Braun
 * @date 11/15/11
 */
public class InterfaceValidation {

    private static final String INET_ADDRESS = "inetAddress";
    private static final String ADDRESS_WILDCARD = "addressWildcard";

    final static String[] inetAddressPrecedence = new String[] {
            INET_ADDRESS,
            ADDRESS_WILDCARD,
            "loopback",
            "loopbackAddress",
            "pointToPoint"
    };


    ValidationResult validate(Interface entity, final Map<String, Object> changedValues) {

        final Map<String, Object> changeset = clearChangeset(changedValues);
        System.out.println("Before : "+changedValues);
        System.out.println("After : "+changeset);

        ValidationResult result = new ValidationResult();

        //   inet address, address wildcard, loopback, point to point

        DecisionTree tree =  new DecisionTree(entity);

        // INET ADDRESS
        tree.createRoot(1,"Is inet address set?", new DecisionTree.Decision() {
            @Override
            public boolean evaluate(Interface entity) {
                return isSet(entity.getInetAddress());
            }
        });
        tree.yes(1, 2, "Attempt to modify other values?", new DecisionTree.Decision() {
            @Override
            public boolean evaluate(Interface entity) {
                changeset.remove(INET_ADDRESS);
                return !changeset.isEmpty();
            }
        });
        tree.no(1, 3, "Is address wildcard set?", new DecisionTree.Decision() {
            @Override
            public boolean evaluate(Interface entity) {
                return isSet(entity.getAddressWildcard());
            }
        });

        tree.yes(2, 4, "Error: when inet address is set, no other values are possible.", FAILURE);
        tree.no(2, 5, "Success: Inet address", SUCCESS);



        // ADDRESS WILDCARD
        tree.yes(3, 6, "Attempt to modify other values?", new DecisionTree.Decision() {
            @Override
            public boolean evaluate(Interface entity) {
                changeset.remove(ADDRESS_WILDCARD);
                return !changeset.isEmpty();
            }
        });
        tree.no(3, 7, "Failure : neither inet address nor wildcard set!", FAILURE);


        tree.yes(6, 8, "Error: when address wildcard is set, no other values are possible.", FAILURE);
        tree.no(6, 9, "Success: Address Wildcard", SUCCESS);

        tree.outputBinTree();
        tree.queryBinTree();

        //System.out.println(success ? "*** FALURE ***" : "*** SUCCESS ***");

        return result;
    }

    private Map<String, Object> clearChangeset(Map<String, Object> changedValues) {

        Map<String,Object> clean = new HashMap<String, Object>();
        Set<String> keys = changedValues.keySet();
        for(String key : keys)
        {
            Object value = changedValues.get(key);
            if(! (value == FormItem.VALUE_SEMANTICS.UNDEFINED))
                clean.put(key, value);
        }
        return clean;
    }

    static DecisionTree.Decision SUCCESS = new DecisionTree.Decision() {
        @Override
        public boolean evaluate(Interface entity) {
            return true;
        }
    };

    static DecisionTree.Decision FAILURE = new DecisionTree.Decision() {
        @Override
        public boolean evaluate(Interface entity) {
            return false;
        }
    };

    private static boolean isSet(String value)
    {
        return value!=null && !value.isEmpty();
    }
}
