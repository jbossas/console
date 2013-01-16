package org.jboss.mbui.model.structure;

import org.jboss.mbui.model.behaviour.Behaviour;

import java.util.HashSet;
import java.util.Set;

/**
 * A dialog contains a set of hierarchically structured abstract interaction objects,
 * which enable the execution of an interactive task.
 *
 * @author Heiko Braun
 * @date 1/16/13
 */
public class Dialog {
    private QName id;
    private InteractionUnit root;
    private Set<Behaviour> behaviours = new HashSet<Behaviour>();

    public Dialog(InteractionUnit root) {
        this.root = root;
    }

    public Set<Behaviour> getBehaviours() {
        return behaviours;
    }
}
