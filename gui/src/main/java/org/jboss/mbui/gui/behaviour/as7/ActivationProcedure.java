package org.jboss.mbui.gui.behaviour.as7;

import org.jboss.mbui.gui.behaviour.InteractionCoordinator;
import org.jboss.mbui.gui.behaviour.ModelDrivenCommand;
import org.jboss.mbui.gui.behaviour.Procedure;
import org.jboss.mbui.gui.behaviour.SystemEvent;
import org.jboss.mbui.model.Dialog;
import org.jboss.mbui.model.behaviour.Resource;
import org.jboss.mbui.model.behaviour.ResourceType;
import org.jboss.mbui.model.structure.QName;

/**
 * @author Heiko Braun
 * @date 2/26/13
 */
public class ActivationProcedure extends Procedure {

    public final static QName ID = QName.valueOf("org.jboss.as:activate");
    Resource<ResourceType> activation = new Resource<ResourceType>(SystemEvent.ACTIVATE_ID, ResourceType.System);

    public ActivationProcedure(final InteractionCoordinator coordinator) {
        super(ID);
        this.coordinator = coordinator;


        setCommand(new ModelDrivenCommand() {
            @Override
            public void execute(Dialog dialog, Object data) {
                // activate target unit

                QName targetUnit = (QName)data;
                System.out.println("activate "+targetUnit);
                SystemEvent activationEvent = new SystemEvent(SystemEvent.ACTIVATE_ID);
                activationEvent.setPayload(targetUnit);

                coordinator.fireEvent(activationEvent);

            }
        });

        // complement model
        setOutputs(activation);

    }



}
