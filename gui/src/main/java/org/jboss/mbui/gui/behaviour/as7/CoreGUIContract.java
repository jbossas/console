package org.jboss.mbui.gui.behaviour.as7;

import org.jboss.as.console.client.Console;
import org.jboss.dmr.client.dispatch.DispatchAsync;

/**
 * @author Heiko Braun
 * @date 2/22/13
 */
public class CoreGUIContract implements ProcedureContract {
    @Override
    public DispatchAsync getDispatcher() {
        return Console.MODULES.getDispatchAsync();
    }
}
