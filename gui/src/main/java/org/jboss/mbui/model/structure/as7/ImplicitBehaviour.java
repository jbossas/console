package org.jboss.mbui.model.structure.as7;

import org.jboss.mbui.model.behaviour.ResourceType;
import org.jboss.mbui.model.structure.InteractionUnit;
import org.jboss.mbui.model.behaviour.Resource;

/**
 * @author Heiko Braun
 * @date 11/20/12
 */
public class ImplicitBehaviour {

    private static final String PROJECT_NAMESPACE = "org.jboss.as";

    public static void attach(InteractionUnit unit)
    {
        if(unit instanceof Form)
        {
            attachFormBehaviour(unit);
        }
    }

    private static void attachFormBehaviour(InteractionUnit unit) {

        Resource<ResourceType> reset = new Resource<ResourceType>(PROJECT_NAMESPACE, "reset", ResourceType.System);
        Resource<ResourceType> save = new Resource<ResourceType>(PROJECT_NAMESPACE, "save", ResourceType.Interaction);

        unit.setInputs(reset);
        unit.setOutputs(save);
    }
}
