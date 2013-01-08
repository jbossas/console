package org.jboss.as.console.client.mbui.cui.widgets;

import org.jboss.dmr.client.ModelNode;

/**
 * @author Heiko Braun
 * @date 11/12/12
 */
public class ModelNodeVisitor {

    public void visit(ModelNode bean, InspectionContext ctx) {};

    public void endVisit(ModelNode bean, InspectionContext ctx){};

    public boolean visitValueProperty(
            final String propertyName,
            final ModelNode value,
            PropertyContext ctx
    )
    {
        return true;
    }

    public boolean endVisitValueProperty(
            final String propertyName,
            final ModelNode value,
            PropertyContext ctx
    )
    {
        return true;
    }


    public boolean visitReferenceProperty(
            String propertyName,
            ModelNode value,
            PropertyContext ctx)
    {
        return true;
    }

    public void endVisitReferenceProperty(
            String propertyName,
            ModelNode value,
            PropertyContext ctx
    )
    {

    }

    public boolean visitCollectionProperty(
            String propertyName,
            final ModelNode value,
            PropertyContext ctx)
    {
        return true;
    }

    public boolean endVisitCollectionProperty(
            String propertyName,
            final ModelNode value,
            PropertyContext ctx)
    {
        return true;
    }


}
