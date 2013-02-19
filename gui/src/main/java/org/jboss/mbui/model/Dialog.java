package org.jboss.mbui.model;

import org.jboss.mbui.model.behaviour.BehaviourResolution;
import org.jboss.mbui.model.structure.Container;
import org.jboss.mbui.model.structure.InteractionUnit;
import org.jboss.mbui.model.structure.QName;
import org.jboss.mbui.model.structure.impl.InteractionUnitVisitor;

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
    //private BehaviourResolution behaviour;

    public Dialog(QName id, InteractionUnit root) {
        this.id = id;
        this.root = root;
    }

    public QName getId() {
        return id;
    }

    public InteractionUnit getInterfaceModel() {
        return root;
    }

    /*public void setBehaviour(BehaviourResolution behaviour) {
        this.behaviour = behaviour;
    }

    public BehaviourResolution getBehaviour() {
        return behaviour;
    } */

    public InteractionUnit findUnit(final QName id) {

        final Result result = new Result();

        InteractionUnitVisitor findById = new InteractionUnitVisitor() {

            @Override
            public void startVisit(Container container) {
                if (container.getId().equals(id))
                    result.setUnit(container);
            }

            @Override
            public void visit(InteractionUnit interactionUnit) {
                if (interactionUnit.getId().equals(id))
                    result.setUnit(interactionUnit);
            }

            @Override
            public void endVisit(Container container) {

            }
        };

        root.accept(findById);

        if(null==result.getUnit())
            System.out.println("No interaction unit with id "+ id);

        return result.getUnit();
    }

    class Result {
        InteractionUnit unit;

        public InteractionUnit getUnit() {
            return unit;
        }

        public void setUnit(InteractionUnit unit) {
            this.unit = unit;
        }
    }

}
