package org.jboss.mbui.gui.behaviour.as7;

import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.mbui.gui.behaviour.Procedure;

/**
 * Contract between {@link Procedure}'s and business logic components
 * @author Heiko Braun
 * @date 2/22/13
 */
public interface ProcedureContract {

    DispatchAsync getDispatcher();
}
