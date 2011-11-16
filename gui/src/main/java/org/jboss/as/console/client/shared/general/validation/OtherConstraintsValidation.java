package org.jboss.as.console.client.shared.general.validation;

import org.jboss.as.console.client.shared.general.model.Interface;

import java.util.Map;
import java.util.Set;

/**
 * Validates the other constraints
 *
 * @author Heiko Braun
 * @date 11/15/11
 */
class OtherConstraintsValidation extends AbstractValidationStep<Interface> {

    private static final String UP = "up";
    private static final String VIRTUAL = "virtual";

    private static final String PUBLIC_ADDRESS = "publicAddress";
    private static final String SITE_LOCAL_ADDRESS= "siteLocal";
    private static final String LINK_LOCAL_ADDRESS = "linkLocal";
    private static final String MULTICAST = "multicast";
    private static final String POINT_TO_POINT = "pointToPoint";

    private static String[] ALL = {
            UP, VIRTUAL, PUBLIC_ADDRESS, SITE_LOCAL_ADDRESS, LINK_LOCAL_ADDRESS, MULTICAST, POINT_TO_POINT
    };

    @Override
    public boolean doesApplyTo(Interface entity, Map<String, Object> changedValues) {
        Map<String, Object> clean = clearChangeset(changedValues);

        boolean hasSetValues = entity.isLoopback() || isSet(entity.getLoopbackAddress());
        boolean relevantChanges = false;

        Set<String> keys = clean.keySet();
        for(String key : keys)
        {
            for(String prop : ALL)
            {
                if(key.equals(prop) )
                {
                    relevantChanges = true;
                    break;
                }
            }

        }

        return hasSetValues || relevantChanges;
    }

    @Override
    protected DecisionTree<Interface> buildDecisionTree(Interface entity, Map<String,Object> changedValues) {

        final Map<String, Object> changeset = clearChangeset(changedValues);

        final DecisionTree<Interface> tree =  new DecisionTree<Interface>(entity);

        // INET ADDRESS
        tree.createRoot(1,"Anything is valid", SUCCESS);

        // TODO: for now we keep it simple

        return tree;
    }

}
