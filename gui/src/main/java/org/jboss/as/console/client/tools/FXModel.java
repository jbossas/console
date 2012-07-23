package org.jboss.as.console.client.tools;

import org.jboss.dmr.client.ModelNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Heiko Braun
 * @date 7/23/12
 */
public class FXModel {

    public enum ExecutionType {
        CREATE, UPDATE, DELETE
    }

    private ExecutionType type;

    private ModelNode address;

    private Set<String> fieldNames = new HashSet<String>();

    private int id;

    public FXModel(ExecutionType type, ModelNode address) {
        this.type = type;
        this.address = address;
        this.id = address.toString().hashCode();
    }

    public FXModel(ExecutionType type, ModelNode address, String... fieldNames) {
        this(type, address);

        for(String fieldName : fieldNames)
            this.fieldNames.add(fieldName);
    }

    public ExecutionType getType() {
        return type;
    }

    public ModelNode getAddress() {
        return address;
    }

    public Set<String> getFieldNames() {
        return fieldNames;
    }

    public int getId() {
        return id;
    }

    public static FXModel fromBase64(String encoded) {
        final ModelNode modelNode = ModelNode.fromBase64(encoded);
        return fromModelNode(modelNode);
    }

    public String toBase64() {
        return asModelNode().toBase64String();
    }

    public ModelNode asModelNode() {
        ModelNode modelNode = new ModelNode();
        modelNode.get("execType").set(type.name());
        modelNode.get("address").set(address);
        modelNode.get("fieldNames").setEmptyList();
        for(String name : fieldNames)
            modelNode.get("fieldNames").add(name);

        return modelNode;
    }

    public static FXModel fromModelNode(ModelNode modelNode) {

        final String type = modelNode.get("execType").asString();
        final ModelNode address = modelNode.get("address").asObject();

        final List<ModelNode> fieldNames = modelNode.get("fieldNames").asList();
        List<String> values = new ArrayList<String>();
        for(ModelNode name : fieldNames)
            values.add(name.asString());

        final FXModel fxModel = new FXModel(ExecutionType.valueOf(type), address);
        fxModel.getFieldNames().addAll(values);
        return fxModel;
    }

}
