package org.jboss.as.console.client.shared.runtime.env;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.general.EnvironmentProperties;
import org.jboss.as.console.client.shared.properties.PropertyRecord;

import java.util.List;

/**
 * Created with IntelliJ IDEA. User: pehlh Date: 15.10.12 Time: 17:04 To change this template use File | Settings | File
 * Templates.
 */
public class EnvironmentView extends SuspendableViewImpl
{
    private EnvironmentProperties properties;

    @Override
    public Widget createWidget()
    {
        properties = new EnvironmentProperties();

        return properties.asWidget();
    }

    public void setEnvironment(final List<PropertyRecord> environment)
    {
        properties.setProperties(environment);
    }

    public void clearEnvironment()
    {
        properties.clearValues();
    }
}
