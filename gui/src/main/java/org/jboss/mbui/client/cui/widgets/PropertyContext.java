package org.jboss.mbui.client.cui.widgets;

import org.jboss.dmr.client.ModelType;

/**
 * @author Heiko Braun
 * @date 11/12/12
 */
public class PropertyContext {

    private ModelType type;

    public PropertyContext(ModelType type) {
        this.type = type;
    }

    public ModelType getType() {
        return type;
    }
}
