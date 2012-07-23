package org.jboss.as.console.client.tools;

import com.google.gwt.dom.client.Document;
import org.jboss.dmr.client.ModelNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 7/23/12
 */
public class FXTemplate {
    private String name;
    private String id;
    private List<FXModel> models = new LinkedList<FXModel>();

    public FXTemplate(String name, String id, FXModel... models) {
        this.name = name;
        this.id = id;
        for(FXModel model : models)
            this.models.add(model);
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public List<FXModel> getModels() {
        return models;
    }

    public static FXTemplate fromBase64(String encoded) {

        ModelNode modelNode = ModelNode.fromBase64(encoded);
        final String name = modelNode.get("name").asString();
        final String id = modelNode.get("id").asString();

        final List<ModelNode> nodeList = modelNode.get("models").asList();
        List<FXModel> values = new ArrayList<FXModel>(nodeList.size());

        for(ModelNode node : nodeList)
        {
            values.add(FXModel.fromModelNode(node));
        }

        FXTemplate template = new FXTemplate(name, id);
        template.getModels().addAll(values);

        return template;
    }

    public String toBase64() {
        return asModelNode().toBase64String();
    }

    public ModelNode asModelNode() {
        ModelNode modelNode = new ModelNode();
        modelNode.get("name").set(name);
        modelNode.get("id").set(id);
        modelNode.get("models").setEmptyList();
        for(FXModel model : models)
            modelNode.get("models").add(model.asModelNode());

        return modelNode;
    }
}
