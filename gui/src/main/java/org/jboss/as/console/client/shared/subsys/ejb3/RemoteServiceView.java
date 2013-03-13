package org.jboss.as.console.client.shared.subsys.ejb3;

import org.jboss.as.console.client.Console;
import org.jboss.dmr.client.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.ejb3.model.RemoteService;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;

public class RemoteServiceView extends AbstractThreadPoolView<RemoteService> {
    protected RemoteServiceView(ApplicationMetaData propertyMetaData,
            DispatchAsync dispatcher) {
        super(RemoteService.class, propertyMetaData, dispatcher);
    }

    @Override
    protected String getEntityDisplayName() {
        return Console.CONSTANTS.subsys_ejb3_remoteService();
    }
}
