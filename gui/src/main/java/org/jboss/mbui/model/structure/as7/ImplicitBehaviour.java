package org.jboss.mbui.model.structure.as7;

import org.jboss.mbui.model.structure.InteractionUnit;
import org.jboss.mbui.model.behaviour.Trigger;
import org.jboss.mbui.model.behaviour.TriggerType;

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

        Trigger<TriggerType> reset = new Trigger<TriggerType>(PROJECT_NAMESPACE, "reset", TriggerType.System);
        Trigger<TriggerType> save = new Trigger<TriggerType>(PROJECT_NAMESPACE, "save", TriggerType.Interaction);

        unit.setInputs(reset);
        unit.setOutputs(save);
    }
}
