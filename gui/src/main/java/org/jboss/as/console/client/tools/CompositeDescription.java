package org.jboss.as.console.client.tools;

import org.jboss.dmr.client.ModelNode;

import java.util.Collections;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 6/18/12
 */
public class CompositeDescription {

    private ModelNode description;
    private List<ModelNode> childNames = Collections.EMPTY_LIST;
    private ModelNode address;

    public CompositeDescription() {
    }

    public ModelNode getAddress() {
        return address;
    }

    public void setAddress(ModelNode address) {
        this.address = address;
    }

    public ModelNode getDescription() {
        return description;
    }

    public List<ModelNode> getChildNames() {
        return childNames;
    }

    public void setDescription(ModelNode description) {
        this.description = description;
    }

    public void setChildNames(List<ModelNode> childNames) {
        this.childNames = childNames;
    }
}
