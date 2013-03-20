package org.jboss.mbui.gui.behaviour.as7;

import org.jboss.mbui.gui.behaviour.InteractionCoordinator;
import org.jboss.mbui.gui.behaviour.ModelDrivenCommand;
import org.jboss.mbui.gui.behaviour.Procedure;
import org.jboss.mbui.model.Dialog;
import org.jboss.mbui.model.behaviour.Resource;
import org.jboss.mbui.model.behaviour.ResourceType;
import org.jboss.mbui.model.structure.QName;

/**
 * @author Heiko Braun
 * @date 2/26/13
 */
public class SelectStatementProcedure extends Procedure {

    public final static QName ID = QName.valueOf("org.jboss.as:select");
    private final static Resource<ResourceType> SELECT = new Resource<ResourceType>(ID, ResourceType.Statement);

    public SelectStatementProcedure(final InteractionCoordinator coordinator) {
        super(ID);
        this.coordinator = coordinator;


        setCommand(new ModelDrivenCommand() {
            @Override
            public void execute(Dialog dialog, Object data) {

                Tuple tuple = (Tuple)data;

                if(tuple.getValue()!=null)
                    coordinator.setStatement(tuple.getKey(), tuple.getValue());
                else
                    coordinator.clearStatement(tuple.getKey());

                // when statement change, the system will be reset
                coordinator.onReset();
            }
        });

        setInputs(SELECT);

    }



}
