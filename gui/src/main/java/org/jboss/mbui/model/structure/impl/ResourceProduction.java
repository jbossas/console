package org.jboss.mbui.model.structure.impl;

import org.jboss.mbui.model.behaviour.Producer;
import org.jboss.mbui.model.behaviour.Resource;
import org.jboss.mbui.model.behaviour.ResourceType;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Heiko Braun
 * @date 10/31/12
 */
public class ResourceProduction implements Producer {

    private Set<Resource<ResourceType>> producedTypes;


    public ResourceProduction() {

    }

    public boolean doesProduce()
    {
        return producedTypes!=null && !producedTypes.isEmpty();
    }

    @Override
    public boolean doesProduce(Resource<ResourceType> resource) {
        boolean match = false;

        if(producedTypes!=null)
        {
            for(Resource<ResourceType> candidate : producedTypes)
            {
                if(candidate.equals(resource))
                {
                    match = true;
                    break;
                }
            }
        }
        return match;
    }

    @Override
    public void setOutputs(Resource<ResourceType>... resources) {
        this.producedTypes = new HashSet<Resource<ResourceType>>();
        for(Resource<ResourceType> event : resources)
            this.producedTypes.add(event);
    }

    @Override
    public Set<Resource<ResourceType>> getOutputs() {
        return producedTypes;
    }
}
