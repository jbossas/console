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
public class NavigationProcedure extends Procedure {

    public final static QName ID = QName.valueOf("org.jboss.as:navigate");
    Resource<ResourceType> navigation = new Resource<ResourceType>(ID, ResourceType.Event);

    public NavigationProcedure(final InteractionCoordinator coordinator) {
        super(ID);
        this.coordinator = coordinator;


        setCommand(new ModelDrivenCommand() {
            @Override
            public void execute(Dialog dialog, Object data) {
                // activate target unit

                QName targetUnit = (QName)data;
                System.out.println("navigate "+targetUnit);


            }
        });

        // complement model
        setInputs(navigation);

    }



}
