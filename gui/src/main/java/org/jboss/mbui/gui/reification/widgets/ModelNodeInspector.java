package org.jboss.mbui.gui.reification.widgets;

import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

/**
 * @author Heiko Braun
 * @date 11/12/12
 */
public class ModelNodeInspector {

    private ModelNode bean;
    private InspectionContext context;

    public ModelNodeInspector(ModelNode bean) {
        this.bean = bean;
    }

    public void accept(ModelNodeVisitor visitor)
    {
        this.context = new InspectionContext();

        visitor.visit(bean, context);

        for(Property prop : bean.asPropertyList()) {
            PropertyContext propCtx = new PropertyContext(prop.getValue().getType());

            switch (propCtx.getType())
            {
                case OBJECT:
                    visitor.visitReferenceProperty(prop.getName(), prop.getValue(), propCtx);
                    visitor.endVisitReferenceProperty(prop.getName(), prop.getValue(), propCtx);
                    break;
                default:
                    visitor.visitValueProperty(prop.getName(), prop.getValue(), propCtx);
                    visitor.endVisitValueProperty(prop.getName(), prop.getValue(), propCtx);
                    break;
            }

        }

        visitor.endVisit(bean, context);
    }
}
