package org.jboss.mbui.gui.behaviour.as7;

import org.jboss.mbui.model.behaviour.Behaviour;
import org.jboss.mbui.model.structure.QName;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Heiko Braun
 * @date 2/22/13
 */
public class BehaviourMap<T extends Behaviour> extends HashMap<QName, Set<T>> {


    public void add(T behaviour) {
        Set<T> collection = get(behaviour.getId());
        if(null==collection)
        {
            collection = new HashSet<T>();
            put(behaviour.getId(), collection);
        }

        // Some procedures share the same ID, but are further distinguished (i.e by origin)
        // We need to check if they are equal and prevent registration of
        // multiple procedures that are of the kind AND discriminator.

        for(T existing : collection)
        {
            if(existing.getJustification()!=null
                    && existing.getJustification().equals(behaviour))
            {
                throw new RuntimeException("Behaviour already registered:"+ behaviour);
            }
        }

        collection.add(behaviour);
    }

    public Map<QName, Set<T>> list()
    {
        return Collections.unmodifiableMap(this);
    }
}
