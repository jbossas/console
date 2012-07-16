package org.jboss.as.console.client.tools.mapping;

import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 7/16/12
 */
public class DescriptionMapper {

    private ModelNode description;
    private ModelNode address;

    public DescriptionMapper(ModelNode address, ModelNode description) {
        this.address = address;
        this.description = description;
    }

    public interface Mapping {
        void onAttribute(String name, String description, String type, boolean required);
        void onOperation(String name, String description, List<RequestParameter> parameter, ResponseParameter response);
        void onChild(String name, String description);

        void onBegin();
        void onFinish();
    }

    public void map(Mapping mapping) {

        mapping.onBegin();

        if(description.hasDefined("attributes"))
        {

            final List<Property> properties = description.get("attributes").asPropertyList();

            if(!properties.isEmpty())
            {

                for(Property att : properties)
                {
                    final String name = att.getName();
                    final String description = att.getValue().get("description").asString();
                    final String type = att.getValue().get("type").asString();

                    final boolean required = att.getValue().hasDefined("required") ?
                            att.getValue().get("required").asBoolean() : false;

                    mapping.onAttribute(name, description, type, required);
                }

            }
        }


        if(description.hasDefined("operations"))
        {

            final List<Property> operations = description.get("operations").asPropertyList();

            if(!operations.isEmpty())
            {

                for(Property op : operations)
                {
                    final String opName = op.getName();
                    final String opDesc = op.getValue().get("description").asString();


                    List<RequestParameter> parameters = new LinkedList<RequestParameter>();
                    ResponseParameter response = null;

                    // parameters
                    if(op.getValue().hasDefined("request-properties"))
                    {
                        for(Property param : op.getValue().get("request-properties").asPropertyList())
                        {
                            final ModelNode value = param.getValue();
                            final String paramDesc = value.get("description").asString();
                            final String paramName = param.getName();
                            final String paramType = value.get("type").asString();
                            boolean required = false;
                            if(value.hasDefined("required"))
                            {
                                required = value.get("required").asBoolean();
                            }

                            parameters.add(
                                    new RequestParameter(
                                            paramDesc, paramName, paramType, required
                                    )
                            );
                        }
                    }

                    // response
                    if(op.getValue().hasDefined("reply-properties"))
                    {
                        final ModelNode reply = op.getValue().get("reply-properties");
                        final String replyDesc = reply.get("description").asString();
                        final String replyType = reply.get("type").asString();

                        response = new ResponseParameter(replyDesc, replyType);
                    }


                    mapping.onOperation(opName, opDesc, parameters, response);
                }

            }
        }

        if(description.hasDefined("children"))
        {
            final List<Property> children = description.get("children").asPropertyList();

            if(!children.isEmpty())
            {

                for(Property child : children)
                {
                    final String childName = child.getName();
                    final String childDesc = child.getValue().get("description").asString();
                    mapping.onChild(childName, childDesc);
                }

            }
        }

        mapping.onFinish();
    }
}
