package org.jboss.mbui.gui.behaviour.as7;

import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.mbui.gui.behaviour.InteractionCoordinator;
import org.jboss.mbui.gui.behaviour.StatementContext;
import org.jboss.mbui.model.Dialog;
import org.jboss.mbui.model.mapping.as7.AddressMapping;
import org.jboss.mbui.model.structure.InteractionUnit;

/**
 * @author Heiko Braun
 * @date 3/6/13
 */
public interface OperationContext {

    Dialog getDialog();
    InteractionUnit getUnit();
    AddressMapping getAddress();
    DispatchAsync getDispatcher();

    StatementContext getStatementContext();

    InteractionCoordinator getCoordinator();
}
