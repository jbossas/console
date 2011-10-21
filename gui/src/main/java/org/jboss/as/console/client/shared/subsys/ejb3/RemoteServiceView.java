package org.jboss.as.console.client.shared.subsys.ejb3;

import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.ejb3.model.RemoteService;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;

public class RemoteServiceView extends AbstractThreadPoolView<RemoteService> {
    protected RemoteServiceView(PropertyMetaData propertyMetaData,
            DispatchAsync dispatcher) {
        super(RemoteService.class, propertyMetaData, dispatcher);
    }

    @Override
    protected String getEntityDisplayName() {
        return "Remote Service";
    }
}
