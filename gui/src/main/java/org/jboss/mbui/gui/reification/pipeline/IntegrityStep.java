package org.jboss.mbui.gui.reification.pipeline;

import com.allen_sauer.gwt.log.client.Log;
import org.jboss.mbui.gui.behaviour.Integrity;
import org.jboss.mbui.gui.behaviour.IntegrityErrors;
import org.jboss.mbui.gui.behaviour.InteractionCoordinator;
import org.jboss.mbui.gui.reification.Context;
import org.jboss.mbui.gui.reification.ContextKey;
import org.jboss.mbui.gui.reification.ReificationException;
import org.jboss.mbui.model.Dialog;

/**
 * @author Harald Pehl
 * @date 02/22/2013
 */
public class IntegrityStep extends ReificationStep
{
    public IntegrityStep()
    {
        super("integrity check");
    }

    @Override
    public void execute(final Dialog dialog, final Context context) throws ReificationException
    {
        InteractionCoordinator coordinator = context.get(ContextKey.COORDINATOR);
        try
        {
            // Step 3: Verify integrity
            Integrity.check(
                    dialog.getInterfaceModel(),
                    coordinator.listProcedures()
            );
        }
        catch (IntegrityErrors integrityErrors)
        {

            if (integrityErrors.needsToBeRaised())
            {
                Log.error(integrityErrors.getMessage());
                //                throw new RuntimeException("Integrity check failed", integrityErrors);
            }
        }
    }
}
