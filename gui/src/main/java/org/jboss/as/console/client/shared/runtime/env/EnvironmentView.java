package org.jboss.as.console.client.shared.runtime.env;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.general.EnvironmentProperties;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;

import java.util.List;

/**
 * Created with IntelliJ IDEA. User: pehlh Date: 15.10.12 Time: 17:04 To change this template use File | Settings | File
 * Templates.
 */
public class EnvironmentView extends SuspendableViewImpl implements EnvironmentPresenter.MyView
{
    private EnvironmentPresenter presenter;
    private EnvironmentProperties properties;

    @Override
    public Widget createWidget()
    {
        properties = new EnvironmentProperties();

        return properties.asWidget();
    }

    @Override
    public void setPresenter(final EnvironmentPresenter environmentPresenter)
    {
        this.presenter = environmentPresenter;
    }

    @Override
    public void setEnvironment(final List<PropertyRecord> environment)
    {
        properties.setProperties(environment);
    }

    @Override
    public void clearEnvironment()
    {
        properties.clearValues();
    }
}
