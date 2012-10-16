package org.jboss.as.console.client.shared.runtime.env;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.general.EnvironmentProperties;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

/**
 * Created with IntelliJ IDEA. User: pehlh Date: 15.10.12 Time: 17:04 To change this template use File | Settings | File
 * Templates.
 */
public class EnvironmentPropertiesView extends SuspendableViewImpl implements EnvironmentPropertiesPresenter.MyView
{
    private EnvironmentPropertiesPresenter presenter;
    private EnvironmentProperties properties;

    @Override
    public Widget createWidget()
    {
        final ToolStrip toolStrip = new ToolStrip();
        toolStrip.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_refresh(), new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {

            }
        }));

        properties = new EnvironmentProperties();
        SimpleLayout layout = new SimpleLayout()
                .setTitle("Web")
                .setTopLevelTools(toolStrip.asWidget())
                .setHeadline("Environment Properties")
                .addContent("Environment Properties", properties.asWidget());

        return layout.build();
    }

    @Override
    public void setPresenter(final EnvironmentPropertiesPresenter presenter)
    {
        this.presenter = presenter;
    }
}
