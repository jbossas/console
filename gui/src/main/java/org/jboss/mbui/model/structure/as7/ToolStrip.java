package org.jboss.mbui.model.structure.as7;

import org.jboss.mbui.model.structure.Container;

import static org.jboss.mbui.model.structure.TemporalOperator.Concurrency;

/**
 * @author Heiko Braun
 * @date 2/26/13
 */
public class ToolStrip extends Container {
    public ToolStrip(String namespace, String id, String name) {
        super(namespace, id, name, Concurrency);
    }

    @Override
    public String toString()
    {
        return "ToolStrip{" + getId() + '}';
    }
}
