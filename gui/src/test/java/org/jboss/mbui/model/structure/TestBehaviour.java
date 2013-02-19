package org.jboss.mbui.model.structure;

import org.jboss.mbui.model.behaviour.Behaviour;
import org.jboss.mbui.model.behaviour.BehaviourResolution;
import org.jboss.mbui.model.structure.QName;

import java.util.HashMap;
import java.util.Map;

public class TestBehaviour implements BehaviourResolution
{
    private Map<QName, Behaviour> registry = new HashMap<QName, Behaviour>();

    public void register(Behaviour behaviour)
    {
        registry.put(behaviour.getId(), behaviour);
    }

    @Override
    public Behaviour resolve(QName id) {
        return registry.get(id);
    }
}