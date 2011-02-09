package org.jboss.as.console.client.server;

import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.components.AbstractToolsetView;
import org.jboss.as.console.client.components.NavigationItem;
import org.jboss.as.console.client.components.NavigationSection;
import org.jboss.as.console.client.components.ViewName;
import org.jboss.as.console.client.util.message.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/4/11
 */
public class ServerMgmtViewImpl extends ViewImpl
        implements ServerMgmtApplicationPresenter.ServerManagementView{

    AbstractToolsetView delegate;
    Widget delegateWidget = null;

    public ServerMgmtViewImpl() {
        super();
        this.delegate = createToolset();
    }

    @Override
    public Widget asWidget() {
        if(null==delegateWidget)
            delegateWidget = delegate.asWidget();
        return delegateWidget;
    }

    @Override
    public void setInSlot(Object slot, Widget content) {

        if (slot == ServerMgmtApplicationPresenter.TYPE_SetToolContent) {
            if(content!=null)
                delegate.setContent(content);

        } else {
            Console.MODULES.getMessageCenter().notify(
                    new Message("Unknown slot requested:" + slot)
            );
        }
    }

    private static AbstractToolsetView createToolset() {

        AbstractToolsetView toolset = new AbstractToolsetView("config") {

            @Override
            protected List<NavigationSection> getNavigationSections()
            {
                final ArrayList<NavigationSection> sections = new ArrayList<NavigationSection>();

                final NavigationSection serverConfig= new NavigationSection(
                        new ViewName("common", "Common Settings"),
                        new NavigationItem(new ViewName("path","Path"), "",null),
                        new NavigationItem(new ViewName("interfaces","Interfaces"), "",null),
                        new NavigationItem(new ViewName("sockets","Sockets"), "",null),
                        new NavigationItem(new ViewName("properties","Properties"), "",null)
                );

                 final NavigationSection subsystems= new NavigationSection(
                        new ViewName("config", "Subsystem Configuration"),
                        new NavigationItem(new ViewName("subsys;name=threads","Threading"), "",null)
                );

                final NavigationSection deployments = new NavigationSection(
                        new ViewName("deployments", "Deployments"),
                        new NavigationItem(new ViewName("deployments","Deployments"), "", null)
                );

                sections.add(serverConfig);
                sections.add(subsystems);
                sections.add(deployments);
                return sections;
            };
        };

        return toolset;
    }
}
