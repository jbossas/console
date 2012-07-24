package org.jboss.as.console.client.tools;

import org.jboss.dmr.client.ModelNode;

import java.util.Map;
import java.util.Set;

/**
 * @author Heiko Braun
 * @date 7/24/12
 */
public class DMR {

    public static void mergeChanges(ModelNode target, Map<String,Object> changeset)
    {
        final Set<String> changesetKeys = changeset.keySet();
        final Set<String> attributeNames = target.keys();

        for(String key : changesetKeys)
        {
            boolean matched = false;
            for(String attribute : attributeNames)
            {
                if(key.equals(attribute))
                {
                    final Object o = changeset.get(key);
                    final ModelNode node = Types.toDMR(o);
                    target.get(attribute).set(node);
                    matched = true;
                    break;
                }
            }

            if(!matched)
                throw new RuntimeException("Unmatched attribute name in changeset: "+key);
        }

    }

}
