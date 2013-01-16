package org.jboss.mbui.model.behaviour;

import org.jboss.mbui.model.structure.QName;

/**
 * Call of backend functions
 *
 * @author Heiko Braun
 * @date 10/31/12
 */
public class Call extends Transition {

    protected Call(QName id) {
        super(id);
    }
}
