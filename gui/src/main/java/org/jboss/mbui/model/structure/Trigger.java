package org.jboss.mbui.model.structure;

/**
 * The trigger element is used to specify a command from the user perspective.
 * This might be a function call or a navigation trigger.
 *
 * @author Heiko Braun
 * @date 1/16/13
 */
public class Trigger extends InteractionUnit {

    protected Trigger(String namespace, String id) {
        super(namespace, id);
    }

    @Override
    public String toString()
    {
        return "Trigger {" + getId() + '}';
    }
}
