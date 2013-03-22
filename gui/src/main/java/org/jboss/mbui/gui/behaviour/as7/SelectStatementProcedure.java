package org.jboss.mbui.gui.behaviour.as7;

import org.jboss.mbui.gui.behaviour.InteractionCoordinator;
import org.jboss.mbui.gui.behaviour.ModelDrivenCommand;
import org.jboss.mbui.gui.behaviour.Procedure;
import org.jboss.mbui.gui.behaviour.StatementEvent;
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

                StatementEvent event = (StatementEvent)data;

                QName sourceId = (QName)event.getSource();
                String key = event.getKey();
                String value = event.getValue();


                if(value!=null)
                    coordinator.setStatement(sourceId, key, value);
                else
                    coordinator.clearStatement(sourceId, key, value);

                // when statement change, the system will be reset
                coordinator.reset();
            }
        });

        setInputs(SELECT);

    }



}
